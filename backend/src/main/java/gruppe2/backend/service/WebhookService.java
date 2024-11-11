package gruppe2.backend.service;

import gruppe2.backend.service.webhook.LineItem;
import gruppe2.backend.service.webhook.WebhookPayload;
import gruppe2.backend.domain.*;
import gruppe2.backend.domain.webhook.WebhookOrder;
import gruppe2.backend.domain.exception.WebhookProcessingException;
import gruppe2.backend.domain.command.*;
import gruppe2.backend.dto.ItemDTO;
import gruppe2.backend.model.Item;
import gruppe2.backend.model.ProductType;
import gruppe2.backend.model.OrderDetails;
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
    private final OrderProductTypeRepository orderProductTypeRepository;

    public WebhookService(
            OrderService orderService,
            ItemService itemService,
            ProductTypeService productTypeService,
            ItemRepository itemRepository,
            ProductTypeRepository productTypeRepository,
            OrderProductTypeRepository orderProductTypeRepository) {
        this.orderService = orderService;
        this.itemService = itemService;
        this.productTypeService = productTypeService;
        this.itemRepository = itemRepository;
        this.productTypeRepository = productTypeRepository;
        this.orderProductTypeRepository = orderProductTypeRepository;
    }

    @Transactional
    public void createOrderInDatabase(WebhookPayload payload) {
        try {
            // Convert webhook payload to domain objects
            CustomerInfo customerInfo = createCustomerInfo(payload);
            Map<Long, Integer> items = createItemsMap(payload);
            Map<Long, Integer> processingTimes = getProcessingTimes(items.keySet());

            // Create order through factory
            Order order = OrderFactory.createOrder(
                new OrderId(payload.getId()),
                customerInfo,
                items,
                processingTimes,
                false // Default priority
            );

            // Persist order and get the persisted entity
            gruppe2.backend.model.Order persistedOrder = orderService.createOrder(convertToOrderDTO(order));

            // Create OrderDetails for each item
            setupOrderDetails(persistedOrder.getId(), items);
            
        } catch (Exception e) {
            throw new WebhookProcessingException(
                payload.getId(),
                "Failed to process webhook payload",
                e
            );
        }
    }

    private void setupOrderDetails(Long orderId, Map<Long, Integer> items) {
        items.forEach((itemId, quantity) -> {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

            ProductType productType = productTypeRepository.findById(item.getProductTypeId())
                    .orElseThrow(() -> new RuntimeException("Product type not found: " + item.getProductTypeId()));

            // Create OrderDetails with initial status
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setOrderId(orderId);
            orderDetails.setItem(item);
            orderDetails.setProduct_type(productType.getName());
            orderDetails.setItemAmount(quantity);

            // Set up steps
            Long[] steps = Arrays.copyOf(productType.getDifferentSteps(),
                    productType.getDifferentSteps().length);
            orderDetails.setDifferentSteps(steps);
            orderDetails.setCurrentStepIndex(0);

            // Initialize status updates
            Map<Long, LocalDateTime> statusUpdates = new HashMap<>();
            statusUpdates.put(steps[0], LocalDateTime.now());
            orderDetails.setUpdated(statusUpdates);

            orderProductTypeRepository.save(orderDetails);
        });
    }

    private CustomerInfo createCustomerInfo(WebhookPayload payload) {
        String displayName;
        if (!Objects.equals(payload.getBilling().getCompany(), "")) {
            String companyName = payload.getBilling().getCompany();
            String name = payload.getBilling().getFirstName() + " " + payload.getBilling().getLastName();
            displayName = companyName + " | " + name;
        } else {
            displayName = payload.getBilling().getFirstName() + " " + payload.getBilling().getLastName();
        }

        return new CustomerInfo(
            displayName,
            "", // Notes
            false // Priority
        );
    }

    private Map<Long, Integer> createItemsMap(WebhookPayload payload) {
        Map<Long, Integer> itemsMap = new HashMap<>();
        for (LineItem item : payload.getItems()) {
            ensureItemExists(item);
            itemsMap.put(item.getProduct_id(), item.getQuantity());
        }
        return itemsMap;
    }

    private void ensureItemExists(LineItem item) {
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

    private gruppe2.backend.dto.OrderDTO convertToOrderDTO(Order order) {
        return new gruppe2.backend.dto.OrderDTO(
            order.getId().getValue(),
            order.getCustomerInfo().getName(),
            order.getCustomerInfo().isPriority(),
            order.getCustomerInfo().getNotes(),
            order.getItems().stream()
                .collect(HashMap::new, 
                    (m, item) -> m.put(item.getItem().getId(), item.getQuantity()),
                    HashMap::putAll),
            ""
        );
    }
}
