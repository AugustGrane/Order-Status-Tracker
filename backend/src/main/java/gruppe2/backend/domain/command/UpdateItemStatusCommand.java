package gruppe2.backend.domain.command;

import gruppe2.backend.domain.Order;
import gruppe2.backend.domain.OrderStatus;

public class UpdateItemStatusCommand implements OrderCommand {
    private final Long itemId;
    private final OrderStatus newStatus;

    public UpdateItemStatusCommand(Long itemId, OrderStatus newStatus) {
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID cannot be null");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }
        this.itemId = itemId;
        this.newStatus = newStatus;
    }

    @Override
    public void execute(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("OrderModel cannot be null");
        }
        order.updateItemStatus(itemId, newStatus);
    }

    public Long getItemId() {
        return itemId;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    @Override
    public String toString() {
        return String.format("UpdateItemStatusCommand{itemId=%d, newStatus=%s}", 
            itemId, newStatus);
    }
}
