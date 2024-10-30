package gruppe2.backend.controller;

import gruppe2.backend.dto.*;
import gruppe2.backend.model.*;
import gruppe2.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final ProductTypeRepository productTypeRepository;
    private final OrderProductTypeRepository orderProductTypeRepository;
    private final StatusDefinitionRepository statusDefinitionRepository;

    @Autowired
    public OrderController(
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

    @PostMapping("/items")
    public ResponseEntity<Item> createItem(@RequestBody ItemDTO itemDTO) {
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

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody OrderDTO orderDTO) {
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

    @PostMapping("/product-types")
    public ResponseEntity<ProductType> createProductType(@RequestBody ProductTypeDTO productTypeDTO) {
        // Verify all steps exist
        for (Long stepId : productTypeDTO.differentSteps()) {
            statusDefinitionRepository.findById(stepId)
                    .orElseThrow(() -> new RuntimeException("Status definition not found: " + stepId));
        }

        ProductType productType = new ProductType();
        productType.setName(productTypeDTO.name());
        productType.setDifferentSteps(productTypeDTO.differentSteps());

        return ResponseEntity.ok(productTypeRepository.save(productType));
    }

    @PostMapping("/status-definitions")
    public ResponseEntity<StatusDefinition> createStatusDefinition(@RequestBody StatusDefinitionDTO dto) {
        StatusDefinition statusDefinition = new StatusDefinition();
        statusDefinition.setName(dto.name());
        statusDefinition.setDescription(dto.description());
        statusDefinition.setImage(dto.image());

        return ResponseEntity.ok(statusDefinitionRepository.save(statusDefinition));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<List<OrderDetailsWithStatusDTO>> getOrderProductType(@PathVariable Long orderId) {
        // Fetch the order and verify it exists
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Get order details
        List<OrderDetails> orderDetailsList = orderProductTypeRepository.findByOrderId(orderId);

        // Convert each OrderDetails to OrderDetailsWithStatusDTO
        List<OrderDetailsWithStatusDTO> result = orderDetailsList.stream()
                .map(details -> {
                    // Convert step IDs to StatusDefinition objects
                    StatusDefinition[] statusDefinitions = Arrays.stream(details.getDifferentSteps())
                            .map(stepId -> statusDefinitionRepository.findById(stepId)
                                    .orElseThrow(() -> new RuntimeException("Status definition not found: " + stepId)))
                            .toArray(StatusDefinition[]::new);

                    return new OrderDetailsWithStatusDTO(
                            details.getId(),
                            details.getOrderId(),
                            details.getItem(),
                            details.getItemAmount(),
                            details.getProduct_type(),
                            details.getCurrentStepIndex(),
                            statusDefinitions,
                            details.getUpdated()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PutMapping("/order-product-types/{id}/next-step")
    public ResponseEntity<?> moveToNextStep(@PathVariable Long id) {
        try {
            OrderDetails orderDetails = orderProductTypeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("OrderDetails not found with id: " + id));

            // Check if we're already at the last step
            if (orderDetails.getCurrentStepIndex() >= orderDetails.getDifferentSteps().length - 1) {
                return ResponseEntity
                        .badRequest()
                        .body("Already at final step");
            }

            // Move to next step
            int newIndex = orderDetails.getCurrentStepIndex() + 1;
            orderDetails.setCurrentStepIndex(newIndex);

            // Record timestamp for this step
            Long stepId = orderDetails.getDifferentSteps()[newIndex];
            orderDetails.getUpdated().put(stepId, LocalDateTime.now());

            OrderDetails updated = orderProductTypeRepository.save(orderDetails);

            return ResponseEntity.ok(
                    Map.of(
                            "currentStep", newIndex + 1, // 1-based for display
                            "totalSteps", orderDetails.getDifferentSteps().length,
                            "stepId", stepId,
                            "updatedAt", orderDetails.getUpdated().get(stepId)
                    )
            );

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating step: " + e.getMessage());
        }
    }

    @GetMapping("/order-product-types/{id}/progress")
    public ResponseEntity<?> getProgress(@PathVariable Long id) {
        try {
            OrderDetails orderDetails = orderProductTypeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("OrderDetails not found with id: " + id));

            return ResponseEntity.ok(
                    Map.of(
                            "currentStep", orderDetails.getCurrentStepIndex() + 1,
                            "totalSteps", orderDetails.getDifferentSteps().length,
                            "percentComplete",
                            ((double)(orderDetails.getCurrentStepIndex() + 1) /
                                    orderDetails.getDifferentSteps().length) * 100,
                            "currentStepId",
                            orderDetails.getDifferentSteps()[orderDetails.getCurrentStepIndex()],
                            "stepHistory", orderDetails.getUpdated()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting progress: " + e.getMessage());
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
}
