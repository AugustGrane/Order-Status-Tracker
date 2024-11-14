package gruppe2.backend.service;

import gruppe2.backend.domain.*;
import gruppe2.backend.domain.command.CreateProductTypeCommand;
import gruppe2.backend.domain.command.UpdateProductTypeCommand;
import gruppe2.backend.domain.specification.CanChangeProductTypeSpecification;
import gruppe2.backend.domain.specification.HasItemSpecification;
import gruppe2.backend.dto.ProductTypeDTO;
import gruppe2.backend.model.Item;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.model.ProductType;
import gruppe2.backend.repository.*;
import gruppe2.backend.service.util.OrderFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing product types and their transitions.
 * This service handles the creation and modification of product types,
 * as well as the transition of items between different product types.
 */
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

    /**
     * Creates a new product type.
     *
     * @param productTypeDTO The DTO containing product type details
     * @return The created product type
     */
    @Transactional
    public ProductType createProductType(ProductTypeDTO productTypeDTO) {
        CreateProductTypeCommand command = new CreateProductTypeCommand(
            productTypeDTO,
            productTypeRepository,
            statusDefinitionRepository
        );
        return command.execute();
    }

    /**
     * Updates an item's product type and handles all necessary transitions.
     *
     * @param itemId ID of the item to update
     * @param targetProductTypeId ID of the target product type
     * @throws IllegalStateException if the transition is invalid
     */
    @Transactional
    public void updateItemProductType(Long itemId, Long targetProductTypeId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

        ProductType targetProductType = findById(targetProductTypeId);

        ProductTypeTransition transition = new ProductTypeTransition(
            itemId,
            item.getProductTypeId(),
            targetProductTypeId,
            targetProductType
        );

        if (!transition.isValidTransition()) {
            throw new IllegalStateException("Invalid product type transition");
        }

        List<OrderDetails> orderDetailsList = orderProductTypeRepository.findByItemId(itemId);
        
        for (OrderDetails orderDetails : orderDetailsList) {
            Order order = OrderFactory.createFromOrderDetails(orderDetails, orderRepository);
            
            HasItemSpecification hasItem = new HasItemSpecification(itemId);
            CanChangeProductTypeSpecification canChange = new CanChangeProductTypeSpecification(itemId);
            
            if (!hasItem.and(canChange).isSatisfiedBy(order)) {
                throw new IllegalStateException(
                    String.format("Cannot update product type for item %d: item not found or cannot change type", itemId)
                );
            }

            UpdateProductTypeCommand command = new UpdateProductTypeCommand(itemId, transition);
            command.execute(order);
            
            updateOrderDetailsForTransition(orderDetails, transition);
        }

        updateItemProductTypeId(item, targetProductTypeId);
    }

    /**
     * Finds a product type by its ID.
     *
     * @param productTypeId ID of the product type to find
     * @return The found product type
     * @throws RuntimeException if the product type is not found
     */
    @Transactional(readOnly = true)
    public ProductType findById(Long productTypeId) {
        return productTypeRepository.findById(productTypeId)
                .orElseThrow(() -> new RuntimeException("Product type not found: " + productTypeId));
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
}
