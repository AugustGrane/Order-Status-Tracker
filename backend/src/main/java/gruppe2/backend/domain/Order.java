package gruppe2.backend.domain;

import gruppe2.backend.domain.event.*;
import gruppe2.backend.domain.exception.*;
import java.time.LocalDateTime;
import java.util.*;

public class Order {
    private final OrderId id;
    private final CustomerInfo customerInfo;
    private final Set<OrderItem> items;
    private final OrderTimeline timeline;
    private final OrderEstimation estimation;
    private final List<OrderEvent> events;

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

    private void validateInvariants() {
        if (id == null) {
            throw new OrderException("OrderModel must have an ID") {};
        }
        if (customerInfo == null) {
            throw new OrderException("OrderModel must have customer information") {};
        }
        if (timeline == null) {
            throw new OrderException("OrderModel must have a timeline") {};
        }
        if (estimation == null) {
            throw new OrderException("OrderModel must have an estimation") {};
        }
    }

    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        items.add(item);
        raiseEvent(new ItemAddedEvent(id.getValue(), item));
    }

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

    public boolean isDelayed() {
        return estimation.isDelayed(timeline);
    }

    public double getCompletionPercentage() {
        return estimation.getCompletionPercentage(timeline);
    }

    public Map<Long, Boolean> getItemDelayStatus() {
        return estimation.getItemDelayStatus(timeline);
    }

    // Changed from private to public for specification pattern
    public Optional<OrderItem> findItem(Long itemId) {
        return items.stream()
                .filter(item -> item.getItem().getId().equals(itemId))
                .findFirst();
    }

    private void raiseEvent(OrderEvent event) {
        events.add(event);
    }

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

        public Order build() {
            if (id == null || customerInfo == null || timeline == null || estimation == null) {
                throw new IllegalStateException("Cannot build OrderModel: missing required fields");
            }
            return new Order(id, customerInfo, items, timeline, estimation);
        }
    }
}
