package gruppe2.backend.service.util;

import gruppe2.backend.domain.*;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.model.OrderModel;
import gruppe2.backend.repository.OrderRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for creating Order domain objects from various sources.
 * This class centralizes the order creation logic that was previously duplicated
 * across services.
 */
public final class OrderFactory {
    private OrderFactory() {
        // Prevent instantiation
    }

    /**
     * Creates an Order domain object from OrderDetails.
     * This method centralizes the previously duplicated logic from
     * OrderProgressService and ProductTypeService.
     *
     * @param orderDetails The order details entity
     * @param orderRepository Repository to fetch the parent order
     * @return Order domain object
     * @throws RuntimeException if the order is not found
     */
    public static Order createFromOrderDetails(OrderDetails orderDetails, OrderRepository orderRepository) {
        var orderEntity = orderRepository.findById(orderDetails.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderDetails.getOrderId()));

        CustomerInfo customerInfo = new CustomerInfo(
            orderEntity.getCustomerName(),
            orderEntity.getNotes(),
            orderEntity.isPriority()
        );

        OrderTimeline timeline = new OrderTimeline(
            orderEntity.getOrderCreated(),
            orderEntity.isPriority()
        );

        Map<Long, Integer> items = new HashMap<>();
        items.put(orderDetails.getItem().getId(), orderDetails.getItemAmount());

        Map<Long, Integer> processingTimes = new HashMap<>();
        processingTimes.put(orderDetails.getItem().getId(), orderEntity.getTotalEstimatedTime());

        OrderEstimation estimation = new OrderEstimation(
            items,
            processingTimes,
            orderEntity.isPriority()
        );

        return new Order.Builder()
            .withId(new OrderId(orderEntity.getId()))
            .withCustomerInfo(customerInfo)
            .withTimeline(timeline)
            .withEstimation(estimation)
            .build();
    }

    /**
     * Creates an Order domain object from an OrderModel.
     * This method provides a clean way to convert between persistence and domain models.
     *
     * @param orderModel The order model entity
     * @return Order domain object
     */
    public static Order createFromModel(OrderModel orderModel) {
        CustomerInfo customerInfo = new CustomerInfo(
            orderModel.getCustomerName(),
            orderModel.getNotes(),
            orderModel.isPriority()
        );

        OrderTimeline timeline = new OrderTimeline(
            orderModel.getOrderCreated(),
            orderModel.isPriority()
        );

        OrderEstimation estimation = new OrderEstimation(
            new HashMap<>(), // These would be populated from OrderDetails if needed
            new HashMap<>(),
            orderModel.isPriority()
        );

        return new Order.Builder()
            .withId(new OrderId(orderModel.getId()))
            .withCustomerInfo(customerInfo)
            .withTimeline(timeline)
            .withEstimation(estimation)
            .build();
    }
}
