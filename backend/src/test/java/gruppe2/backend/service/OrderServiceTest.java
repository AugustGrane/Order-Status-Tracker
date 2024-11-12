package gruppe2.backend.service;

import gruppe2.backend.domain.OrderStatus;
import gruppe2.backend.model.Item;
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
    private OrderProgressService orderService;  // Update this to the correct service class if needed

    OrderDetails orderDetails = null; // Readable to the whole test script
    Map<Long, LocalDateTime> updates; // Same goes for this one

    @BeforeEach
    void beforeEach() {
        orderDetails = new OrderDetails();
        orderDetails.setId(1L);
        orderDetails.setCurrentStepIndex(1);
        orderDetails.setDifferentSteps(new Long[]{1L, 2L, 3L});
        updates = new HashMap<>();
        updates.put(1L, LocalDateTime.now());
        orderDetails.setUpdated(updates);
    }

    /*@Test
    void moveToNextStep_WithValidId_ShouldReturnSuccess() {
        // Arrange
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setId(1L);
        orderDetails.setCurrentStepIndex(0);
        orderDetails.setDifferentSteps(new Long[]{1L, 2L, 3L});
        orderDetails.setUpdated(new HashMap<>());

        when(orderProductTypeRepository.findById(1L)).thenReturn(Optional.of(orderDetails));
        when(orderProductTypeRepository.save(any(OrderDetails.class))).thenReturn(orderDetails);

        // Act
        Map<String, Object> result = orderService.moveToNextStep(1L);

        // Assert
        assertThat(result).containsKey("currentStep");
        assertThat(result).containsKey("totalSteps");
        verify(orderProductTypeRepository).save(any(OrderDetails.class));
    }

    @Test
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
        Map<String, Object> result = orderService.moveToPreviousStep(1L);  // Ensure method name matches

        // Assert
        assertThat(result).containsKey("currentStep");
        assertThat(result).containsKey("totalSteps");
        verify(orderProductTypeRepository).save(any(OrderDetails.class));
    }

    @Test
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
        Map<String, Object> progress = orderService.getProgress(1L);

        // Assert
        assertThat(progress).containsKey("currentStep");
        assertThat(progress).containsKey("totalSteps");
        assertThat(progress).containsKey("percentComplete");
        assertThat(progress).containsKey("stepHistory");
        verify(orderProductTypeRepository).findById(1L);
    }*/

    @Test
    void findOrderDetails_WithValidId_ShouldReturnOrderDetails() {
        when(orderProductTypeRepository.findById(orderDetails.getId())).thenReturn(Optional.of(orderDetails)); // Telling Mockito to return the corresponding orderDetails if ID is valid

        // Act
        OrderDetails result = orderService.findOrderDetails(orderDetails.getId()); // Result is the returned data

        // Assert
        assertNotNull(result); // Result should contain data
        assertEquals(orderDetails.getId(), result.getId()); // The ID passed should be the ID returned
        assertEquals(1, result.getCurrentStepIndex()); // Should be at step 1
        assertArrayEquals(new Long[]{1L, 2L, 3L}, result.getDifferentSteps()); // Checking that it has default steps
        assertEquals(updates, result.getUpdated()); // Checking that it has the date hashmap and that it matches
    }

    @Test
    void findOrderDetails_WithoutValidId_ShouldThrowException() {

        Long invalidId = 2L; // Invalid ID

        when(orderProductTypeRepository.findById(invalidId)).thenReturn(Optional.empty()); // Telling Mockito to return nothing if ID is invalid

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> { // Invalid ID -> Throw error
            orderService.findOrderDetails(invalidId);
        });

        // Assert
        assertEquals("OrderDetails not found with id: " + invalidId, exception.getMessage()); // Check error message
    }

    @Test
    void createOrderStatus_WithValidOrderDetails_ShouldCreateOrderStatus() {

        // Act
        OrderStatus statusResult = orderService.createOrderStatus(orderDetails);

        assertNotNull(statusResult); // Result should contain data
        assertArrayEquals(orderDetails.getDifferentSteps(), statusResult.getSteps()); // Checks that the array of steps are the same in both objects
        assertEquals(orderDetails.getCurrentStepIndex(), statusResult.getCurrentStepIndex()); // Checks that current step is the same
        assertEquals(orderDetails.getUpdated(), statusResult.getStatusUpdates()); // Checks that dates are the same
    }

    /* Add test for createOrderStatus with invalid object maybe? */

    @Test
    void validateGenericProductType() {
        Item item = new Item(1L, "T-shirt m. print", 0L);

        orderDetails.setItem(item);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.validateGenericProductType(orderDetails);
        });

        assertEquals("Item cannot change step while item is generic product type", exception.getMessage());
    }

    @Test
    void updateOrderDetails_WithValidOrderDetailsAndOrderStatus_ShouldUpdateOrderDetails() {}
}
