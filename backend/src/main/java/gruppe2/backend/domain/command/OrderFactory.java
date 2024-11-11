package gruppe2.backend.domain.command;

import gruppe2.backend.domain.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Factory for creating Order objects.
 * This complements the Command pattern by handling initial order creation.
 */
public class OrderFactory {
    public static Order createOrder(
            OrderId id,
            CustomerInfo customerInfo,
            Map<Long, Integer> items,
            Map<Long, Integer> processingTimes,
            boolean priority) {
        
        OrderTimeline timeline = new OrderTimeline(LocalDateTime.now(), priority);
        OrderEstimation estimation = new OrderEstimation(items, processingTimes, priority);

        return new Order.Builder()
            .withId(id)
            .withCustomerInfo(customerInfo)
            .withTimeline(timeline)
            .withEstimation(estimation)
            .build();
    }
}
