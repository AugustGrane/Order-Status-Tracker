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

    public OrderService(
            OrderRepository orderRepository,
            OrderProductTypeRepository orderProductTypeRepository,
            ItemService itemService,
            StatusDefinitionRepository statusDefinitionRepository,
            ProductTypeRepository productTypeRepository) {
        this.orderRepository = orderRepository;
        this.orderProductTypeRepository = orderProductTypeRepository;
        this.itemService = itemService;
        this.statusDefinitionRepository = statusDefinitionRepository;
        this.productTypeRepository = productTypeRepository;
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
        return persistOrder(domainOrder);
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
        orderEntity = orderRepository.save(orderEntity);

        // Create order items with their statuses
        persistOrderItems(domainOrder.getItems(), orderEntity.getId(), domainOrder.getTimeline());
        
        return orderEntity;
    }

    private void persistOrderItems(Set<OrderItem> items, Long orderId, OrderTimeline timeline) {
        items.forEach(orderItem -> {
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setOrderId(orderId);
            orderDetails.setItem(orderItem.getItem());
            orderDetails.setProduct_type(orderItem.getProductTypeName());
            orderDetails.setItemAmount(orderItem.getQuantity());
            orderDetails.setDifferentSteps(orderItem.getStatus().getSteps());
            orderDetails.setCurrentStepIndex(orderItem.getCurrentStepIndex());
            orderDetails.setUpdated(orderItem.getStatus().getStatusUpdates());

            orderProductTypeRepository.save(orderDetails);
            timeline.recordItemStatus(orderItem.getItem().getId(), orderItem.getCurrentStepId(), LocalDateTime.now());
        });
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
