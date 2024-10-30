package gruppe2.backend.controller;

import gruppe2.backend.service.webhook.WebhookPayload;
import gruppe2.backend.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);
    private final WebhookService webhookService;

    // Dependency injection of WebhookService
    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/wooOrder")
    public ResponseEntity<WebhookPayload> handleWebhook(@RequestBody WebhookPayload payload) {
        try {
            webhookService.createOrderInDatabase(payload);
            return ResponseEntity.ok(payload);
        } catch (Exception e) {
            log.error("Error processing webhook payload: ", e);
            return ResponseEntity.status(500).body(null);
            }
        }
    }
