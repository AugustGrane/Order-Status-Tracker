package gruppe2.backend.domain.exception;

public class InvalidProductTypeTransitionException extends OrderException {
    private final Long itemId;

    public InvalidProductTypeTransitionException(Long itemId) {
        super(String.format(
            "Invalid product type transition for item %d: can only change product type for generic items",
            itemId
        ));
        this.itemId = itemId;
    }

    public InvalidProductTypeTransitionException(Long itemId, String message) {
        super(String.format(
            "Invalid product type transition for item %d: %s",
            itemId,
            message
        ));
        this.itemId = itemId;
    }

    public Long getItemId() {
        return itemId;
    }
}
