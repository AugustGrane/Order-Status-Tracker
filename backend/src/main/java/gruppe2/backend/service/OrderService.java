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

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductTypeRepository orderProductTypeRepository;
    private final ItemService itemService;
    private final StatusDefinitionRepository statusDefinitionRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailsMapper orderDetailsMapper;

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
            throw new IllegalStateException("OrderModel validation failed");
        }

        // Persist order using mapper
        OrderModel persistedOrderModel = orderRepository.save(orderMapper.toModelOrder(domainOrder));

        // Create order items with their statuses using command
        SetupOrderDetailsCommand setupCommand = new SetupOrderDetailsCommand(
            persistedOrderModel.getId(),
            orderDTO.items(),
            itemRepository,
            productTypeRepository,
            orderProductTypeRepository
        );
        setupCommand.execute();
        
        return persistedOrderModel;
    }

    private Long generateOrderId() {
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

    @Transactional(readOnly = true)
    public List<OrderSummaryProjection> getAllOrderSummaries() {
        return orderRepository.findAllOrderSummaries();
    }

    @Transactional(readOnly = true)
    public OrderModel getOrderDetails(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional(readOnly = true)
    public List<OrderDetailsWithStatusDTO> getOrderDetailsWithStatus(Long orderId) {
        OrderModel orderModel = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderDetails> orderDetailsList = orderProductTypeRepository.findByOrderId(orderId);
        return orderDetailsList.stream()
                .map(orderDetailsMapper::toOrderDetailsDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDashboardDTO> getAllOrders() {
        List<OrderModel> orderModelEntities = orderRepository.findAllWithDetailsOrderByOrderCreatedAsc();
        return orderModelEntities.stream()
                .map(order -> orderDetailsMapper.toOrderDashboardDTO(order, order.getOrderDetails()))
                .collect(Collectors.toList());
    }

    public StatusDefinition createStatusDefinition(StatusDefinitionDTO dto) {
        CreateStatusDefinitionCommand command = new CreateStatusDefinitionCommand(dto, statusDefinitionRepository);
        return command.execute();
    }
}
