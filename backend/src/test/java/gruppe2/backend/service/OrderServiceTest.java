package gruppe2.backend.service;

import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.repository.OrderRepository;
import gruppe2.backend.repository.OrderProductTypeRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProductTypeRepository orderProductTypeRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
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
        Map<String, Object> result = orderService.moveToPrevStep(1L);

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
    }
}
