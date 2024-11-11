package gruppe2.backend.domain.event;

import gruppe2.backend.domain.OrderStatus;

public class ItemStatusChangedEvent extends OrderEvent {
    private final Long itemId;
    private final OrderStatus oldStatus;
    private final OrderStatus newStatus;

    public ItemStatusChangedEvent(Long orderId, Long itemId, OrderStatus oldStatus, OrderStatus newStatus) {
        super(orderId);
        this.itemId = itemId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public Long getItemId() {
        return itemId;
    }

    public OrderStatus getOldStatus() {
        return oldStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    @Override
    public String getEventType() {
        return "ITEM_STATUS_CHANGED";
    }

    public boolean isProgressionEvent() {
        return newStatus.getCurrentStepIndex() > oldStatus.getCurrentStepIndex();
    }

    public boolean isRegressionEvent() {
        return newStatus.getCurrentStepIndex() < oldStatus.getCurrentStepIndex();
    }
}
