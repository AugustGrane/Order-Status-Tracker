package gruppe2.backend.domain.exception;

import gruppe2.backend.domain.OrderStatus;

public class InvalidStatusTransitionException extends OrderException {
    private final Long itemId;
    private final OrderStatus currentStatus;
    private final OrderStatus targetStatus;

    public InvalidStatusTransitionException(Long itemId, OrderStatus currentStatus, OrderStatus targetStatus) {
        super(String.format(
            "Invalid status transition for item %d: cannot move from step %d to step %d",
            itemId,
            currentStatus.getCurrentStepIndex(),
            targetStatus.getCurrentStepIndex()
        ));
        this.itemId = itemId;
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }

    public Long getItemId() {
        return itemId;
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }

    public OrderStatus getTargetStatus() {
        return targetStatus;
    }
}
