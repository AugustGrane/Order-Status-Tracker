package gruppe2.backend.service;

import gruppe2.backend.service.webhook.LineItem;
import gruppe2.backend.service.webhook.WebhookPayload;
import gruppe2.backend.domain.*;
import gruppe2.backend.domain.webhook.WebhookOrder;
import gruppe2.backend.domain.exception.WebhookProcessingException;
import gruppe2.backend.dto.ItemDTO;
import gruppe2.backend.dto.OrderDTO;
import gruppe2.backend.model.Item;
import gruppe2.backend.model.ProductType;
import gruppe2.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class WebhookService {
    private final OrderService orderService;
    private final ItemService itemService;
    private final ProductTypeService productTypeService;
    private final ItemRepository itemRepository;
    private final ProductTypeRepository productTypeRepository;

    public WebhookService(
            OrderService orderService,
            ItemService itemService,
            ProductTypeService productTypeService,
            ItemRepository itemRepository,
            ProductTypeRepository productTypeRepository) {
        this.orderService = orderService;
        this.itemService = itemService;
        this.productTypeService = productTypeService;
        this.itemRepository = itemRepository;
        this.productTypeRepository = productTypeRepository;
    }

    @Transactional
    public void createOrderInDatabase(WebhookPayload payload) {
        try {
            // Convert webhook payload to domain object
            WebhookOrder webhookOrder = WebhookOrder.fromPayload(payload);
            
            // Ensure all items exist
            ensureItemsExist(webhookOrder, payload.getItems());
            
            // Create order using domain model
            createOrderFromWebhook(webhookOrder);
            
        } catch (Exception e) {
            throw new WebhookProcessingException(
                payload.getId(),
                "Failed to process webhook payload",
                e
            );
        }
    }

    private void ensureItemsExist(WebhookOrder webhookOrder, List<LineItem> lineItems) {
        Map<Long, LineItem> lineItemMap = new HashMap<>();
        lineItems.forEach(item -> lineItemMap.put(item.getProduct_id(), item));

        webhookOrder.getItems().keySet().forEach(itemId -> {
            if (!itemRepository.existsById(itemId)) {
                LineItem lineItem = lineItemMap.get(itemId);
                if (lineItem == null) {
                    throw new WebhookProcessingException(
                        itemId,
                        "Unable to find line item information"
                    );
                }
                createNewItem(lineItem);
            }
        });
    }

    private void createNewItem(LineItem lineItem) {
        ItemDTO itemDTO = new ItemDTO(
            lineItem.getName(),
            lineItem.getProduct_id(),
            0L, // Generic product type
            lineItem.getImg().getSrc()
        );
        
        try {
            itemService.createItem(itemDTO);
        } catch (Exception e) {
            throw new WebhookProcessingException(
                lineItem.getProduct_id(),
                "Failed to create new item",
                e
            );
        }
    }

    private void createOrderFromWebhook(WebhookOrder webhookOrder) {
        // Create order timeline
        OrderTimeline timeline = new OrderTimeline(
            LocalDateTime.now(),
            webhookOrder.getCustomerInfo().isPriority()
        );
        
        // Create order estimation
        Map<Long, Integer> processingTimes = getProcessingTimes(webhookOrder.getItems().keySet());
        OrderEstimation estimation = new OrderEstimation(
            webhookOrder.getItems(),
            processingTimes,
            webhookOrder.getCustomerInfo().isPriority()
        );

        // Create order DTO
        OrderDTO orderDTO = new OrderDTO(
            webhookOrder.getOrderId(),
            webhookOrder.getCustomerInfo().getName(),
            webhookOrder.getCustomerInfo().isPriority(),
            webhookOrder.getCustomerInfo().getNotes(),
            webhookOrder.getItems(),
            ""
        );

        try {
            orderService.createOrder(orderDTO);
        } catch (Exception e) {
            throw new WebhookProcessingException(
                webhookOrder.getOrderId(),
                "Failed to create order",
                e
            );
        }
    }

    private Map<Long, Integer> getProcessingTimes(Set<Long> itemIds) {
        Map<Long, Integer> processingTimes = new HashMap<>();
        itemIds.forEach(itemId -> {
            try {
                Item item = itemRepository.findById(itemId)
                        .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
                ProductType productType = productTypeRepository.findById(item.getProductTypeId())
                        .orElseThrow(() -> new RuntimeException("Product type not found: " + item.getProductTypeId()));
                processingTimes.put(itemId, productType.getDifferentSteps().length * 10); // Base estimation
            } catch (Exception e) {
                throw new WebhookProcessingException(
                    itemId,
                    "Failed to calculate processing time",
                    e
                );
            }
        });
        return processingTimes;
    }
}
