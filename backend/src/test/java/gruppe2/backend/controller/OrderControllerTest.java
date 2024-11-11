package gruppe2.backend.controller;

import gruppe2.backend.model.Order;
import gruppe2.backend.model.Item;
import gruppe2.backend.model.ProductType;
import gruppe2.backend.dto.OrderDTO;
import gruppe2.backend.dto.OrderDetailsWithStatusDTO;
import gruppe2.backend.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private Order testOrder;
    private OrderDTO testOrderDTO;

    @BeforeEach
    void setUp() {
        // Prepare OrderDTO with item map
        Map<Long, Integer> items = new HashMap<>();
        items.put(1L, 2); // item_id 1, quantity 2

        testOrderDTO = new OrderDTO(
            null, // id
            "Test Customer", 
            true, // priority 
            "Test Notes", 
            items
        );

        // Create a test Order
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerName("Test Customer");
        testOrder.setPriority(true);
        testOrder.setNotes("Test Notes");
        testOrder.setOrderCreated(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Order Creation Tests")
    class OrderCreationTests {
        @Test
        @DisplayName("Create Order Successfully")
        void createOrderSuccessfully() {
            // Arrange
            when(orderService.createOrder(any(OrderDTO.class))).thenReturn(testOrder);

            // Act
            ResponseEntity<Order> response = orderController.createOrder(testOrderDTO);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            verify(orderService, times(1)).createOrder(testOrderDTO);
        }

        @Test
        @DisplayName("Create Order with Empty Items")
        void createOrderWithEmptyItems() {
            // Arrange
            OrderDTO emptyOrderDTO = new OrderDTO(
                null, 
                "Test Customer", 
                true, 
                "Test Notes", 
                Collections.emptyMap()
            );
            when(orderService.createOrder(any(OrderDTO.class))).thenThrow(new RuntimeException("Empty items"));

            // Act
            ResponseEntity<Order> response = orderController.createOrder(emptyOrderDTO);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
        }
    }

    @Nested
    @DisplayName("Order Retrieval Tests")
    class OrderRetrievalTests {
        @Test
        @DisplayName("Get Order Details by Valid ID")
        void getOrderDetailsByValidId() {
            // Arrange
            Long orderId = 1L;
            List<OrderDetailsWithStatusDTO> mockOrderDetails = Collections.emptyList();
            when(orderService.getOrderDetails(orderId)).thenReturn(mockOrderDetails);

            // Act
            ResponseEntity<List<OrderDetailsWithStatusDTO>> response = orderController.getOrderDetails(orderId);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            verify(orderService, times(1)).getOrderDetails(orderId);
        }

        @Test
        @DisplayName("Get Order Details by Non-Existent ID")
        void getOrderDetailsByNonExistentId() {
            // Arrange
            Long orderId = 999L;
            when(orderService.getOrderDetails(orderId)).thenThrow(new RuntimeException("Order not found"));

            // Act
            ResponseEntity<List<OrderDetailsWithStatusDTO>> response = orderController.getOrderDetails(orderId);

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }
    }
}
