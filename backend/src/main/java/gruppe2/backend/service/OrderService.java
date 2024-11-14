package gruppe2.backend.service;

import gruppe2.backend.dto.*;
import gruppe2.backend.domain.*;
import gruppe2.backend.domain.command.*;
import gruppe2.backend.domain.specification.OrderInvariantsSpecification;
import gruppe2.backend.mapper.OrderMapper;
import gruppe2.backend.mapper.OrderDetailsMapper;
import gruppe2.backend.model.*;
import gruppe2.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for managing orders in the Order Status Tracker system.
 * Handles business logic for order creation, retrieval, and status management.
 * Implements transactional boundaries and domain logic validation.
 */
@Service
public class OrderService {
    // ==================== Repository Dependencies ====================
    private final OrderRepository orderRepository;
    private final OrderProductTypeRepository orderProductTypeRepository;
    private final ItemService itemService;
    private final StatusDefinitionRepository statusDefinitionRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ItemRepository itemRepository;

    // ==================== Mapper Dependencies ====================
    private final OrderMapper orderMapper;
    private final OrderDetailsMapper orderDetailsMapper;

    /**
     * Constructs a new OrderService with required dependencies.
     * 
     * @param orderRepository Repository for order operations
     * @param orderProductTypeRepository Repository for order product type operations
     * @param itemService Service for item operations
     * @param statusDefinitionRepository Repository for status definition operations
     * @param productTypeRepository Repository for product type operations
     * @param itemRepository Repository for item operations
     * @param orderMapper Mapper for order entities
     * @param orderDetailsMapper Mapper for order details
     */
    public OrderService(
            OrderRepository orderRepository,
            OrderProductTypeRepository orderProductTypeRepository,
            ItemService itemService,
            StatusDefinitionRepository statusDefinitionRepository,
            ProductTypeRepository productTypeRepository,
            ItemRepository itemRepository,
            OrderMapper orderMapper,
            OrderDetailsMapper orderDetailsMapper) {
        this.orderRepository = orderRepository;
        this.orderProductTypeRepository = orderProductTypeRepository;
        this.itemService = itemService;
        this.statusDefinitionRepository = statusDefinitionRepository;
        this.productTypeRepository = productTypeRepository;
        this.itemRepository = itemRepository;
        this.orderMapper = orderMapper;
        this.orderDetailsMapper = orderDetailsMapper;
    }

    // ==================== Order Creation Operations ====================

    /**
     * Creates a new order in the system with the provided details.
     * Validates order invariants and sets up initial order status.
     *
     * @param orderDTO Data transfer object containing order details
     * @return Created order model
     * @throws IllegalStateException if order validation fails
     */
    @Transactional
    public OrderModel createOrder(OrderDTO orderDTO) {
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
        Order domainOrder = OrderFactory.createOrder(
            orderId,
            customerInfo,
            orderDTO.items(),
            processingTimes,
            orderDTO.priority()
        );

        // Validate using specification
        validateOrderInvariants(domainOrder);

        // Persist order using mapper
        OrderModel persistedOrder = orderRepository.save(orderMapper.toModelOrder(domainOrder));

        // Setup order details
        setupOrderDetails(persistedOrder.getId(), orderDTO.items());
        
        return persistedOrder;
    }

    // ==================== Order Retrieval Operations ====================

    /**
     * Retrieves all order summaries from the system.
     *
     * @return List of order summaries
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryProjection> getAllOrderSummaries() {
        return orderRepository.findAllOrderSummaries();
    }

    /**
     * Retrieves detailed order information for a specific order.
     *
     * @param orderId ID of the order to retrieve
     * @return Order model containing complete order details
     * @throws RuntimeException if order is not found
     */
    @Transactional(readOnly = true)
    public OrderModel getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    /**
     * Retrieves order details with current status information.
     *
     * @param orderId ID of the order to retrieve
     * @return List of order details with status information
     * @throws RuntimeException if order is not found
     */
    @Transactional(readOnly = true)
    public List<OrderDetailsWithStatusDTO> getOrderDetailsWithStatus(Long orderId) {
        // Verify order exists
        orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderDetails> orderDetailsList = orderProductTypeRepository.findByOrderId(orderId);
        return orderDetailsList.stream()
                .map(orderDetailsMapper::toOrderDetailsDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all orders with dashboard information.
     *
     * @return List of orders with dashboard information
     */
    public List<OrderDashboardDTO> getAllOrders() {
        List<OrderModel> orderEntities = orderRepository.findAllWithDetailsOrderByOrderCreatedAsc();
        return orderEntities.stream()
                .map(order -> orderDetailsMapper.toOrderDashboardDTO(order, order.getOrderDetails()))
                .collect(Collectors.toList());
    }

    // ==================== Status Management Operations ====================

    /**
     * Creates a new status definition for order processing.
     *
     * @param dto Status definition details
     * @return Created status definition
     */
    public StatusDefinition createStatusDefinition(StatusDefinitionDTO dto) {
        CreateStatusDefinitionCommand command = new CreateStatusDefinitionCommand(dto, statusDefinitionRepository);
        return command.execute();
    }

    // ==================== Private Helper Methods ====================

    /**
     * Validates order invariants using the specification pattern.
     *
     * @param order Order to validate
     * @throws IllegalStateException if validation fails
     */
    private void validateOrderInvariants(Order order) {
        OrderInvariantsSpecification invariants = OrderInvariantsSpecification.getInstance();
        if (!invariants.isSatisfiedBy(order)) {
            throw new IllegalStateException("Order validation failed");
        }
    }

    /**
     * Sets up order details for a newly created order.
     *
     * @param orderId ID of the order
     * @param items Map of items and their quantities
     */
    private void setupOrderDetails(Long orderId, Map<Long, Integer> items) {
        SetupOrderDetailsCommand setupCommand = new SetupOrderDetailsCommand(
            orderId,
            items,
            itemRepository,
            productTypeRepository,
            orderProductTypeRepository
        );
        setupCommand.execute();
    }

    /**
     * Generates a new order ID based on current timestamp.
     *
     * @return Generated order ID
     */
    private Long generateOrderId() {
        return System.currentTimeMillis();
    }

    /**
     * Retrieves processing times for a set of items.
     *
     * @param itemIds Set of item IDs
     * @return Map of item IDs to their processing times
     */
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
}
