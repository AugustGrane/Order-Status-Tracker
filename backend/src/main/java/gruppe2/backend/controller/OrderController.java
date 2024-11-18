package gruppe2.backend.controller;

import gruppe2.backend.dto.*;
import gruppe2.backend.domain.OrderProgress;
import gruppe2.backend.model.*;
import gruppe2.backend.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
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

    @PostMapping("/create-item")
    public ResponseEntity<Item> createItem(@RequestBody ItemDTO itemDTO) {
        if (itemDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        try {
            return ResponseEntity.ok(itemService.createItem(itemDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/create-order")
    public ResponseEntity<Order> createOrder(@RequestBody OrderDTO orderDTO) {
        if (orderDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        try {
            return ResponseEntity.ok(orderService.createOrder(orderDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/create-product-type")
    public ResponseEntity<ProductType> createProductType(@RequestBody ProductTypeDTO productTypeDTO) {
        if (productTypeDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        try {
            return ResponseEntity.ok(productTypeService.createProductType(productTypeDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/create-status-definition")
    public ResponseEntity<StatusDefinition> createStatusDefinition(@RequestBody StatusDefinitionDTO dto) {
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        try {
            return ResponseEntity.ok(orderService.createStatusDefinition(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @PostMapping("/update-generic-product-type")
    public ResponseEntity<String> updateItemProductType(@RequestBody UpdateProductTypeDTO dto) {
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: DTO is null");
        }
        try {
            productTypeService.updateItemProductType(dto.itemId(), dto.productTypeId());
            return ResponseEntity.ok("Success");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<List<OrderDetailsWithStatusDTO>> getOrderDetails(@PathVariable Long orderId) {
        if (orderId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        try {
            return ResponseEntity.ok(orderService.getOrderDetails(orderId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

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

    @GetMapping("/orders/dashboard")
    public ResponseEntity<List<OrderDashboardDTO>> getAllOrders() {
        try {
            logger.info("Fetching dashboard orders");
            List<OrderDashboardDTO> orders = orderService.getAllOrders();
            logger.info("Successfully fetched {} orders", orders.size());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching dashboard orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/order-product-types/{id}/progress")
    public ResponseEntity<OrderProgress> getProgress(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        try {
            return ResponseEntity.ok(orderProgressService.getProgress(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete-order/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        if (orderId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: Order ID is null");
        }
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok("Successfully deleted order with ID: " + orderId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-item/{itemId}")
    public ResponseEntity<String> deleteItem(@PathVariable Long itemId) {
        if (itemId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: Item ID is null");
        }
        try {
            itemService.setItemAsDeleted(itemId);   // Doesn't actually delete the item, just marks it as deleted
            return ResponseEntity.ok("Successfully deleted item with ID: " + itemId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-item-from-order/{itemId}/{orderId}")
    public ResponseEntity<String> deleteOrderItem(@PathVariable Long itemId, @PathVariable Long orderId) {
        if (itemId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: Item ID is null");
        }
        if (orderId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: Order ID is null");
        }
        try {
            orderProgressService.deleteItemFromOrder(itemId, orderId);
            return ResponseEntity.ok("Successfully deleted item " + itemId + " from order " + orderId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-product-type/{productTypeId}")
    public ResponseEntity<String> deleteProductType(@PathVariable Long productTypeId) {
        if (productTypeId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: Product type ID is null");
        }
        try {
            productTypeService.deleteProductType(productTypeId);
            return ResponseEntity.ok("Successfully deleted product type with ID: " + productTypeId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
