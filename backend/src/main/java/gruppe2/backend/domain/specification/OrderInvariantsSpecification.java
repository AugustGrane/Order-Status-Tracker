package gruppe2.backend.domain.specification;

import gruppe2.backend.domain.Order;

/**
 * Specification that validates core business rules (invariants) for an order.
 */
public class OrderInvariantsSpecification implements OrderSpecification {
    
    @Override
    public boolean isSatisfiedBy(Order order) {
        if (order == null) {
            return false;
        }

        // Check required components
        if (order.getId() == null || 
            order.getCustomerInfo() == null || 
            order.getTimeline() == null || 
            order.getEstimation() == null) {
            return false;
        }

        // Check items collection is initialized
        if (order.getItems() == null) {
            return false;
        }

        // Check events collection is initialized
        if (order.getEvents() == null) {
            return false;
        }

        // Check order ID validity
        if (order.getId().getValue() <= 0) {
            return false;
        }

        // All invariants satisfied
        return true;
    }

    @Override
    public String toString() {
        return "OrderInvariantsSpecification";
    }

    /**
     * Creates a new instance of OrderInvariantsSpecification.
     * This is a singleton since the specification is stateless.
     */
    public static OrderInvariantsSpecification getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final OrderInvariantsSpecification INSTANCE = 
            new OrderInvariantsSpecification();
    }
}
