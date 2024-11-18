package gruppe2.backend.service;

import gruppe2.backend.domain.*;
import gruppe2.backend.domain.command.UpdateItemStatusCommand;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.repository.OrderProductTypeRepository;
import gruppe2.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderProgressService {
    private final OrderProductTypeRepository orderProductTypeRepository;
    private final OrderRepository orderRepository;

    public OrderProgressService(
            OrderProductTypeRepository orderProductTypeRepository,
            OrderRepository orderRepository) {
        this.orderProductTypeRepository = orderProductTypeRepository;
        this.orderRepository = orderRepository;
    }

    public OrderProgress getProgress(Long orderDetailsId) {
        OrderDetails orderDetails = findOrderDetails(orderDetailsId);
        OrderStatus status = createOrderStatus(orderDetails);
        return status.toProgress();
    }

    @Transactional
    public OrderProgress moveToNextStep(Long orderDetailsId) {
        OrderDetails orderDetails = findOrderDetails(orderDetailsId);
        validateGenericProductType(orderDetails);

        // Create current order status
        OrderStatus status = createOrderStatus(orderDetails);

        if (!status.canMoveToNextStep()) {
            throw new IllegalStateException("Cannot move to next step: already at final step");
        }

        // Move to next step
        status.moveToNextStep();

        // Create and execute the command
        UpdateItemStatusCommand command = new UpdateItemStatusCommand(
            orderDetails.getItem().getId(),
            status
        );

        // Create Order domain object and execute command
        Order order = createOrderFromDetails(orderDetails);
        command.execute(order);

        // Update persistence
        updateOrderDetails(orderDetails, status);

        return status.toProgress();
    }

    @Transactional
    public OrderProgress moveToPreviousStep(Long orderDetailsId) {
        OrderDetails orderDetails = findOrderDetails(orderDetailsId);
        validateGenericProductType(orderDetails);

        // Create current order status
        OrderStatus status = createOrderStatus(orderDetails);

        if (!status.canMoveToPreviousStep()) {
            throw new IllegalStateException("Cannot move to previous step: already at first step");
        }

        // Move to previous step
        status.moveToPreviousStep();

        // Create and execute the command
        UpdateItemStatusCommand command = new UpdateItemStatusCommand(
            orderDetails.getItem().getId(),
            status
        );

        // Create Order domain object and execute command
        Order order = createOrderFromDetails(orderDetails);
        command.execute(order);

        // Update persistence
        updateOrderDetails(orderDetails, status);

        return status.toProgress();
    }

    private Order createOrderFromDetails(OrderDetails orderDetails) {
        var orderEntity = orderRepository.findById(orderDetails.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

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

    private OrderDetails findOrderDetails(Long orderDetailsId) {
        return orderProductTypeRepository.findById(orderDetailsId)
                .orElseThrow(() -> new RuntimeException("OrderDetails not found with id: " + orderDetailsId));
    }

    private OrderStatus createOrderStatus(OrderDetails orderDetails) {
        return new OrderStatus(
            orderDetails.getDifferentSteps(),
            orderDetails.getCurrentStepIndex(),
            orderDetails.getUpdated()
        );
    }

    private void validateGenericProductType(OrderDetails orderDetails) {
        if (orderDetails.getItem().getProductTypeId() == 0) {
            throw new IllegalStateException("Item cannot change step while item is generic product type");
        }
    }

    private void updateOrderDetails(OrderDetails orderDetails, OrderStatus status) {
        orderDetails.setCurrentStepIndex(status.getCurrentStepIndex());
        orderDetails.setUpdated(status.getStatusUpdates());
        orderProductTypeRepository.save(orderDetails);

        // Create timeline for status tracking
        var order = orderRepository.findById(orderDetails.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderTimeline timeline = new OrderTimeline(order.getOrderCreated(), order.isPriority());
        timeline.recordItemStatus(orderDetails.getItem().getId(),
                                status.getCurrentStepId(),
                                LocalDateTime.now());
    }

    @Transactional
    public OrderProgress moveToStep(Long orderDetailsId, int nextStepIndex) {
        OrderDetails orderDetails = findOrderDetails(orderDetailsId);
        validateGenericProductType(orderDetails);

        // Create current order status
        OrderStatus status = createOrderStatus(orderDetails);

        if (!status.canMoveToNextStep()) {
            throw new IllegalStateException("Cannot move to next step: already at final step");
        }

        // Move to next step
        status.moveToStep(nextStepIndex);

        // Create and execute the command
        UpdateItemStatusCommand command = new UpdateItemStatusCommand(
                orderDetails.getItem().getId(),
                status
        );

        // Create Order domain object and execute command
        Order order = createOrderFromDetails(orderDetails);
        command.execute(order);

        // Update persistence
        updateOrderDetails(orderDetails, status);

        return status.toProgress();
    }




}
