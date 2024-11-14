package gruppe2.backend.domain;

import gruppe2.backend.domain.event.*;
import gruppe2.backend.domain.exception.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents an Order in the system.
 * This is the core domain entity that encapsulates order-related business logic.
 * Implements the Domain-Driven Design (DDD) aggregate root pattern.
 */
public class Order {
    // ==================== Entity State ====================
    private final OrderId id;
    private final CustomerInfo customerInfo;
    private final Set<OrderItem> items;
    private final OrderTimeline timeline;
    private final OrderEstimation estimation;
    private final List<OrderEvent> events;

    /**
     * Constructs a new Order with the specified parameters.
     * Validates invariants and raises an OrderCreatedEvent.
     *
     * @param id Unique identifier for the order
     * @param customerInfo Customer information
     * @param items Set of order items
     * @param timeline Order timeline tracking
     * @param estimation Order completion estimation
     * @throws OrderException if any invariants are violated
     */
    public Order(OrderId id, CustomerInfo customerInfo, Set<OrderItem> items, 
                OrderTimeline timeline, OrderEstimation estimation) {
        this.id = id;
        this.customerInfo = customerInfo;
        this.items = new HashSet<>(items);
        this.timeline = timeline;
        this.estimation = estimation;
        this.events = new ArrayList<>();
        validateInvariants();
        raiseEvent(new OrderCreatedEvent(id.getValue()));
    }

    // ==================== Invariant Validation ====================

    /**
     * Validates the order's invariants.
     * Ensures all required fields are present.
     *
     * @throws OrderException if any invariants are violated
     */
    private void validateInvariants() {
        if (id == null) {
            throw new OrderException("Order must have an ID") {};
        }
        if (customerInfo == null) {
            throw new OrderException("Order must have customer information") {};
        }
        if (timeline == null) {
            throw new OrderException("Order must have a timeline") {};
        }
        if (estimation == null) {
            throw new OrderException("Order must have an estimation") {};
        }
    }

    // ==================== Item Management ====================

    /**
     * Adds a new item to the order.
     * Raises an ItemAddedEvent upon successful addition.
     *
     * @param item Item to add
     * @throws IllegalArgumentException if item is null
     */
    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        items.add(item);
        raiseEvent(new ItemAddedEvent(id.getValue(), item));
    }

    /**
     * Updates the status of an item in the order.
     * Records the status change in the timeline and raises an ItemStatusChangedEvent.
     *
     * @param itemId ID of the item to update
     * @param newStatus New status to set
     * @throws InvalidStatusTransitionException if the status transition is not allowed
     */
    public void updateItemStatus(Long itemId, OrderStatus newStatus) {
        findItem(itemId).ifPresent(item -> {
            OrderStatus oldStatus = item.getStatus();
            if (item.canChangeStatus(newStatus)) {
                items.remove(item);
                OrderItem updatedItem = item.withStatus(newStatus);
                items.add(updatedItem);
                timeline.recordItemStatus(itemId, newStatus.getCurrentStepId(), LocalDateTime.now());
                raiseEvent(new ItemStatusChangedEvent(id.getValue(), itemId, oldStatus, newStatus));
            } else {
                throw new InvalidStatusTransitionException(itemId, oldStatus, newStatus);
            }
        });
    }

    /**
     * Updates the product type of an item in the order.
     * Raises a ProductTypeChangedEvent upon successful update.
     *
     * @param itemId ID of the item to update
     * @param transition Product type transition information
     * @throws InvalidProductTypeTransitionException if the product type change is not allowed
     */
    public void updateItemProductType(Long itemId, ProductTypeTransition transition) {
        findItem(itemId).ifPresent(item -> {
            if (item.canChangeProductType()) {
                OrderStatus newStatus = transition.createNewOrderStatus(
                    timeline.getStatusTimestamp(itemId, item.getCurrentStepId())
                        .orElse(timeline.getOrderCreated())
                );
                
                items.remove(item);
                OrderItem updatedItem = item.withNewProductType(
                    transition.getTargetProductTypeId(),
                    transition.getTargetProductTypeName(),
                    newStatus
                );
                items.add(updatedItem);
                
                raiseEvent(new ProductTypeChangedEvent(id.getValue(), itemId, 
                    transition.getSourceProductTypeId(), 
                    transition.getTargetProductTypeId()));
            } else {
                throw new InvalidProductTypeTransitionException(itemId);
            }
        });
    }

    // ==================== Order Status ====================

    /**
     * Checks if the order is delayed based on estimation.
     *
     * @return true if the order is delayed, false otherwise
     */
    public boolean isDelayed() {
        return estimation.isDelayed(timeline);
    }

    /**
     * Calculates the completion percentage of the order.
     *
     * @return Percentage of order completion
     */
    public double getCompletionPercentage() {
        return estimation.getCompletionPercentage(timeline);
    }

    /**
     * Gets the delay status for each item in the order.
     *
     * @return Map of item IDs to their delay status
     */
    public Map<Long, Boolean> getItemDelayStatus() {
        return estimation.getItemDelayStatus(timeline);
    }

    /**
     * Finds an item in the order by its ID.
     *
     * @param itemId ID of the item to find
     * @return Optional containing the item if found
     */
    public Optional<OrderItem> findItem(Long itemId) {
        return items.stream()
                .filter(item -> item.getItem().getId().equals(itemId))
                .findFirst();
    }

    // ==================== Event Handling ====================

    /**
     * Raises a domain event.
     *
     * @param event Event to raise
     */
    private void raiseEvent(OrderEvent event) {
        events.add(event);
    }

    // ==================== Getters ====================

    public OrderId getId() {
        return id;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public Set<OrderItem> getItems() {
        return Collections.unmodifiableSet(items);
    }

    public OrderTimeline getTimeline() {
        return timeline;
    }

    public OrderEstimation getEstimation() {
        return estimation;
    }

    public List<OrderEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    // ==================== Builder ====================

    /**
     * Builder class for creating Order instances.
     * Implements the Builder pattern for flexible object construction.
     */
    public static class Builder {
        private OrderId id;
        private CustomerInfo customerInfo;
        private Set<OrderItem> items = new HashSet<>();
        private OrderTimeline timeline;
        private OrderEstimation estimation;

        public Builder withId(OrderId id) {
            this.id = id;
            return this;
        }

        public Builder withCustomerInfo(CustomerInfo customerInfo) {
            this.customerInfo = customerInfo;
            return this;
        }

        public Builder withItems(Set<OrderItem> items) {
            this.items = new HashSet<>(items);
            return this;
        }

        public Builder withTimeline(OrderTimeline timeline) {
            this.timeline = timeline;
            return this;
        }

        public Builder withEstimation(OrderEstimation estimation) {
            this.estimation = estimation;
            return this;
        }

        /**
         * Builds a new Order instance.
         *
         * @return New Order instance
         * @throws IllegalStateException if required fields are missing
         */
        public Order build() {
            if (id == null || customerInfo == null || timeline == null || estimation == null) {
                throw new IllegalStateException("Cannot build Order: missing required fields");
            }
            return new Order(id, customerInfo, items, timeline, estimation);
        }
    }
}
