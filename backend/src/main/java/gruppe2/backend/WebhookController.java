package gruppe2.backend;

import gruppe2.backend.dto.OrderDTO;
import gruppe2.backend.webhook.LineItem;
import gruppe2.backend.webhook.WebhookPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    @PostMapping("/wooOrder")
    public ResponseEntity<WebhookPayload> handleWebhook(@RequestBody WebhookPayload payload) {
        try {
            // Log basic information for verification
//            log.info("Order ID: {}", payload.getId());
//            log.info("Customer: {} {}, Company: {}", payload.getBilling().getFirstName(), payload.getBilling().getLastName(), payload.getBilling().getCompany());
//
//            // Log line items
//            for (LineItem item : payload.getItems()) {
//                log.info("Item: {}, Quantity: {}, Id: {}", item.getName(), item.getQuantity(),item.getId());
//                log.info("Image source: {}", item.getImg());
//            }
            System.out.println("payload:"+ payload.toString());
            // Send payload back as JSON for confirmation (or further processing)


            // Extracting names from payload object - displayName depends on existance of company name
            String companyName, name, displayName;

            if(!Objects.equals(payload.getBilling().getCompany(), "")) {
                companyName = payload.getBilling().getCompany();
                name = payload.getBilling().getFirstName() + " " + payload.getBilling().getLastName();
                displayName = companyName + " | " + name;
            }
            else {
                displayName = payload.getBilling().getFirstName() + " " + payload.getBilling().getLastName();
            }
//            Map<Long, Integer> itemsMap = new Map<>()
//            for (LineItem item : payload.getItems()) {
//
//            }

            // COWI | John Doe
            System.out.println(displayName);
            // repackage to orderDTO
//            OrderDTO orderDTO = new OrderDTO(
//                    displayName,
//                      false,
//                      "",
//                    Map<payload.getItems().ge>
//            );

            return ResponseEntity.ok(payload);

        } catch (Exception e) {
            log.error("Error processing webhook payload: ", e);
            return ResponseEntity.status(500).body(null);
        }
    }
}