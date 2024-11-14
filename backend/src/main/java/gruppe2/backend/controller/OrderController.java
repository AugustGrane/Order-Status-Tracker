package gruppe2.backend.controller;

import gruppe2.backend.dto.*;
import gruppe2.backend.domain.OrderProgress;
import gruppe2.backend.model.*;
import gruppe2.backend.repository.OrderSummaryProjection;
import gruppe2.backend.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * REST Controller for managing orders and related entities in the Order Status Tracker system.
 * Provides endpoints for creating, retrieving, and managing orders, items, and product types.
 * All endpoints are accessible under the /api base path.
 */
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;
    private final ItemService itemService;
    private final ProductTypeService productTypeService;
    private final OrderProgressService orderProgressService;

    public OrderController(
            OrderService orderService,
            ItemService itemService,
            ProductTypeService productTypeService,
            OrderProgressService orderProgressService) {
        this.orderService = orderService;
        this.itemService = itemService;
        this.productTypeService = productTypeService;
        this.orderProgressService = orderProgressService;
    }

    // ==================== Item Management Endpoints ====================

    /**
     * Creates a new item in the system.
     * @param itemDTO The item data transfer object containing item details
     * @return ResponseEntity containing the created item or error response
     */
    @PostMapping("/items")
    public ResponseEntity<Item> createItem(@RequestBody ItemDTO itemDTO) {
        if (itemDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(itemService.createItem(itemDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // ==================== Order Management Endpoints ====================

    /**
     * Creates a new order with the provided details.
     * @param orderDTO The order data transfer object containing order details
     * @return ResponseEntity containing the created order or error response
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderModel> createOrder(@RequestBody OrderDTO orderDTO) {
        if (orderDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(orderService.createOrder(orderDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Retrieves a list of all order summaries.
     * @return ResponseEntity containing list of order summaries or error response
     */
    @GetMapping("/orders/summaries")
    public ResponseEntity<List<OrderSummaryProjection>> getAllOrderSummaries() {
        try {
            return ResponseEntity.ok(orderService.getAllOrderSummaries());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Retrieves detailed order information including status for a specific order.
     * @param orderId The ID of the order to retrieve
     * @return ResponseEntity containing order details with status or error response
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<List<OrderDetailsWithStatusDTO>> getOrderDetailsWithStatus(
            @PathVariable Long orderId) {
        if (orderId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(orderService.getOrderDetailsWithStatus(orderId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Retrieves complete order details for a specific order.
     * @param orderId The ID of the order to retrieve
     * @return ResponseEntity containing complete order details or error response
     */
    @GetMapping("/orders/{orderId}/details")
    public ResponseEntity<OrderModel> getOrderDetails(@PathVariable Long orderId) {
        if (orderId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(orderService.getOrderDetails(orderId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Retrieves all orders with their dashboard information.
     * @return ResponseEntity containing list of orders with dashboard information
     */
    @GetMapping("/get-all-orders")
    public ResponseEntity<List<OrderDashboardDTO>> getAllOrders() {
        try {
            return ResponseEntity.ok(orderService.getAllOrders());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ==================== Product Type Management Endpoints ====================

    /**
     * Creates a new product type.
     * @param productTypeDTO The product type data transfer object
     * @return ResponseEntity containing the created product type or error response
     */
    @PostMapping("/product-types")
    public ResponseEntity<ProductType> createProductType(@RequestBody ProductTypeDTO productTypeDTO) {
        if (productTypeDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(productTypeService.createProductType(productTypeDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Updates the product type of an item.
     * @param dto The update product type data transfer object
     * @return ResponseEntity containing success message or error response
     */
    @PostMapping("/update-generic-product-type")
    public ResponseEntity<String> updateItemProductType(@RequestBody UpdateProductTypeDTO dto) {
        if (dto == null) {
            return ResponseEntity.badRequest().body("Invalid request: DTO is null");
        }
        try {
            productTypeService.updateItemProductType(dto.itemId(), dto.productTypeId());
            return ResponseEntity.ok("Success");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ==================== Order Progress Management Endpoints ====================

    /**
     * Moves an order product type to its next step in the workflow.
     * @param id The ID of the order product type
     * @return ResponseEntity containing updated order progress or error response
     */
    @PutMapping("/order-product-types/{id}/next-step")
    public ResponseEntity<OrderProgress> moveToNextStep(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(orderProgressService.moveToNextStep(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Moves an order product type to its previous step in the workflow.
     * @param id The ID of the order product type
     * @return ResponseEntity containing updated order progress or error response
     */
    @PutMapping("/order-product-types/{id}/prev-step")
    public ResponseEntity<OrderProgress> moveToPrevStep(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(orderProgressService.moveToPreviousStep(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves the current progress of an order product type.
     * @param id The ID of the order product type
     * @return ResponseEntity containing order progress or error response
     */
    @GetMapping("/order-product-types/{id}/progress")
    public ResponseEntity<OrderProgress> getProgress(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(orderProgressService.getProgress(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== Status Definition Management Endpoints ====================

    /**
     * Creates a new status definition for order processing.
     * @param dto The status definition data transfer object
     * @return ResponseEntity containing created status definition or error response
     */
    @PostMapping("/status-definitions")
    public ResponseEntity<StatusDefinition> createStatusDefinition(@RequestBody StatusDefinitionDTO dto) {
        if (dto == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(orderService.createStatusDefinition(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // ==================== Exception Handling ====================

    /**
     * Global exception handler for runtime exceptions.
     * @param e The runtime exception that was thrown
     * @return ResponseEntity containing error message
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + e.getMessage());
    }
}
