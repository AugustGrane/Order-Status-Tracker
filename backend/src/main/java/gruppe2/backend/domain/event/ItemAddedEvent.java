package gruppe2.backend.domain.event;

import gruppe2.backend.domain.OrderItem;

public class ItemAddedEvent extends OrderEvent {
    private final OrderItem item;

    public ItemAddedEvent(Long orderId, OrderItem item) {
        super(orderId);
        this.item = item;
    }

    public OrderItem getItem() {
        return item;
    }

    @Override
    public String getEventType() {
        return "ITEM_ADDED";
    }
}
