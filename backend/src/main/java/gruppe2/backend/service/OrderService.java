package gruppe2.backend.service;

import gruppe2.backend.dto.*;
import gruppe2.backend.domain.*;
import gruppe2.backend.domain.command.*;
import gruppe2.backend.domain.specification.OrderInvariantsSpecification;
import gruppe2.backend.mapper.OrderMapper;
import gruppe2.backend.mapper.OrderDetailsMapper;
import gruppe2.backend.model.*;
import gruppe2.backend.model.Order;
import gruppe2.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Objects;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
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
    public gruppe2.backend.model.Order createOrder(OrderDTO orderDTO) {
        // Get processing times for items
        System.out.println("HALLO: " + orderDTO.items());
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

        // Persist order using mapper
        gruppe2.backend.model.Order persistedOrder = orderRepository.save(orderMapper.toModelOrder(domainOrder));

        // Create order items with their statuses using command
        SetupOrderDetailsCommand setupCommand = new SetupOrderDetailsCommand(
            persistedOrder.getId(),
            orderDTO.items(),
            itemRepository,
            productTypeRepository,
            orderProductTypeRepository
        );
        setupCommand.execute();
        
        return persistedOrder;
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
            processingTimes.put(itemId, productType.getDifferentSteps().size() * 10); // Base estimation
        });
        return processingTimes;
    }

    public List<OrderDetailsWithStatusDTO> getOrderDetails(Long orderId) {
        gruppe2.backend.model.Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderDetails> orderDetailsList = orderProductTypeRepository.findByOrderId(orderId);
        
        // Get all unique status definition IDs
        Set<Long> statusIds = orderDetailsList.stream()
                .flatMap(details -> details.getDifferentSteps().stream())
                .collect(Collectors.toSet());
        
        // Fetch all status definitions in a single query
        Map<Long, StatusDefinition> statusDefinitionsMap = statusDefinitionRepository.findAllById(statusIds)
                .stream()
                .collect(Collectors.toMap(
                    StatusDefinition::getId,
                    sd -> sd,
                    (existing, replacement) -> existing
                ));
                
        return orderDetailsList.stream()
                .map(details -> orderDetailsMapper.toOrderDetailsDTO(details, statusDefinitionsMap))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDashboardDTO> getAllOrders() {
        long startTime = System.currentTimeMillis();
        
        // Fetch orders with minimal data first
        logger.info("Starting to fetch dashboard orders");
        List<OrderDashboardDTO> dashboardDTOs = orderRepository.findAllForDashboard();
        logger.info("Fetched {} orders in {} ms", dashboardDTOs.size(), System.currentTimeMillis() - startTime);
        
        if (dashboardDTOs.isEmpty()) {
            return dashboardDTOs;
        }
        
        // Bulk load all order details in a single query
        long detailsStart = System.currentTimeMillis();
        Map<Long, List<OrderDetailsDTO>> orderDetailsMap = orderProductTypeRepository.findByOrderIdsGrouped(
            dashboardDTOs.stream()
                .map(OrderDashboardDTO::getOrderId)
                .collect(Collectors.toList())
        );
        logger.info("Fetched order details in {} ms", System.currentTimeMillis() - detailsStart);
        
        // Get all unique product type IDs
        long productTypeStart = System.currentTimeMillis();
        Set<Long> productTypeIds = orderDetailsMap.values().stream()
                .flatMap(List::stream)
                .map(details -> details.item().getProductTypeId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
                
        // Fetch all product types in a single query
        Map<Long, ProductType> productTypeMap = productTypeRepository.findAllById(productTypeIds)
                .stream()
                .collect(Collectors.toMap(
                    ProductType::getId,
                    pt -> pt
                ));
        logger.info("Fetched {} product types in {} ms", productTypeMap.size(), System.currentTimeMillis() - productTypeStart);
        
        // Get all unique status definition IDs from all order details
        long statusStart = System.currentTimeMillis();
        Set<Long> allStatusIds = orderDetailsMap.values().stream()
                .flatMap(List::stream)
                .flatMap(details -> details.differentSteps().stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        // Fetch all status definitions in a single query if we have any
        Map<Long, StatusDefinition> statusDefinitionsMap = allStatusIds.isEmpty() ? 
            Collections.emptyMap() :
            statusDefinitionRepository.findAllByIds(allStatusIds)
                .stream()
                .collect(Collectors.toMap(
                    StatusDefinition::getId,
                    sd -> sd
                ));
        logger.info("Fetched {} status definitions in {} ms", statusDefinitionsMap.size(), System.currentTimeMillis() - statusStart);
        
        // Map order details to DTOs and set them in the dashboard DTOs
        long mappingStart = System.currentTimeMillis();
        dashboardDTOs.forEach(dto -> {
            List<OrderDetailsDTO> orderDetails = orderDetailsMap.getOrDefault(dto.getOrderId(), Collections.emptyList());
            List<OrderDetailsWithStatusDTO> items = orderDetails.stream()
                    .map(details -> {
                        // Get product type info if available
                        String productTypeName = Optional.ofNullable(details.item())
                            .map(Item::getProductTypeId)
                            .map(productTypeMap::get)
                            .map(ProductType::getName)
                            .orElse(null);
                            
                        return new OrderDetailsWithStatusDTO(
                            details.id(),
                            details.orderId(),
                            details.item(),
                            details.itemAmount(),
                            productTypeName,
                            details.currentStepIndex(),
                            details.differentSteps().stream()
                                .map(statusDefinitionsMap::get)
                                .toArray(StatusDefinition[]::new),
                            details.updated()
                        );
                    })
                    .collect(Collectors.toList());
            dto.setItems(items);
        });
        logger.info("Mapped DTOs in {} ms", System.currentTimeMillis() - mappingStart);
        
        logger.info("Total dashboard loading time: {} ms", System.currentTimeMillis() - startTime);
        return dashboardDTOs;
    }

    public StatusDefinition createStatusDefinition(StatusDefinitionDTO dto) {
        CreateStatusDefinitionCommand command = new CreateStatusDefinitionCommand(dto, statusDefinitionRepository);
        return command.execute();
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        // Delete the relations
        orderProductTypeRepository.deleteAllItemsByOrderId(orderId);
        // Delete the order
        orderRepository.deleteById(orderId);
    }

    @Transactional(readOnly = true)
    public List<StatusDefinition> getAllStatusDefinitions() {
        return statusDefinitionRepository.findAll();
    }

}
