package gruppe2.backend.domain.specification;

import gruppe2.backend.domain.Order;
import gruppe2.backend.domain.OrderItem;

public class CanChangeProductTypeSpecification implements OrderSpecification {
    private final Long itemId;

    public CanChangeProductTypeSpecification(Long itemId) {
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID cannot be null");
        }
        this.itemId = itemId;
    }

    @Override
    public boolean isSatisfiedBy(Order order) {
        if (order == null) {
            return false;
        }
        return order.findItem(itemId)
                   .map(OrderItem::canChangeProductType)
                   .orElse(false);
    }

    @Override
    public String toString() {
        return String.format("CanChangeProductTypeSpecification{itemId=%d}", itemId);
    }
}
