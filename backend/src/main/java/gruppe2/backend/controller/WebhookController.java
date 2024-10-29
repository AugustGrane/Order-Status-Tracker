package gruppe2.backend.controller;

import gruppe2.backend.dto.OrderDTO;
import gruppe2.backend.model.Item;
import gruppe2.backend.model.Order;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.model.ProductType;
import gruppe2.backend.repository.*;
import gruppe2.backend.webhook.LineItem;
import gruppe2.backend.webhook.WebhookPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final ProductTypeRepository productTypeRepository;
    private final OrderProductTypeRepository orderProductTypeRepository;
    private final StatusDefinitionRepository statusDefinitionRepository;

    public WebhookController(
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

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);



    @PostMapping("/wooOrder")
    public ResponseEntity<WebhookPayload> handleWebhook(@RequestBody WebhookPayload payload) {
        try {
            // Log basic information for verification
//            log.info("Order ID: {}", payload.getId());
//            log.info("Customer: {} {}, Company: {}", payload.getBilling().getFirstName(), payload.getBilling().getLastName(), payload.getBilling().getCompany());
//
//            // Log line items
//            for (LineItem item : payload.getItems()) {
//                log.info("Item: {}, Quantity: {}, Id: {}", item.getName(), item.getQuantity(),item.getId());
//                log.info("Image source: {}", item.getImg());
//            }
            //System.out.println("payload:"+ payload.toString());


            // Extracting names from payload object - displayName depends on existance of company name
            String companyName, name, displayName;

            // COWI | John Doe
            if(!Objects.equals(payload.getBilling().getCompany(), "")) {
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
            }

            // Repackage to orderDTO
            OrderDTO orderDTO = new OrderDTO(
                    displayName,
                    false,
                    "",
                    itemsMap
            );

            System.out.println("OrderDTO" + orderDTO);
            // Send package to creatOrderFromWebhook function
            createOrderFromWebhook(orderDTO);


            // Return the payload filtered payload object to sender (for check in postman)
            return ResponseEntity.ok(payload);

        } catch (Exception e) {
            log.error("Error processing webhook payload: ", e);
            return ResponseEntity.status(500).body(null);
        }
    }
    
    public ResponseEntity<Order> createOrderFromWebhook (@RequestBody OrderDTO orderDTO) {
        // 1. Create the order
        Order order = new Order();
        order.setCustomerName(orderDTO.customerName());
        order.setPriority(orderDTO.priority());
        order.setNotes(orderDTO.notes());
        order.setOrderCreated(LocalDateTime.now());

        // 2. Calculate total estimated time
        int totalTime = calculateEstimatedTime(orderDTO.items());
        order.setTotalEstimatedTime(totalTime);

        // Save the order first to get its ID
        order = orderRepository.save(order);
        final Long orderId = order.getId();

        // 3. For each item in the order, create an OrderDetails
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

        return ResponseEntity.ok(order);
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
}

