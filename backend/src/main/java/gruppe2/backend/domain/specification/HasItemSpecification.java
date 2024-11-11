package gruppe2.backend.domain.specification;

import gruppe2.backend.domain.Order;

public class HasItemSpecification implements OrderSpecification {
    private final Long itemId;

    public HasItemSpecification(Long itemId) {
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
        return order.findItem(itemId).isPresent();
    }

    @Override
    public String toString() {
        return String.format("HasItemSpecification{itemId=%d}", itemId);
    }

    public Long getItemId() {
        return itemId;
    }
}
