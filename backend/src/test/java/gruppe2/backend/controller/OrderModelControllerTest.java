package gruppe2.backend.controller;

import gruppe2.backend.model.OrderModel;
import gruppe2.backend.model.Item;
import gruppe2.backend.model.ProductType;
import gruppe2.backend.model.StatusDefinition;
import gruppe2.backend.domain.OrderProgress;
import gruppe2.backend.dto.*;
import gruppe2.backend.service.*;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderModelControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private ItemService itemService;

    @Mock
    private ProductTypeService productTypeService;

    @Mock
    private OrderProgressService orderProgressService;

    @InjectMocks
    private OrderController orderController;

    private OrderModel testOrderModel;
    private OrderDTO testOrderDTO;
    private Item testItem;
    private ItemDTO testItemDTO;
    private ProductType testProductType;
    private ProductTypeDTO testProductTypeDTO;
    private StatusDefinition testStatusDefinition;
    private StatusDefinitionDTO testStatusDefinitionDTO;
    private OrderProgress testOrderProgress;

    @BeforeEach
    void setUp() {
        // Prepare OrderDTO with item map
        Map<Long, Integer> items = new HashMap<>();
        items.put(1L, 2); // item_id 1, quantity 2

        testOrderDTO = new OrderDTO(
            null,
            "Test Customer", 
            true, 
            "Test Notes", 
            items,
            ""
        );

        // Create a test OrderModel
        testOrderModel = new OrderModel();
        testOrderModel.setId(1L);
        testOrderModel.setCustomerName("Test Customer");
        testOrderModel.setPriority(true);
        testOrderModel.setNotes("Test Notes");
        testOrderModel.setOrderCreated(LocalDateTime.now());

        // Create test Item and ItemDTO
        testItem = new Item();
        testItem.setId(1L);
        testItemDTO = new ItemDTO("Test Item", 1L, 1L, "test-image.jpg");

        // Create test ProductType and ProductTypeDTO
        testProductType = new ProductType();
        testProductType.setId(1L);
        testProductTypeDTO = new ProductTypeDTO("Test Type", new Long[]{1L, 2L});

        // Create test StatusDefinition and StatusDefinitionDTO
        testStatusDefinition = new StatusDefinition();
        testStatusDefinition.setId(1L);
        testStatusDefinitionDTO = new StatusDefinitionDTO("Test Status", "Test Description", "test-status.jpg");

        // Create test OrderProgress
        Map<Long, LocalDateTime> stepHistory = new HashMap<>();
        stepHistory.put(1L, LocalDateTime.now());
        testOrderProgress = new OrderProgress(1, 5, 1L, stepHistory);
    }

    @Nested
    @DisplayName("Item Creation Tests")
    class ItemCreationTests {
        @Test
        @DisplayName("Create Item Successfully")
        void createItemSuccessfully() {
            when(itemService.createItem(any(ItemDTO.class))).thenReturn(testItem);

            ResponseEntity<Item> response = orderController.createItem(testItemDTO);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testItem, response.getBody());
            verify(itemService).createItem(testItemDTO);
        }

        @Test
        @DisplayName("Create Item Failure")
        void createItemFailure() {
            when(itemService.createItem(any(ItemDTO.class))).thenThrow(new RuntimeException("Failed to create item"));

            ResponseEntity<Item> response = orderController.createItem(testItemDTO);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Create Item with Null DTO")
        void createItemWithNullDTO() {
            ResponseEntity<Item> response = orderController.createItem(null);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
            verify(itemService, never()).createItem(any());
        }
    }

    @Nested
    @DisplayName("OrderModel Creation Tests")
    class OrderModelCreationTests {
        @Test
        @DisplayName("Create OrderModel Successfully")
        void createOrderSuccessfully() {
            when(orderService.createOrder(any(OrderDTO.class))).thenReturn(testOrderModel);

            ResponseEntity<OrderModel> response = orderController.createOrder(testOrderDTO);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            verify(orderService).createOrder(testOrderDTO);
        }

        @Test
        @DisplayName("Create OrderModel with Empty Items")
        void createOrderWithEmptyItems() {
            OrderDTO emptyOrderDTO = new OrderDTO(null, "Test Customer", true, "Test Notes", Collections.emptyMap(), "");
            when(orderService.createOrder(any(OrderDTO.class))).thenThrow(new RuntimeException("Empty items"));

            ResponseEntity<OrderModel> response = orderController.createOrder(emptyOrderDTO);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Create OrderModel with Null DTO")
        void createOrderWithNullDTO() {
            ResponseEntity<OrderModel> response = orderController.createOrder(null);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
            verify(orderService, never()).createOrder(any());
        }
    }

    @Nested
    @DisplayName("Product Type Tests")
    class ProductTypeTests {
        @Test
        @DisplayName("Create Product Type Successfully")
        void createProductTypeSuccessfully() {
            when(productTypeService.createProductType(any(ProductTypeDTO.class))).thenReturn(testProductType);

            ResponseEntity<ProductType> response = orderController.createProductType(testProductTypeDTO);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testProductType, response.getBody());
            verify(productTypeService).createProductType(testProductTypeDTO);
        }

        @Test
        @DisplayName("Create Product Type Failure")
        void createProductTypeFailure() {
            when(productTypeService.createProductType(any(ProductTypeDTO.class)))
                .thenThrow(new RuntimeException("Failed to create product type"));

            ResponseEntity<ProductType> response = orderController.createProductType(testProductTypeDTO);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Create Product Type with Null DTO")
        void createProductTypeWithNullDTO() {
            ResponseEntity<ProductType> response = orderController.createProductType(null);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
            verify(productTypeService, never()).createProductType(any());
        }

        @Test
        @DisplayName("Update Item Product Type Successfully")
        void updateItemProductTypeSuccessfully() {
            UpdateProductTypeDTO dto = new UpdateProductTypeDTO(1L, 1L);
            doNothing().when(productTypeService).updateItemProductType(anyLong(), anyLong());

            ResponseEntity<String> response = orderController.updateItemProductType(dto);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Success", response.getBody());
            verify(productTypeService).updateItemProductType(dto.itemId(), dto.productTypeId());
        }

        @Test
        @DisplayName("Update Item Product Type Failure with Specific Error Message")
        void updateItemProductTypeFailureWithMessage() {
            UpdateProductTypeDTO dto = new UpdateProductTypeDTO(1L, 1L);
            String errorMessage = "Invalid product type transition";
            doThrow(new RuntimeException(errorMessage))
                .when(productTypeService)
                .updateItemProductType(anyLong(), anyLong());

            ResponseEntity<String> response = orderController.updateItemProductType(dto);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(errorMessage, response.getBody());
        }

        @Test
        @DisplayName("Update Item Product Type with Null DTO")
        void updateItemProductTypeWithNullDTO() {
            ResponseEntity<String> response = orderController.updateItemProductType(null);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Invalid request: DTO is null", response.getBody());
            verify(productTypeService, never()).updateItemProductType(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("Status Definition Tests")
    class StatusDefinitionTests {
        @Test
        @DisplayName("Create Status Definition Successfully")
        void createStatusDefinitionSuccessfully() {
            when(orderService.createStatusDefinition(any(StatusDefinitionDTO.class))).thenReturn(testStatusDefinition);

            ResponseEntity<StatusDefinition> response = orderController.createStatusDefinition(testStatusDefinitionDTO);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testStatusDefinition, response.getBody());
            verify(orderService).createStatusDefinition(testStatusDefinitionDTO);
        }

        @Test
        @DisplayName("Create Status Definition Failure")
        void createStatusDefinitionFailure() {
            when(orderService.createStatusDefinition(any(StatusDefinitionDTO.class)))
                .thenThrow(new RuntimeException("Failed to create status definition"));

            ResponseEntity<StatusDefinition> response = orderController.createStatusDefinition(testStatusDefinitionDTO);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Create Status Definition with Null DTO")
        void createStatusDefinitionWithNullDTO() {
            ResponseEntity<StatusDefinition> response = orderController.createStatusDefinition(null);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
            verify(orderService, never()).createStatusDefinition(any());
        }
    }

    @Nested
    @DisplayName("OrderModel Progress Tests")
    class OrderModelProgressTests {
        @Test
        @DisplayName("Move To Next Step Successfully")
        void moveToNextStepSuccessfully() {
            when(orderProgressService.moveToNextStep(anyLong())).thenReturn(testOrderProgress);

            ResponseEntity<OrderProgress> response = orderController.moveToNextStep(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testOrderProgress, response.getBody());
            verify(orderProgressService).moveToNextStep(1L);
        }

        @Test
        @DisplayName("Move To Next Step Invalid State")
        void moveToNextStepInvalidState() {
            when(orderProgressService.moveToNextStep(anyLong())).thenThrow(new IllegalStateException("Invalid state"));

            ResponseEntity<OrderProgress> response = orderController.moveToNextStep(1L);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Move To Next Step Runtime Error")
        void moveToNextStepRuntimeError() {
            when(orderProgressService.moveToNextStep(anyLong())).thenThrow(new RuntimeException("Unexpected error"));

            ResponseEntity<OrderProgress> response = orderController.moveToNextStep(1L);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Move To Next Step with Null ID")
        void moveToNextStepWithNullId() {
            ResponseEntity<OrderProgress> response = orderController.moveToNextStep(null);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
            verify(orderProgressService, never()).moveToNextStep(any());
        }

        @Test
        @DisplayName("Move To Previous Step Successfully")
        void moveToPrevStepSuccessfully() {
            when(orderProgressService.moveToPreviousStep(anyLong())).thenReturn(testOrderProgress);

            ResponseEntity<OrderProgress> response = orderController.moveToPrevStep(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testOrderProgress, response.getBody());
            verify(orderProgressService).moveToPreviousStep(1L);
        }

        @Test
        @DisplayName("Move To Previous Step Invalid State")
        void moveToPrevStepInvalidState() {
            when(orderProgressService.moveToPreviousStep(anyLong())).thenThrow(new IllegalStateException("Invalid state"));

            ResponseEntity<OrderProgress> response = orderController.moveToPrevStep(1L);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Move To Previous Step Runtime Error")
        void moveToPrevStepRuntimeError() {
            when(orderProgressService.moveToPreviousStep(anyLong())).thenThrow(new RuntimeException("Unexpected error"));

            ResponseEntity<OrderProgress> response = orderController.moveToPrevStep(1L);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Move To Previous Step with Null ID")
        void moveToPrevStepWithNullId() {
            ResponseEntity<OrderProgress> response = orderController.moveToPrevStep(null);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
            verify(orderProgressService, never()).moveToPreviousStep(any());
        }

        @Test
        @DisplayName("Get Progress Successfully")
        void getProgressSuccessfully() {
            when(orderProgressService.getProgress(anyLong())).thenReturn(testOrderProgress);

            ResponseEntity<OrderProgress> response = orderController.getProgress(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(testOrderProgress, response.getBody());
            verify(orderProgressService).getProgress(1L);
        }

        @Test
        @DisplayName("Get Progress Failure")
        void getProgressFailure() {
            when(orderProgressService.getProgress(anyLong())).thenThrow(new RuntimeException("Failed to get progress"));

            ResponseEntity<OrderProgress> response = orderController.getProgress(1L);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Get Progress with Null ID")
        void getProgressWithNullId() {
            ResponseEntity<OrderProgress> response = orderController.getProgress(null);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
            verify(orderProgressService, never()).getProgress(any());
        }
    }

    @Nested
    @DisplayName("OrderModel Retrieval Tests")
    class OrderModelRetrievalTests {
        @Test
        @DisplayName("Get OrderModel Details by Valid ID")
        void getOrderDetailsByValidId() {
            Long orderId = 1L;
            when(orderService.getOrderDetailsWithStatus(orderId)).thenReturn(Collections.emptyList());

            ResponseEntity<List<OrderDetailsWithStatusDTO>> response = orderController.getOrderDetailsWithStatus(orderId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            verify(orderService).getOrderDetailsWithStatus(orderId);
        }

        @Test
        @DisplayName("Get OrderModel Details by Non-Existent ID")
        void getOrderDetailsByNonExistentId() {
            Long orderId = 999L;
            when(orderService.getOrderDetailsWithStatus(orderId)).thenThrow(new RuntimeException("Order not found"));

            ResponseEntity<List<OrderDetailsWithStatusDTO>> response = orderController.getOrderDetailsWithStatus(orderId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Get OrderModel Details with Null ID")
        void getOrderDetailsWithNullId() {
            ResponseEntity<List<OrderDetailsWithStatusDTO>> response = orderController.getOrderDetailsWithStatus(null);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());
            verify(orderService, never()).getOrderDetailsWithStatus(any());
        }

        @Test
        @DisplayName("Get All Orders Successfully")
        void getAllOrdersSuccessfully() {
            List<OrderDashboardDTO> mockOrders = Collections.emptyList();
            when(orderService.getAllOrders()).thenReturn(mockOrders);

            ResponseEntity<List<OrderDashboardDTO>> response = orderController.getAllOrders();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            verify(orderService).getAllOrders();
        }

        @Test
        @DisplayName("Get All Orders Failure")
        void getAllOrdersFailure() {
            when(orderService.getAllOrders()).thenThrow(new RuntimeException("Failed to get orders"));

            ResponseEntity<List<OrderDashboardDTO>> response = orderController.getAllOrders();

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNull(response.getBody());
        }
    }
}
