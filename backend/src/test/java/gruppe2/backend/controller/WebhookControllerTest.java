package gruppe2.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gruppe2.backend.service.WebhookService;
import gruppe2.backend.service.webhook.WebhookPayload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WebhookService webhookService;

    @Test
    void handleWebhook_WithValidPayload_ShouldProcess() throws Exception {
        // Arrange
        WebhookPayload payload = new WebhookPayload();
        payload.setId(123L);

        // Act & Assert
        mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(webhookService).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    void handleWebhook_WithInvalidPayload_ShouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }
}
