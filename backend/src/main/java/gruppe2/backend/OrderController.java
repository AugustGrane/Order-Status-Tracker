package gruppe2.backend;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/endpoints")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {
    @PostMapping("/{orderInput}")
    //@PathVariable takes content from url, in this case: orderid;
    public Order searchOrder(@PathVariable("orderInput") String orderInput) {

        // Simulate order retrieval. Replace this with your actual database lookup.
        // Test order
        Order order = new Order(orderInput, 420, 1000);

        return order;
    }
}