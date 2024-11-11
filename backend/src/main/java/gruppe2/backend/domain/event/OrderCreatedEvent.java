package gruppe2.backend.domain.event;

import java.time.LocalDateTime;

public class OrderCreatedEvent extends OrderEvent {
    private final LocalDateTime createdAt;

    public OrderCreatedEvent(Long orderId) {
        super(orderId);
        this.createdAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getEventType() {
        return "ORDER_CREATED";
    }

    @Override
    public String toString() {
        return String.format("OrderCreatedEvent{orderId=%d, createdAt=%s}", 
            getOrderId(), createdAt);
    }
}
