package gruppe2.backend.service;

import gruppe2.backend.domain.OrderProgress;
import gruppe2.backend.model.Item;
import gruppe2.backend.model.Order;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.repository.OrderRepository;
import gruppe2.backend.repository.OrderProductTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderProgressServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProductTypeRepository orderProductTypeRepository;

    @InjectMocks
    private OrderProgressService orderProgressService;  // Update this to the correct service class if needed

    OrderDetails orderDetails = null; // Readable to the whole test script
    Map<Long, LocalDateTime> updates; // Same goes for this one
    Item item = null;
    Order order = null;

    @BeforeEach
    void beforeEach() {
        orderDetails = new OrderDetails(); // Create order details
        orderDetails.setId(1L);
        orderDetails.setCurrentStepIndex(1);
        orderDetails.setDifferentSteps(List.of(new Long[]{1L, 7L, 3L}));
        updates = new HashMap<>();
        updates.put(1L, LocalDateTime.now());
        orderDetails.setUpdated(updates);

        //status.
        item = new Item();
        item.setId(1L); // Create item for order details
        item.setName("T-shirt m. print");
        item.setProductTypeId(1L);
        orderDetails.setItem(item);

        order = new Order();
        order.setId(1L); // Create order for order details
        order.setCustomerName("Test Name");
        order.setPriority(false);
        order.setNotes("");
        order.setTotalEstimatedTime(1);
        orderDetails.setOrderId(1L);
    }

    @Test
    void moveToNextStep_Successful() {
        // Arrange happens beforeEach

        // Mockito
        when(orderProductTypeRepository.findById(orderDetails.getId())).thenReturn(Optional.of(orderDetails)); // Used in "findOrderDetails"
        // Don't need for validateGenericType
        // Don't need for createOrderStatus
        // Don't need for .canMoveToNextStep
        // Don't need for UpdateItemStatusCommand
        when(orderRepository.findById(orderDetails.getOrderId())).thenReturn(Optional.of(order)); // Used in createOrderFromDetails and updateOrderDetails
        when(orderProductTypeRepository.save(any(OrderDetails.class))).thenReturn(orderDetails); // Used in updateOrderDetails

        // Act
        OrderProgress progress = orderProgressService.moveToNextStep(orderDetails.getId()); // Calling function supposing that all objects are correct

        // Assert
        assertNotNull(progress);
        assertNotNull(progress.getCurrentStepId());
        assertEquals(orderDetails.getCurrentStepIndex(), progress.getCurrentStep());
        assertEquals(orderDetails.getDifferentSteps().size(), progress.getTotalSteps());
        assertEquals(orderDetails.getUpdated(), progress.getStepHistory());
    }

    @Test
    void moveToNextStep_Failed_IsGenericType() {
        // Arrange happens beforeEach
        item.setProductTypeId(0L); // Item has generic productType

        // Mockito
        when(orderProductTypeRepository.findById(orderDetails.getId())).thenReturn(Optional.of(orderDetails)); // Used in "findOrderDetails"

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderProgressService.moveToNextStep(orderDetails.getId());
        });

        assertEquals("Item cannot change step while item is generic product type", exception.getMessage());
    }

    @Test
    void moveToNextStep_Failed_AtFinalStep() {
        // Arrange happens beforeEach
        orderDetails.setCurrentStepIndex(orderDetails.getDifferentSteps().size() - 1);

        // Mockito
        when(orderProductTypeRepository.findById(orderDetails.getId())).thenReturn(Optional.of(orderDetails)); // Used in "findOrderDetails"
        // Used in createOrderFromDetails and updateOrderDetails. Lenient() as we only call it with createOrderFromDetails but not with updateOrderDetails
        lenient().when(orderRepository.findById(orderDetails.getOrderId())).thenReturn(Optional.of(order));

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderProgressService.moveToNextStep(orderDetails.getId());
        });

        assertEquals("Cannot move to next step: already at final step", exception.getMessage());
    }

    @Test
    void moveToPreviousStep_Successful() {
        // Arrange happens beforeEach

        // Mockito
        when(orderProductTypeRepository.findById(orderDetails.getId())).thenReturn(Optional.of(orderDetails));
        when(orderRepository.findById(orderDetails.getOrderId())).thenReturn(Optional.of(order));
        when(orderProductTypeRepository.save(any(OrderDetails.class))).thenReturn(orderDetails);

        // Act
        OrderProgress progress = orderProgressService.moveToPreviousStep(orderDetails.getId()); // Calling function supposing that all objects are correct

        // Assert
        assertNotNull(progress);
        assertNotNull(progress.getCurrentStepId());
        assertEquals(orderDetails.getCurrentStepIndex(), progress.getCurrentStep());
        assertEquals(orderDetails.getDifferentSteps().size(), progress.getTotalSteps());
        assertEquals(orderDetails.getUpdated(), progress.getStepHistory());
    }

    @Test
    void moveToPreviousStep_Failed_IsGenericType() {
        // Arrange happens beforeEach
        item.setProductTypeId(0L); // Item has generic productType

        // Mockito
        when(orderProductTypeRepository.findById(orderDetails.getId())).thenReturn(Optional.of(orderDetails)); // Used in "findOrderDetails"

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderProgressService.moveToPreviousStep(orderDetails.getId());
        });

        assertEquals("Item cannot change step while item is generic product type", exception.getMessage());
    }

    @Test
    void moveToPreviousStep_Failed_AtFirstStep() {
        // Arrange happens beforeEach
        orderDetails.setCurrentStepIndex(0);

        // Mockito
        when(orderProductTypeRepository.findById(orderDetails.getId())).thenReturn(Optional.of(orderDetails)); // Used in "findOrderDetails"
        // Used in createOrderFromDetails and updateOrderDetails. Lenient() as we only call it with createOrderFromDetails but not with updateOrderDetails
        lenient().when(orderRepository.findById(orderDetails.getOrderId())).thenReturn(Optional.of(order));

        // Act
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderProgressService.moveToPreviousStep(orderDetails.getId());
        });

        assertEquals("Cannot move to previous step: already at first step", exception.getMessage());
    }
}
