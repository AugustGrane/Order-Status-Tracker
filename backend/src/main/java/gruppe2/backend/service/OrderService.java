package gruppe2.backend.service;

import gruppe2.backend.dto.*;
import gruppe2.backend.domain.*;
import gruppe2.backend.model.*;
import gruppe2.backend.repository.*;
import org.springframework.stereotype.Service;
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

    public Order createOrder(OrderDTO orderDTO) {
        // Create order timeline
        OrderTimeline timeline = new OrderTimeline(LocalDateTime.now(), orderDTO.priority());
        
        // Create order estimation
        Map<Long, Integer> processingTimes = getProcessingTimes(orderDTO.items().keySet());
        OrderEstimation estimation = new OrderEstimation(
            orderDTO.items(),
            processingTimes,
            orderDTO.priority()
        );

        // Create and save order entity
        Order order = new Order();
        order.setCustomerName(orderDTO.customerName());
        order.setPriority(orderDTO.priority());
        order.setNotes(orderDTO.notes());
        order.setOrderCreated(timeline.getOrderCreated());
        order.setTotalEstimatedTime(estimation.calculateTotalEstimatedTime());
        order = orderRepository.save(order);

        // Create order items with their statuses
        createOrderItems(orderDTO.items(), order.getId(), timeline);
        
        return order;
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

    private void createOrderItems(Map<Long, Integer> items, Long orderId, OrderTimeline timeline) {
        items.forEach((itemId, quantity) -> {
            Item item = itemService.findById(itemId);
            ProductType productType = productTypeRepository.findById(item.getProductTypeId())
                    .orElseThrow(() -> new RuntimeException("Product type not found: " + item.getProductTypeId()));

            OrderStatus status = new OrderStatus(productType.getDifferentSteps());
            OrderItem orderItem = new OrderItem(item, quantity, productType.getName(), status);

            saveOrderDetails(orderItem, orderId, timeline);
        });
    }

    private void saveOrderDetails(OrderItem orderItem, Long orderId, OrderTimeline timeline) {
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
    }

    public List<OrderDetailsWithStatusDTO> getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
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
        List<Order> orders = orderRepository.findAllByOrderByOrderCreatedAsc();
        return orders.stream()
                .map(this::createOrderDashboardDTO)
                .collect(Collectors.toList());
    }

    private OrderDashboardDTO createOrderDashboardDTO(Order order) {
        OrderTimeline timeline = new OrderTimeline(order.getOrderCreated(), order.isPriority());
        List<OrderDetails> orderDetails = orderProductTypeRepository.findByOrderId(order.getId());
        
        List<OrderDetailsWithStatusDTO> items = orderDetails.stream()
                .map(this::createOrderDetailsDTO)
                .sorted(Comparator.comparing(OrderDetailsWithStatusDTO::id))
                .collect(Collectors.toList());

        return new OrderDashboardDTO(
            order.getId(),
            timeline.getOrderCreated(),
            order.isPriority(),
            order.getCustomerName(),
            order.getNotes(),
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
