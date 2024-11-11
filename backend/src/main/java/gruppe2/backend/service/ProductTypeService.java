package gruppe2.backend.service;

import gruppe2.backend.dto.ProductTypeDTO;
import gruppe2.backend.domain.ProductTypeTransition;
import gruppe2.backend.domain.OrderStatus;
import gruppe2.backend.model.Item;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.model.ProductType;
import gruppe2.backend.repository.ItemRepository;
import gruppe2.backend.repository.OrderProductTypeRepository;
import gruppe2.backend.repository.ProductTypeRepository;
import gruppe2.backend.repository.StatusDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductTypeService {
    private final ProductTypeRepository productTypeRepository;
    private final StatusDefinitionRepository statusDefinitionRepository;
    private final ItemRepository itemRepository;
    private final OrderProductTypeRepository orderProductTypeRepository;

    public ProductTypeService(
            ProductTypeRepository productTypeRepository,
            StatusDefinitionRepository statusDefinitionRepository,
            ItemRepository itemRepository,
            OrderProductTypeRepository orderProductTypeRepository) {
        this.productTypeRepository = productTypeRepository;
        this.statusDefinitionRepository = statusDefinitionRepository;
        this.itemRepository = itemRepository;
        this.orderProductTypeRepository = orderProductTypeRepository;
    }

    public ProductType createProductType(ProductTypeDTO productTypeDTO) {
        validateStepsExist(productTypeDTO.differentSteps());

        ProductType productType = new ProductType();
        productType.setName(productTypeDTO.name());
        productType.setDifferentSteps(productTypeDTO.differentSteps());

        return productTypeRepository.save(productType);
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

        updateOrderDetailsForTransition(transition);
        updateItemProductTypeId(item, targetProductTypeId);
    }

    private void validateStepsExist(Long[] stepIds) {
        for (Long stepId : stepIds) {
            statusDefinitionRepository.findById(stepId)
                    .orElseThrow(() -> new RuntimeException("Status definition not found: " + stepId));
        }
    }

    private void updateOrderDetailsForTransition(ProductTypeTransition transition) {
        List<OrderDetails> orderDetailsList = orderProductTypeRepository.findByItemId(transition.getItemId());
        
        for (OrderDetails orderDetails : orderDetailsList) {
            if (orderDetails.getItem().getId().equals(transition.getItemId())) {
                OrderStatus newStatus = transition.createNewOrderStatus(
                    orderDetails.getUpdated().get(orderDetails.getDifferentSteps()[0])
                );

                orderDetails.setDifferentSteps(newStatus.getSteps());
                orderDetails.setCurrentStepIndex(newStatus.getCurrentStepIndex());
                orderDetails.setUpdated(newStatus.getStatusUpdates());
                orderDetails.setProduct_type(transition.getTargetProductTypeName());

                orderProductTypeRepository.save(orderDetails);
            }
        }
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
