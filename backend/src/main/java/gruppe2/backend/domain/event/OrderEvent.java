package gruppe2.backend.domain.event;

import java.time.LocalDateTime;

public abstract class OrderEvent {
    private final Long orderId;
    private final LocalDateTime timestamp;

    protected OrderEvent(Long orderId) {
        this.orderId = orderId;
        this.timestamp = LocalDateTime.now();
    }

    public Long getOrderId() {
        return orderId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public abstract String getEventType();
}
