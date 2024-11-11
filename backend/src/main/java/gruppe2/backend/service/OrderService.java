package gruppe2.backend.service;

import gruppe2.backend.dto.*;
import gruppe2.backend.domain.*;
import gruppe2.backend.domain.command.*;
import gruppe2.backend.domain.specification.OrderInvariantsSpecification;
import gruppe2.backend.model.*;
import gruppe2.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductTypeRepository orderProductTypeRepository;
    private final ItemService itemService;
    private final StatusDefinitionRepository statusDefinitionRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ItemRepository itemRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderProductTypeRepository orderProductTypeRepository,
            ItemService itemService,
            StatusDefinitionRepository statusDefinitionRepository,
            ProductTypeRepository productTypeRepository,
            ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.orderProductTypeRepository = orderProductTypeRepository;
        this.itemService = itemService;
        this.statusDefinitionRepository = statusDefinitionRepository;
        this.productTypeRepository = productTypeRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public gruppe2.backend.model.Order createOrder(OrderDTO orderDTO) {
        // Get processing times for items
        Map<Long, Integer> processingTimes = getProcessingTimes(orderDTO.items().keySet());
        
        // Create domain objects
        CustomerInfo customerInfo = new CustomerInfo(
            orderDTO.customerName(),
            orderDTO.notes(),
            orderDTO.priority()
        );

        // Create order ID
        OrderId orderId = orderDTO.id() != null ? 
            new OrderId(orderDTO.id()) : 
            new OrderId(generateOrderId());

        // Create domain order using factory
        gruppe2.backend.domain.Order domainOrder = OrderFactory.createOrder(
            orderId,
            customerInfo,
            orderDTO.items(),
            processingTimes,
            orderDTO.priority()
        );

        // Validate using specification
        OrderInvariantsSpecification invariants = OrderInvariantsSpecification.getInstance();
        if (!invariants.isSatisfiedBy(domainOrder)) {
            throw new IllegalStateException("Order validation failed");
        }

        // Persist order
        gruppe2.backend.model.Order persistedOrder = persistOrder(domainOrder);

        // Create order items with their statuses using the same logic as WebhookService
        setupOrderDetails(persistedOrder.getId(), orderDTO.items());
        
        return persistedOrder;
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

    private Long generateOrderId() {
        // Simple implementation - in practice, use a more robust ID generation strategy
        return System.currentTimeMillis();
    }

    private Map<Long, Integer> getProcessingTimes(Set<Long> itemIds) {
        Map<Long, Integer> processingTimes = new HashMap<>();
        itemIds.forEach(itemId -> {
            Item item = itemService.findById(itemId);
            ProductType productType = productTypeRepository.findById(item.getProductTypeId())
                    .orElseThrow(() -> new RuntimeException("Product type not found: " + item.getProductTypeId()));
            processingTimes.put(itemId, productType.getDifferentSteps().length * 10); // Base estimation
        });
        return processingTimes;
    }

    private gruppe2.backend.model.Order persistOrder(gruppe2.backend.domain.Order domainOrder) {
        // Create and save order entity
        gruppe2.backend.model.Order orderEntity = new gruppe2.backend.model.Order();
        orderEntity.setId(domainOrder.getId().getValue());
        orderEntity.setCustomerName(domainOrder.getCustomerInfo().getName());
        orderEntity.setPriority(domainOrder.getCustomerInfo().isPriority());
        orderEntity.setNotes(domainOrder.getCustomerInfo().getNotes());
        orderEntity.setOrderCreated(domainOrder.getTimeline().getOrderCreated());
        orderEntity.setTotalEstimatedTime(domainOrder.getEstimation().calculateTotalEstimatedTime());
        return orderRepository.save(orderEntity);
    }

    public List<OrderDetailsWithStatusDTO> getOrderDetails(Long orderId) {
        gruppe2.backend.model.Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderDetails> orderDetailsList = orderProductTypeRepository.findByOrderId(orderId);
        return orderDetailsList.stream()
                .map(this::createOrderDetailsDTO)
                .collect(Collectors.toList());
    }

    private OrderDetailsWithStatusDTO createOrderDetailsDTO(OrderDetails details) {
        OrderStatus status = new OrderStatus(
            details.getDifferentSteps(),
            details.getCurrentStepIndex(),
            details.getUpdated()
        );

        OrderItem orderItem = new OrderItem(
            details.getItem(),
            details.getItemAmount(),
            details.getProduct_type(),
            status
        );

        StatusDefinition[] statusDefinitions = Arrays.stream(details.getDifferentSteps())
                .map(stepId -> statusDefinitionRepository.findById(stepId)
                        .orElseThrow(() -> new RuntimeException("Status definition not found: " + stepId)))
                .toArray(StatusDefinition[]::new);

        return new OrderDetailsWithStatusDTO(
            details.getId(),
            details.getOrderId(),
            orderItem.getItem(),
            orderItem.getQuantity(),
            orderItem.getProductTypeName(),
            orderItem.getCurrentStepIndex(),
            statusDefinitions,
            orderItem.getStatus().getStatusUpdates()
        );
    }

    public List<OrderDashboardDTO> getAllOrders() {
        List<gruppe2.backend.model.Order> orderEntities = orderRepository.findAllByOrderByOrderCreatedAsc();
        return orderEntities.stream()
                .map(this::createOrderDashboardDTO)
                .collect(Collectors.toList());
    }

    private OrderDashboardDTO createOrderDashboardDTO(gruppe2.backend.model.Order orderEntity) {
        OrderTimeline timeline = new OrderTimeline(orderEntity.getOrderCreated(), orderEntity.isPriority());
        List<OrderDetails> orderDetails = orderProductTypeRepository.findByOrderId(orderEntity.getId());
        
        List<OrderDetailsWithStatusDTO> items = orderDetails.stream()
                .map(this::createOrderDetailsDTO)
                .sorted(Comparator.comparing(OrderDetailsWithStatusDTO::id))
                .collect(Collectors.toList());

        return new OrderDashboardDTO(
            orderEntity.getId(),
            orderEntity.getOrderCreated(),
            orderEntity.isPriority(),
            orderEntity.getCustomerName(),
            orderEntity.getNotes(),
            items
        );
    }

    public StatusDefinition createStatusDefinition(StatusDefinitionDTO dto) {
        StatusDefinition statusDefinition = new StatusDefinition();
        statusDefinition.setName(dto.name());
        statusDefinition.setDescription(dto.description());
        statusDefinition.setImage(dto.image());

        return statusDefinitionRepository.save(statusDefinition);
    }
}
