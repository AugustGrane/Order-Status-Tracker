package gruppe2.backend.domain;

import gruppe2.backend.model.ProductType;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class ProductTypeTransition {
    private final Long itemId;
    private final Long sourceProductTypeId;
    private final Long targetProductTypeId;
    private final ProductType targetProductType;

    public ProductTypeTransition(Long itemId, Long sourceProductTypeId, Long targetProductTypeId, ProductType targetProductType) {
        validateTransition(sourceProductTypeId);
        this.itemId = itemId;
        this.sourceProductTypeId = sourceProductTypeId;
        this.targetProductTypeId = targetProductTypeId;
        this.targetProductType = targetProductType;
    }

    private void validateTransition(Long sourceProductTypeId) {
        if (sourceProductTypeId != 0) {
            throw new IllegalStateException("Can only transition from generic product type (0)");
        }
    }

    public OrderStatus createNewOrderStatus(LocalDateTime initialStepTime) {
        List<Long> steps = targetProductType.getDifferentSteps();
        if (steps == null || steps.isEmpty()) {
            throw new IllegalStateException("Target product type has no steps defined");
        }
        
        List<Long> newSteps = new ArrayList<>(steps); // Create a new copy of the list
        Map<Long, LocalDateTime> statusUpdates = new HashMap<>();
        statusUpdates.put(newSteps.get(0), initialStepTime);
        
        return new OrderStatus(newSteps.toArray(new Long[0]), 0, statusUpdates);
    }

    public Long getItemId() {
        return itemId;
    }

    public Long getSourceProductTypeId() {
        return sourceProductTypeId;
    }

    public Long getTargetProductTypeId() {
        return targetProductTypeId;
    }

    public String getTargetProductTypeName() {
        return targetProductType.getName();
    }

    public boolean isValidTransition() {
        return sourceProductTypeId == 0 && targetProductTypeId != 0;
    }
}
