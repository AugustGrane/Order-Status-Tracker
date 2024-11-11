package gruppe2.backend.service;

import gruppe2.backend.domain.OrderProgress;
import gruppe2.backend.domain.OrderStatus;
import gruppe2.backend.domain.OrderTimeline;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.repository.OrderProductTypeRepository;
import gruppe2.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public OrderProgress moveToNextStep(Long orderDetailsId) {
        OrderDetails orderDetails = findOrderDetails(orderDetailsId);
        validateGenericProductType(orderDetails);

        OrderStatus status = createOrderStatus(orderDetails);
        status.moveToNextStep();
        
        updateOrderDetails(orderDetails, status);
        return status.toProgress();
    }

    public OrderProgress moveToPreviousStep(Long orderDetailsId) {
        OrderDetails orderDetails = findOrderDetails(orderDetailsId);
        validateGenericProductType(orderDetails);

        OrderStatus status = createOrderStatus(orderDetails);
        status.moveToPreviousStep();
        
        updateOrderDetails(orderDetails, status);
        return status.toProgress();
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
}
