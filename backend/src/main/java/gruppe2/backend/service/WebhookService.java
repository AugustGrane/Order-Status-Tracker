package gruppe2.backend.service;

import gruppe2.backend.service.webhook.LineItem;
import gruppe2.backend.service.webhook.WebhookPayload;
import gruppe2.backend.dto.ItemDTO;
import gruppe2.backend.dto.OrderDTO;
import gruppe2.backend.model.Item;
import gruppe2.backend.model.Order;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.model.ProductType;
import gruppe2.backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class WebhookService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final ProductTypeRepository productTypeRepository;
    private final OrderProductTypeRepository orderProductTypeRepository;
    private final StatusDefinitionRepository statusDefinitionRepository;

    public WebhookService(
            OrderRepository orderRepository,
            ItemRepository itemRepository,
            ProductTypeRepository productTypeRepository,
            OrderProductTypeRepository orderProductTypeRepository,
            StatusDefinitionRepository statusDefinitionRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.productTypeRepository = productTypeRepository;
        this.orderProductTypeRepository = orderProductTypeRepository;
        this.statusDefinitionRepository = statusDefinitionRepository;
    }

    public void createOrderInDatabase(WebhookPayload payload) {

        // 1. Filter Webhook payload to orderDTO format
        OrderDTO orderDTO = createOrderDTOFromWebhookPayload(payload);
        System.out.println(orderDTO);

        // 2. Create the order
        Order order = new Order();
        order.setId(orderDTO.id());
        order.setCustomerName(orderDTO.customerName());
        order.setPriority(orderDTO.priority());
        order.setNotes(orderDTO.notes());
        order.setOrderCreated(LocalDateTime.now());

        // 3. Calculate total estimated time
        int totalTime = calculateEstimatedTime(orderDTO.items());
        order.setTotalEstimatedTime(totalTime);

        // Save the order first to get its ID
        order = orderRepository.save(order);
        final Long orderId = order.getId();

        // 4. For each item in the order, create an OrderDetails
        orderDTO.items().forEach((itemId, quantity) -> {
            // Find the item
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

            // Get the product type for this item
            ProductType productType = productTypeRepository.findById(item.getProductTypeId())
                    .orElseThrow(() -> new RuntimeException("Product type not found: " + item.getProductTypeId()));

            // Create OrderDetails
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setOrderId(orderId);
            orderDetails.setItem(item);  // Set the Item entity instead of just the ID
            orderDetails.setProduct_type(productType.getName());
            orderDetails.setItemAmount(quantity);

            // Create a new array instead of using the reference directly
            Long[] steps = Arrays.copyOf(productType.getDifferentSteps(),
                    productType.getDifferentSteps().length);
            orderDetails.setDifferentSteps(steps);

            // Set the initial step index
            orderDetails.setCurrentStepIndex(0);

            // Initialize the status updates map with first step
            Map<Long, LocalDateTime> statusUpdates = new HashMap<>();
            statusUpdates.put(steps[0], LocalDateTime.now());
            orderDetails.setUpdated(statusUpdates);

            orderProductTypeRepository.save(orderDetails);
        });
    }

    private OrderDTO createOrderDTOFromWebhookPayload(WebhookPayload payload) {
        // Extracting names from payload object - displayName depends on existance of company name
        String companyName, name, displayName;
        // Example: COWI | John Doe
        if (!Objects.equals(payload.getBilling().getCompany(), "")) {
            companyName = payload.getBilling().getCompany();
            name = payload.getBilling().getFirstName() + " " + payload.getBilling().getLastName();
            displayName = companyName + " | " + name;
        }
        // John Doe
        else {
            displayName = payload.getBilling().getFirstName() + " " + payload.getBilling().getLastName();
        }

        // Create itemsMap with all items in the payload:
        Map<Long, Integer> itemsMap = new HashMap<>();
        for (LineItem item : payload.getItems()) {
            itemsMap.put(item.getProduct_id(), item.getQuantity());
            checkItemExistence(item); // Checks if the items exists. Creates it otherwise
        }

        // Repackage to orderDTO
        OrderDTO orderDTO = new OrderDTO(
                payload.getId(),
                displayName,
                false,
                "",
                itemsMap,
                ""
        );

        System.out.println("OrderDTO" + orderDTO);

        // Return the payload filtered payload object to sender (for check in postman)
        return orderDTO;
    }


    public void checkItemExistence(LineItem item){
        Optional<Item> itemDB = itemRepository.findById(item.getProduct_id());

        if(itemDB.isEmpty()) {
            ItemDTO itemDTO = new ItemDTO(
                    item.getName(),
                    item.getProduct_id(),
                    1L,   // HARDCODED PRODUCT TYPE !!!!!! #####################################
                    item.getImg().getSrc()
            );
            createItem(itemDTO); // If item did not exist, create it.
        }
    }

    private int calculateEstimatedTime(Map<Long, Integer> items) {
        return items.entrySet().stream()
                .mapToInt(entry -> {
                    Item item = itemRepository.findById(entry.getKey())
                            .orElseThrow(() -> new RuntimeException("Item not found"));
                    // Get product type and calculate time based on steps
                    // This is just a placeholder implementation
                    return entry.getValue() * 10; // 10 minutes per item
                })
                .sum();
    }

    public ResponseEntity<Item> createItem(ItemDTO itemDTO) {
        // Verify product type exists
        productTypeRepository.findById(itemDTO.productTypeId())
                .orElseThrow(() -> new RuntimeException("Product type not found"));

        Item item = new Item();
        item.setId(itemDTO.id());
        item.setName(itemDTO.name());
        item.setProductTypeId(itemDTO.productTypeId());
        item.setImage(itemDTO.item_image());

        return ResponseEntity.ok(itemRepository.save(item));
    }
}
