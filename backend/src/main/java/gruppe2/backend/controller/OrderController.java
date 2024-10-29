package gruppe2.backend.controller;

import gruppe2.backend.dto.*;
import gruppe2.backend.model.*;
import gruppe2.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        item.setName(itemDTO.name());
        item.setProductTypeId(itemDTO.productTypeId());

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
        order.setItemMapping(orderDTO.items());

        // 2. Calculate total estimated time
        int totalTime = calculateEstimatedTime(orderDTO.items());
        order.setTotalEstimatedTime(totalTime);

        // Save the order first to get its ID
        order = orderRepository.save(order);
        final Long orderId = order.getId();

        // 3. For each item in the order, create an OrderProductType
        orderDTO.items().forEach((itemId, quantity) -> {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

            // Get the product type for this item
            ProductType productType = productTypeRepository.findById(item.getProductTypeId())
                    .orElseThrow(() -> new RuntimeException("Product type not found: " + item.getProductTypeId()));

            // Create OrderProductType
            OrderProductType orderProductType = new OrderProductType();
            orderProductType.setOrderId(orderId);
            orderProductType.setName(productType.getName());

            // Create a new array instead of using the reference directly
            Long[] steps = Arrays.copyOf(productType.getDifferentSteps(),
                    productType.getDifferentSteps().length);
            orderProductType.setDifferentSteps(steps);

            // Set the initial step index
            orderProductType.setCurrentStepIndex(0);

            // Initialize the status updates map with first step
            Map<Long, LocalDateTime> statusUpdates = new HashMap<>();
            statusUpdates.put(steps[0], LocalDateTime.now());
            orderProductType.setUpdated(statusUpdates);

            orderProductTypeRepository.save(orderProductType);
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
    public ResponseEntity<OrderDetailsDTO> getOrderDetails(@PathVariable Long orderId) {
        // Fetch the order and verify it exists
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Get product types
        List<OrderProductType> productTypes = orderProductTypeRepository.findByOrderId(orderId);

        // Get item mapping directly from the order
        Map<Long, Integer> itemQuantities = order.getItemMapping();

        // Combine both pieces of information in the DTO
        OrderDetailsDTO orderDetails = new OrderDetailsDTO(productTypes, itemQuantities);

        return ResponseEntity.ok(orderDetails);
    }


    @PutMapping("/order-product-types/{id}/next-step")
    public ResponseEntity<?> moveToNextStep(@PathVariable Long id) {
        try {
            OrderProductType orderProductType = orderProductTypeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("OrderProductType not found with id: " + id));

            // Check if we're already at the last step
            if (orderProductType.getCurrentStepIndex() >= orderProductType.getDifferentSteps().length - 1) {
                return ResponseEntity
                        .badRequest()
                        .body("Already at final step");
            }

            // Move to next step
            int newIndex = orderProductType.getCurrentStepIndex() + 1;
            orderProductType.setCurrentStepIndex(newIndex);

            // Record timestamp for this step
            Long stepId = orderProductType.getDifferentSteps()[newIndex];
            orderProductType.getUpdated().put(stepId, LocalDateTime.now());

            OrderProductType updated = orderProductTypeRepository.save(orderProductType);

            return ResponseEntity.ok(
                    Map.of(
                            "currentStep", newIndex + 1, // 1-based for display
                            "totalSteps", orderProductType.getDifferentSteps().length,
                            "stepId", stepId,
                            "updatedAt", orderProductType.getUpdated().get(stepId)
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
            OrderProductType orderProductType = orderProductTypeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("OrderProductType not found with id: " + id));

            return ResponseEntity.ok(
                    Map.of(
                            "currentStep", orderProductType.getCurrentStepIndex() + 1,
                            "totalSteps", orderProductType.getDifferentSteps().length,
                            "percentComplete",
                            ((double)(orderProductType.getCurrentStepIndex() + 1) /
                                    orderProductType.getDifferentSteps().length) * 100,
                            "currentStepId",
                            orderProductType.getDifferentSteps()[orderProductType.getCurrentStepIndex()],
                            "stepHistory", orderProductType.getUpdated()
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