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

    @PostMapping("/items")
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

    @PostMapping("/orders")
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

    @PostMapping("/product-types")
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

    @PostMapping("/status-definitions")
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

    @PutMapping("/update-step")
    public ResponseEntity<OrderProgress> moveToStep(@RequestBody Long orderDetailsId, int newStepIndex) {
        if (orderDetailsId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok(orderProgressService.moveToStep(orderDetailsId, newStepIndex));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
