package gruppe2.backend.controller;

import gruppe2.backend.dto.*;
import gruppe2.backend.model.*;
import gruppe2.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/items")
    public ResponseEntity<Item> createItem(@RequestBody ItemDTO itemDTO) {
        try {
            Item item = orderService.createItem(itemDTO);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            Order order = orderService.createOrder(orderDTO);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/product-types")
    public ResponseEntity<ProductType> createProductType(@RequestBody ProductTypeDTO productTypeDTO) {
        try {
            ProductType productType = orderService.createProductType(productTypeDTO);
            return ResponseEntity.ok(productType);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/status-definitions")
    public ResponseEntity<StatusDefinition> createStatusDefinition(@RequestBody StatusDefinitionDTO dto) {
        try {
            StatusDefinition statusDefinition = orderService.createStatusDefinition(dto);
            return ResponseEntity.ok(statusDefinition);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/test")
    public ResponseEntity<?> updateItemProductType(@RequestBody UpdateProductTypeDTO dto) {
        try {
                // Set product type
                orderService.setProductType(dto.itemId(), dto.productTypeId());
                orderService.updateOrderProductTypeSteps(dto. orderId(), dto.itemId(), dto.productTypeId());
                return ResponseEntity.ok("Success");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @GetMapping("/orders/{orderId}")
    public ResponseEntity<List<OrderDetailsWithStatusDTO>> getOrderDetails(@PathVariable Long orderId) {
        try {
            List<OrderDetailsWithStatusDTO> orderDetails = orderService.getOrderDetails(orderId);
            return ResponseEntity.ok(orderDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/order-product-types/{id}/next-step")
    public ResponseEntity<?> moveToNextStep(@PathVariable Long id) {
        try {
            Map<String, Object> result = orderService.moveToNextStep(id);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating step: " + e.getMessage());
        }
    }

    @PutMapping("/order-product-types/{id}/prev-step")
    public ResponseEntity<?> moveToPrevStep(@PathVariable Long id) {
        try {
            Map<String, Object> result = orderService.moveToPrevStep(id);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating step: " + e.getMessage());
        }
    }

    @GetMapping("/get-all-orders")
    public ResponseEntity<List<OrderDashboardDTO>> getAllOrders() {
        try {
            List<OrderDashboardDTO> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/order-product-types/{id}/progress")
    public ResponseEntity<?> getProgress(@PathVariable Long id) {
        try {
            Map<String, Object> progress = orderService.getProgress(id);
            return ResponseEntity.ok(progress);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting progress: " + e.getMessage());
        }
    }
}
