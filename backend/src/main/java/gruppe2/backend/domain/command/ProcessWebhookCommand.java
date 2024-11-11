package gruppe2.backend.domain.command;

import gruppe2.backend.service.webhook.WebhookPayload;
import gruppe2.backend.domain.*;
import gruppe2.backend.domain.webhook.WebhookOrder;
import gruppe2.backend.domain.exception.WebhookProcessingException;
import gruppe2.backend.mapper.OrderMapper;
import gruppe2.backend.mapper.WebhookMapper;
import gruppe2.backend.model.Item;
import gruppe2.backend.model.ProductType;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.repository.*;
import gruppe2.backend.service.OrderService;
import gruppe2.backend.dto.ItemDTO;
import gruppe2.backend.service.ItemService;
import java.time.LocalDateTime;
import java.util.*;

public class ProcessWebhookCommand {
    private final WebhookPayload payload;
    private final OrderService orderService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final ProductTypeRepository productTypeRepository;
    private final OrderProductTypeRepository orderProductTypeRepository;
    private final WebhookMapper webhookMapper;
    private final OrderMapper orderMapper;

    public ProcessWebhookCommand(
            WebhookPayload payload,
            OrderService orderService,
            ItemService itemService,
            ItemRepository itemRepository,
            ProductTypeRepository productTypeRepository,
            OrderProductTypeRepository orderProductTypeRepository,
            WebhookMapper webhookMapper,
            OrderMapper orderMapper) {
        this.payload = payload;
        this.orderService = orderService;
        this.itemService = itemService;
        this.itemRepository = itemRepository;
        this.productTypeRepository = productTypeRepository;
        this.orderProductTypeRepository = orderProductTypeRepository;
        this.webhookMapper = webhookMapper;
        this.orderMapper = orderMapper;
    }

    public void execute() {
        try {
            // Convert webhook payload to domain objects using mapper
            CustomerInfo customerInfo = webhookMapper.toCustomerInfo(payload);
            Map<Long, Integer> items = createItemsMap();
            Map<Long, Integer> processingTimes = getProcessingTimes(items.keySet());

            // Create order through factory
            Order order = OrderFactory.createOrder(
                new OrderId(payload.getId()),
                customerInfo,
                items,
                processingTimes,
                false // Default priority
            );

            // Persist order using OrderService
            gruppe2.backend.model.Order persistedOrder = orderService.createOrder(orderMapper.toOrderDTO(order));

            // Setup order details using command
            SetupOrderDetailsCommand setupCommand = new SetupOrderDetailsCommand(
                persistedOrder.getId(),
                items,
                itemRepository,
                productTypeRepository,
                orderProductTypeRepository
            );
            setupCommand.execute();
            
        } catch (Exception e) {
            throw new WebhookProcessingException(
                payload.getId(),
                "Failed to process webhook payload",
                e
            );
        }
    }

    private Map<Long, Integer> createItemsMap() {
        Map<Long, Integer> itemsMap = new HashMap<>();
        payload.getItems().forEach(item -> {
            ensureItemExists(item);
            itemsMap.put(item.getProduct_id(), item.getQuantity());
        });
        return itemsMap;
    }

    private void ensureItemExists(gruppe2.backend.service.webhook.LineItem item) {
        if (!itemRepository.existsById(item.getProduct_id())) {
            ItemDTO itemDTO = new ItemDTO(
                item.getName(),
                item.getProduct_id(),
                0L, // Generic product type
                item.getImg().getSrc()
            );
            
            try {
                itemService.createItem(itemDTO);
            } catch (Exception e) {
                throw new WebhookProcessingException(
                    item.getProduct_id(),
                    "Failed to create new item",
                    e
                );
            }
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
                processingTimes.put(itemId, productType.getDifferentSteps().length * 10);
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
