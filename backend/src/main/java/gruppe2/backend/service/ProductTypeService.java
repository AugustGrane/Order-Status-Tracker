package gruppe2.backend.service;

import gruppe2.backend.dto.ProductTypeDTO;
import gruppe2.backend.domain.*;
import gruppe2.backend.domain.command.CreateProductTypeCommand;
import gruppe2.backend.domain.command.UpdateProductTypeCommand;
import gruppe2.backend.domain.specification.CanChangeProductTypeSpecification;
import gruppe2.backend.domain.specification.HasItemSpecification;
import gruppe2.backend.model.Item;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.model.ProductType;
import gruppe2.backend.repository.ItemRepository;
import gruppe2.backend.repository.OrderProductTypeRepository;
import gruppe2.backend.repository.OrderRepository;
import gruppe2.backend.repository.ProductTypeRepository;
import gruppe2.backend.repository.StatusDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductTypeService {
    private final ProductTypeRepository productTypeRepository;
    private final StatusDefinitionRepository statusDefinitionRepository;
    private final ItemRepository itemRepository;
    private final OrderProductTypeRepository orderProductTypeRepository;
    private final OrderRepository orderRepository;

    public ProductTypeService(
            ProductTypeRepository productTypeRepository,
            StatusDefinitionRepository statusDefinitionRepository,
            ItemRepository itemRepository,
            OrderProductTypeRepository orderProductTypeRepository,
            OrderRepository orderRepository) {
        this.productTypeRepository = productTypeRepository;
        this.statusDefinitionRepository = statusDefinitionRepository;
        this.itemRepository = itemRepository;
        this.orderProductTypeRepository = orderProductTypeRepository;
        this.orderRepository = orderRepository;
    }

    public ProductType createProductType(ProductTypeDTO productTypeDTO) {
        CreateProductTypeCommand command = new CreateProductTypeCommand(
            productTypeDTO,
            productTypeRepository,
            statusDefinitionRepository
        );
        return command.execute();
    }

    @Transactional
    public void updateItemProductType(Long itemId, Long targetProductTypeId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

        ProductType targetProductType = productTypeRepository.findById(targetProductTypeId)
                .orElseThrow(() -> new RuntimeException("Product type not found: " + targetProductTypeId));

        ProductTypeTransition transition = new ProductTypeTransition(
            itemId,
            item.getProductTypeId(),
            targetProductTypeId,
            targetProductType
        );

        if (!transition.isValidTransition()) {
            throw new IllegalStateException("Invalid product type transition");
        }

        // Find all order details for this item
        List<OrderDetails> orderDetailsList = orderProductTypeRepository.findByItemId(itemId);
        
        for (OrderDetails orderDetails : orderDetailsList) {
            // Create domain objects
            Order order = createOrderFromDetails(orderDetails);
            
            // Create and validate specifications
            HasItemSpecification hasItem = new HasItemSpecification(itemId);
            CanChangeProductTypeSpecification canChange = new CanChangeProductTypeSpecification(itemId);
            
            if (!hasItem.and(canChange).isSatisfiedBy(order)) {
                throw new IllegalStateException(
                    String.format("Cannot update product type for item %d: item not found or cannot change type", itemId)
                );
            }

            // Create and execute command
            UpdateProductTypeCommand command = new UpdateProductTypeCommand(itemId, transition);
            command.execute(order);
            
            // Update persistence
            updateOrderDetailsForTransition(orderDetails, transition);
        }

        updateItemProductTypeId(item, targetProductTypeId);
    }

    private Order createOrderFromDetails(OrderDetails orderDetails) {
        var orderEntity = orderRepository.findById(orderDetails.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        CustomerInfo customerInfo = new CustomerInfo(
            orderEntity.getCustomerName(),
            orderEntity.getNotes(),
            orderEntity.isPriority()
        );

        OrderTimeline timeline = new OrderTimeline(
            orderEntity.getOrderCreated(),
            orderEntity.isPriority()
        );

        Map<Long, Integer> items = new HashMap<>();
        items.put(orderDetails.getItem().getId(), orderDetails.getItemAmount());

        Map<Long, Integer> processingTimes = new HashMap<>();
        processingTimes.put(orderDetails.getItem().getId(), orderEntity.getTotalEstimatedTime());

        OrderEstimation estimation = new OrderEstimation(
            items,
            processingTimes,
            orderEntity.isPriority()
        );

        return new Order.Builder()
            .withId(new OrderId(orderEntity.getId()))
            .withCustomerInfo(customerInfo)
            .withTimeline(timeline)
            .withEstimation(estimation)
            .build();
    }

    private void updateOrderDetailsForTransition(OrderDetails orderDetails, ProductTypeTransition transition) {
        OrderStatus newStatus = transition.createNewOrderStatus(
            orderDetails.getUpdated().get(orderDetails.getDifferentSteps()[0])
        );

        orderDetails.setDifferentSteps(newStatus.getSteps());
        orderDetails.setCurrentStepIndex(newStatus.getCurrentStepIndex());
        orderDetails.setUpdated(newStatus.getStatusUpdates());
        orderDetails.setProduct_type(transition.getTargetProductTypeName());

        orderProductTypeRepository.save(orderDetails);
    }

    private void updateItemProductTypeId(Item item, Long productTypeId) {
        item.setProductTypeId(productTypeId);
        itemRepository.save(item);
    }

    public ProductType findById(Long productTypeId) {
        return productTypeRepository.findById(productTypeId)
                .orElseThrow(() -> new RuntimeException("Product type not found: " + productTypeId));
    }
}
