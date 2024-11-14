package gruppe2.backend.domain.specification;

import gruppe2.backend.domain.Order;

/**
 * Specification pattern interface for OrderModel-related business rules.
 */
@FunctionalInterface
public interface OrderSpecification {
    /**
     * Checks if the order satisfies this specification.
     * @param order The order to check
     * @return true if the order satisfies the specification, false otherwise
     */
    boolean isSatisfiedBy(Order order);

    /**
     * Combines this specification with another using AND.
     * @param other The other specification
     * @return A new specification that is the AND of both specifications
     */
    default OrderSpecification and(OrderSpecification other) {
        return order -> isSatisfiedBy(order) && other.isSatisfiedBy(order);
    }

    /**
     * Combines this specification with another using OR.
     * @param other The other specification
     * @return A new specification that is the OR of both specifications
     */
    default OrderSpecification or(OrderSpecification other) {
        return order -> isSatisfiedBy(order) || other.isSatisfiedBy(order);
    }

    /**
     * Creates a new specification that is the NOT of this specification.
     * @return A new specification that is the NOT of this specification
     */
    default OrderSpecification not() {
        return order -> !isSatisfiedBy(order);
    }
}
