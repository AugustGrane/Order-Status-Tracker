package gruppe2.backend.service;

import gruppe2.backend.domain.OrderProgress;
import gruppe2.backend.domain.OrderStatus;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

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
        orderDetails.setDifferentSteps(Arrays.asList(1L, 2L, 3L));
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
    void moveToNextStep_WithValidId_ShouldReturnSuccess() {
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
        assertEquals(orderDetails.getCurrentStepIndex() + 1, progress.getCurrentStep());
        assertEquals(orderDetails.getDifferentSteps().size(), progress.getTotalSteps());
        assertEquals(orderDetails.getUpdated(), progress.getStepHistory());
    }

    /*@Test
    void moveToPrevStep_WithValidId_ShouldReturnSuccess() {
        // Arrange
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setId(1L);
        orderDetails.setCurrentStepIndex(1);
        orderDetails.setDifferentSteps(new Long[]{1L, 2L, 3L});
        orderDetails.setUpdated(new HashMap<>());

        when(orderProductTypeRepository.findById(1L)).thenReturn(Optional.of(orderDetails));
        when(orderProductTypeRepository.save(any(OrderDetails.class))).thenReturn(orderDetails);

        // Act
        Map<String, Object> result = orderProgressService.moveToPreviousStep(1L);  // Ensure method name matches

        // Assert
        assertThat(result).containsKey("currentStep");
        assertThat(result).containsKey("totalSteps");
        verify(orderProductTypeRepository).save(any(OrderDetails.class));
    }*/

    /*@Test
    void getProgress_WithValidId_ShouldReturnProgressInfo() {
        // Arrange
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setId(1L);
        orderDetails.setCurrentStepIndex(1);
        orderDetails.setDifferentSteps(new Long[]{1L, 2L, 3L});
        Map<Long, LocalDateTime> updates = new HashMap<>();
        updates.put(1L, LocalDateTime.now());
        orderDetails.setUpdated(updates);

        when(orderProductTypeRepository.findById(1L)).thenReturn(Optional.of(orderDetails));

        // Act
        Map<String, Object> progress = orderProgressService.getProgress(1L);

        // Assert
        assertThat(progress).containsKey("currentStep");
        assertThat(progress).containsKey("totalSteps");
        assertThat(progress).containsKey("percentComplete");
        assertThat(progress).containsKey("stepHistory");
        verify(orderProductTypeRepository).findById(1L);
    }

    @Test
    void findOrderDetails_WithValidId_ShouldReturnOrderDetails() {
        when(orderProductTypeRepository.findById(orderDetails.getId())).thenReturn(Optional.of(orderDetails)); // Telling Mockito to return the corresponding orderDetails if ID is valid

        // Act
        OrderDetails result = orderProgressService.findOrderDetails(orderDetails.getId()); // Result is the returned data

        // Assert
        assertNotNull(result); // Result should contain data
        assertEquals(orderDetails.getId(), result.getId()); // The ID passed should be the ID returned
        assertEquals(1, result.getCurrentStepIndex()); // Should be at step 1
        assertEquals(orderDetails.getDifferentSteps(), result.getDifferentSteps()); // Checking that it has default steps
        assertEquals(updates, result.getUpdated()); // Checking that it has the date hashmap and that it matches
    }

    @Test
    void findOrderDetails_WithoutValidId_ShouldThrowException() {

        Long invalidId = 2L; // Invalid ID

        when(orderProductTypeRepository.findById(invalidId)).thenReturn(Optional.empty()); // Telling Mockito to return nothing if ID is invalid

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> { // Invalid ID -> Throw error
            orderProgressService.findOrderDetails(invalidId);
        });

        // Assert
        assertEquals("OrderDetails not found with id: " + invalidId, exception.getMessage()); // Check error message
    }

    @Test
    void createOrderStatus_WithValidOrderDetails_ShouldCreateOrderStatus() {

        // Act
        OrderStatus statusResult = orderProgressService.createOrderStatus(orderDetails);

        assertNotNull(statusResult); // Result should contain data
        assertEquals(orderDetails.getDifferentSteps(), statusResult.getSteps()); // Checks that the array of steps are the same in both objects
        assertEquals(orderDetails.getCurrentStepIndex(), statusResult.getCurrentStepIndex()); // Checks that current step is the same
        assertEquals(orderDetails.getUpdated(), statusResult.getStatusUpdates()); // Checks that dates are the same
    }

    /* Add test for createOrderStatus with invalid object maybe?

    @Test
    void validateGenericProductType() {


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderProgressService.validateGenericProductType(orderDetails);
        });

        assertEquals("Item cannot change step while item is generic product type", exception.getMessage());
    }

    @Test
    void updateOrderDetails_WithValidOrderDetailsAndOrderStatus_ShouldUpdateOrderDetails() {
        orderDetails.setOrderId(1L);
        Order order = new Order(1L, "Test Name", false, "", LocalDateTime.now(), 1);

        when(orderProductTypeRepository.save(any(OrderDetails.class))).thenReturn(orderDetails);
        when(orderRepository.findById(orderDetails.getOrderId())).thenReturn(Optional.of(order));



    }

    @Test
    void updateOrderDetails_WithInvalidOrderId_ShouldThrowException() {
        orderDetails.setOrderId(2L);
        OrderStatus orderStatus = orderProgressService.createOrderStatus(orderDetails);


        when(orderRepository.findById(orderDetails.getOrderId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderProgressService.updateOrderDetails(orderDetails, orderStatus);
        });

        assertEquals("Order not found", exception.getMessage());
    }*/
}
