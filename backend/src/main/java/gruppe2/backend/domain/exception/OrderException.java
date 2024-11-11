package gruppe2.backend.domain.exception;

public abstract class OrderException extends RuntimeException {
    protected OrderException(String message) {
        super(message);
    }

    protected OrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
