package gruppe2.backend.domain.exception;

public class WebhookProcessingException extends OrderException {
    private final Long orderId;

    public WebhookProcessingException(Long orderId, String message) {
        super(String.format("Failed to process webhook order %d: %s", orderId, message));
        this.orderId = orderId;
    }

    public WebhookProcessingException(Long orderId, String message, Throwable cause) {
        super(String.format("Failed to process webhook order %d: %s", orderId, message), cause);
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }
}
