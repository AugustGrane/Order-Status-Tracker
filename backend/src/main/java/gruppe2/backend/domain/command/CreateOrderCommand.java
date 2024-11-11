package gruppe2.backend.domain.command;

import gruppe2.backend.domain.*;
import java.time.LocalDateTime;
import java.util.Map;

public class CreateOrderCommand implements OrderCommand {
    private final OrderId id;
    private final CustomerInfo customerInfo;
    private final Map<Long, Integer> items;
    private final Map<Long, Integer> processingTimes;
    private final boolean priority;

    public CreateOrderCommand(
            OrderId id,
            CustomerInfo customerInfo,
            Map<Long, Integer> items,
            Map<Long, Integer> processingTimes,
            boolean priority) {
        this.id = id;
        this.customerInfo = customerInfo;
        this.items = items;
        this.processingTimes = processingTimes;
        this.priority = priority;
    }

    @Override
    public void execute(Order order) {
        OrderTimeline timeline = new OrderTimeline(LocalDateTime.now(), priority);
        OrderEstimation estimation = new OrderEstimation(items, processingTimes, priority);

        Order newOrder = new Order.Builder()
            .withId(id)
            .withCustomerInfo(customerInfo)
            .withTimeline(timeline)
            .withEstimation(estimation)
            .build();

        // Copy state from new order to existing order
        // This is needed because we can't return a new order from execute()
        order = newOrder;
    }

    public OrderId getId() {
        return id;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public Map<Long, Integer> getItems() {
        return items;
    }

    public Map<Long, Integer> getProcessingTimes() {
        return processingTimes;
    }

    public boolean isPriority() {
        return priority;
    }
}
