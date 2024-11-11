package gruppe2.backend.service;

import gruppe2.backend.dto.*;
import gruppe2.backend.domain.*;
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
        // Create domain objects
        CustomerInfo customerInfo = new CustomerInfo(
            orderDTO.customerName(),
            orderDTO.notes(),
            orderDTO.priority()
        );

        OrderTimeline timeline = new OrderTimeline(LocalDateTime.now(), orderDTO.priority());
        
        Map<Long, Integer> processingTimes = getProcessingTimes(orderDTO.items().keySet());
        OrderEstimation estimation = new OrderEstimation(
            orderDTO.items(),
            processingTimes,
            orderDTO.priority()
        );

        // Create and save JPA entity
        gruppe2.backend.model.Order orderEntity = new gruppe2.backend.model.Order();
        orderEntity.setCustomerName(customerInfo.getName());
        orderEntity.setPriority(customerInfo.isPriority());
        orderEntity.setNotes(customerInfo.getNotes());
        orderEntity.setOrderCreated(timeline.getOrderCreated());
        orderEntity.setTotalEstimatedTime(estimation.calculateTotalEstimatedTime());
        orderEntity = orderRepository.save(orderEntity);

        // Create domain order
        Set<OrderItem> orderItems = createOrderItems(orderDTO.items(), orderEntity.getId());
        gruppe2.backend.domain.Order domainOrder = new gruppe2.backend.domain.Order.Builder()
            .withId(orderEntity.getId())
            .withCustomerInfo(customerInfo)
            .withItems(orderItems)
            .withTimeline(timeline)
            .withEstimation(estimation)
            .build();

        // Save order details
        saveOrderDetails(domainOrder);
        
        return orderEntity;
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

    private Set<OrderItem> createOrderItems(Map<Long, Integer> items, Long orderId) {
        Set<OrderItem> orderItems = new HashSet<>();
        items.forEach((itemId, quantity) -> {
            Item item = itemService.findById(itemId);
            ProductType productType = productTypeRepository.findById(item.getProductTypeId())
                    .orElseThrow(() -> new RuntimeException("Product type not found: " + item.getProductTypeId()));

            OrderStatus status = new OrderStatus(productType.getDifferentSteps());
            OrderItem orderItem = new OrderItem(item, quantity, productType.getName(), status);
            orderItems.add(orderItem);
        });
        return orderItems;
    }

    private void saveOrderDetails(gruppe2.backend.domain.Order domainOrder) {
        domainOrder.getItems().forEach(item -> {
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setOrderId(domainOrder.getId());
            orderDetails.setItem(item.getItem());
            orderDetails.setProduct_type(item.getProductTypeName());
            orderDetails.setItemAmount(item.getQuantity());
            orderDetails.setDifferentSteps(item.getStatus().getSteps());
            orderDetails.setCurrentStepIndex(item.getCurrentStepIndex());
            orderDetails.setUpdated(item.getStatus().getStatusUpdates());

            orderProductTypeRepository.save(orderDetails);
        });
    }

    public List<OrderDetailsWithStatusDTO> getOrderDetails(Long orderId) {
        gruppe2.backend.model.Order orderEntity = orderRepository.findById(orderId)
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
