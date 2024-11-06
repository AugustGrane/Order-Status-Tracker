package gruppe2.backend.service;

import gruppe2.backend.dto.*;
import gruppe2.backend.model.*;
import gruppe2.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final ProductTypeRepository productTypeRepository;
    private final OrderProductTypeRepository orderProductTypeRepository;
    private final StatusDefinitionRepository statusDefinitionRepository;

    public OrderService(
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

    public Item createItem(ItemDTO itemDTO) {
        // Verify product type exists
        productTypeRepository.findById(itemDTO.productTypeId())
                .orElseThrow(() -> new RuntimeException("Product type not found"));

        Item item = new Item();
        item.setId(itemDTO.id());
        item.setName(itemDTO.name());
        item.setProductTypeId(itemDTO.productTypeId());
        item.setImage(itemDTO.item_image());

        return itemRepository.save(item);
    }

    public Order createOrder(OrderDTO orderDTO) {
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
            orderDetails.setItem(item);
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

        return order;
    }

    public ProductType createProductType(ProductTypeDTO productTypeDTO) {
        // Verify all steps exist
        for (Long stepId : productTypeDTO.differentSteps()) {
            statusDefinitionRepository.findById(stepId)
                    .orElseThrow(() -> new RuntimeException("Status definition not found: " + stepId));
        }

        ProductType productType = new ProductType();
        productType.setName(productTypeDTO.name());
        productType.setDifferentSteps(productTypeDTO.differentSteps());

        return productTypeRepository.save(productType);
    }

    public StatusDefinition createStatusDefinition(StatusDefinitionDTO dto) {
        StatusDefinition statusDefinition = new StatusDefinition();
        statusDefinition.setName(dto.name());
        statusDefinition.setDescription(dto.description());
        statusDefinition.setImage(dto.image());

        return statusDefinitionRepository.save(statusDefinition);
    }

    public List<OrderDetailsWithStatusDTO> getOrderDetails(Long orderId) {
        // Fetch the order and verify it exists
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Get order details
        List<OrderDetails> orderDetailsList = orderProductTypeRepository.findByOrderId(orderId);

        // Convert each OrderDetails to OrderDetailsWithStatusDTO
        return orderDetailsList.stream()
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
    }

    public Map<String, Object> moveToNextStep(Long id) {
        OrderDetails orderDetails = orderProductTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderDetails not found with id: " + id));

        // Check if we're already at the last step
        if (orderDetails.getCurrentStepIndex() >= orderDetails.getDifferentSteps().length - 1) {
            throw new IllegalStateException("Already at final step");
        }

        // Move to next step
        int newIndex = orderDetails.getCurrentStepIndex() + 1;
        orderDetails.setCurrentStepIndex(newIndex);

        // Record timestamp for this step
        Long stepId = orderDetails.getDifferentSteps()[newIndex];
        orderDetails.getUpdated().put(stepId, LocalDateTime.now());

        orderProductTypeRepository.save(orderDetails);

        return Map.of(
                "currentStep", newIndex + 1, // 1-based for display
                "totalSteps", orderDetails.getDifferentSteps().length,
                "stepId", stepId,
                "updatedAt", orderDetails.getUpdated().get(stepId)
        );
    }

    public Map<String, Object> moveToPrevStep(Long id) {
        OrderDetails orderDetails = orderProductTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderDetails not found with id: " + id));

        // Check if we're already at the first step
        if (orderDetails.getCurrentStepIndex() <= 0) {
            throw new IllegalStateException("Already at first step");
        }

        // Move to previous step
        int newIndex = orderDetails.getCurrentStepIndex() - 1;
        orderDetails.setCurrentStepIndex(newIndex);

        // Record timestamp for this step
        Long stepId = orderDetails.getDifferentSteps()[newIndex];
        orderDetails.getUpdated().put(stepId, LocalDateTime.now());

        orderProductTypeRepository.save(orderDetails);

        return Map.of(
                "currentStep", newIndex + 1, // 1-based for display
                "totalSteps", orderDetails.getDifferentSteps().length,
                "stepId", stepId,
                "updatedAt", orderDetails.getUpdated().get(stepId)
        );
    }

    public Map<String, Object> getProgress(Long id) {
        OrderDetails orderDetails = orderProductTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderDetails not found with id: " + id));

        return Map.of(
                "currentStep", orderDetails.getCurrentStepIndex() + 1,
                "totalSteps", orderDetails.getDifferentSteps().length,
                "percentComplete",
                ((double)(orderDetails.getCurrentStepIndex() + 1) /
                        orderDetails.getDifferentSteps().length) * 100,
                "currentStepId",
                orderDetails.getDifferentSteps()[orderDetails.getCurrentStepIndex()],
                "stepHistory", orderDetails.getUpdated()
        );
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