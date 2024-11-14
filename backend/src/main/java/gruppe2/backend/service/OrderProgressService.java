package gruppe2.backend.service;

import gruppe2.backend.domain.*;
import gruppe2.backend.domain.command.UpdateItemStatusCommand;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.repository.OrderProductTypeRepository;
import gruppe2.backend.repository.OrderRepository;
import gruppe2.backend.service.util.OrderFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing order progress and status updates.
 * This service handles the movement of orders through their workflow steps.
 */
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

    /**
     * Retrieves the current progress of an order.
     *
     * @param orderDetailsId ID of the order details to check
     * @return Current order progress
     * @throws RuntimeException if order details not found
     */
    @Transactional(readOnly = true)
    public OrderProgress getProgress(Long orderDetailsId) {
        OrderDetails orderDetails = findOrderDetails(orderDetailsId);
        OrderStatus status = createOrderStatus(orderDetails);
        return status.toProgress();
    }

    /**
     * Moves an order to its next step in the workflow.
     *
     * @param orderDetailsId ID of the order details to move
     * @return Updated order progress
     * @throws IllegalStateException if the order cannot move to next step
     */
    @Transactional
    public OrderProgress moveToNextStep(Long orderDetailsId) {
        OrderDetails orderDetails = findOrderDetails(orderDetailsId);
        validateGenericProductType(orderDetails);

        OrderStatus status = createOrderStatus(orderDetails);
        if (!status.canMoveToNextStep()) {
            throw new IllegalStateException("Cannot move to next step: already at final step");
        }

        status.moveToNextStep();

        UpdateItemStatusCommand command = new UpdateItemStatusCommand(
            orderDetails.getItem().getId(),
            status
        );

        Order order = OrderFactory.createFromOrderDetails(orderDetails, orderRepository);
        command.execute(order);

        updateOrderDetails(orderDetails, status);

        return status.toProgress();
    }

    /**
     * Moves an order to its previous step in the workflow.
     *
     * @param orderDetailsId ID of the order details to move
     * @return Updated order progress
     * @throws IllegalStateException if the order cannot move to previous step
     */
    @Transactional
    public OrderProgress moveToPreviousStep(Long orderDetailsId) {
        OrderDetails orderDetails = findOrderDetails(orderDetailsId);
        validateGenericProductType(orderDetails);

        OrderStatus status = createOrderStatus(orderDetails);
        if (!status.canMoveToPreviousStep()) {
            throw new IllegalStateException("Cannot move to previous step: already at first step");
        }

        status.moveToPreviousStep();

        UpdateItemStatusCommand command = new UpdateItemStatusCommand(
            orderDetails.getItem().getId(),
            status
        );

        Order order = OrderFactory.createFromOrderDetails(orderDetails, orderRepository);
        command.execute(order);

        updateOrderDetails(orderDetails, status);

        return status.toProgress();
    }

    private OrderDetails findOrderDetails(Long orderDetailsId) {
        return orderProductTypeRepository.findById(orderDetailsId)
                .orElseThrow(() -> new RuntimeException("Order details not found: " + orderDetailsId));
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
        Order order = OrderFactory.createFromOrderDetails(orderDetails, orderRepository);
        OrderTimeline timeline = order.getTimeline();
        timeline.recordItemStatus(orderDetails.getItem().getId(),
                                status.getCurrentStepId(),
                                LocalDateTime.now());
    }
}
