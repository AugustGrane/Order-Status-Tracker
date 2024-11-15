package gruppe2.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gruppe2.backend.service.WebhookService;
import gruppe2.backend.service.webhook.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WebhookService webhookService;

    @Autowired
    private WebhookController webhookController;

    private WebhookPayload testPayload;

    @BeforeEach
    void setUp() {
        // Create a complete test payload
        testPayload = new WebhookPayload();
        testPayload.setId(123L);
        
        // Set up billing info
        BillingInfo billingInfo = new BillingInfo();
        billingInfo.setFirstName("John");
        billingInfo.setLastName("Doe");
        billingInfo.setCompany("Test Company");
        testPayload.setBilling(billingInfo);

        // Set up line items
        List<LineItem> items = new ArrayList<>();
        Img img = new Img(1, "http://example.com/image.jpg");
        LineItem item = new LineItem("Test Product", 2, 1L, img);
        
        items.add(item);
        testPayload.setItems(items);
    }

    @Test
    @DisplayName("Handle webhook with valid payload successfully")
    void handleWebhook_WithValidPayload_ShouldProcess() throws Exception {
        // Arrange
        doNothing().when(webhookService).createOrderInDatabase(any(WebhookPayload.class));

        // Act
        ResultActions result = mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPayload)));

        // Assert
        result.andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andExpect(jsonPath("$.id").value(123))
              .andExpect(jsonPath("$.billing.first_name").value("John"))
              .andExpect(jsonPath("$.billing.last_name").value("Doe"))
              .andExpect(jsonPath("$.line_items[0].name").value("Test Product"))
              .andExpect(jsonPath("$.line_items[0].quantity").value(2))
              .andExpect(jsonPath("$.line_items[0].product_id").value(1))
              .andExpect(jsonPath("$.line_items[0].image.src").value("http://example.com/image.jpg"));

        verify(webhookService, times(1)).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Handle webhook with invalid JSON syntax")
    void handleWebhook_WithInvalidJsonSyntax_ShouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json syntax"))
                .andExpect(status().isBadRequest());

        verify(webhookService, never()).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Handle webhook with malformed JSON")
    void handleWebhook_WithMalformedJson_ShouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\": []}"))  // Valid JSON but wrong structure
                .andExpect(status().isBadRequest());

        verify(webhookService, never()).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Handle webhook when service throws exception")
    void handleWebhook_WhenServiceThrowsException_ShouldReturn500() throws Exception {
        // Arrange
        RuntimeException testException = new RuntimeException("Database error");
        doThrow(testException)
            .when(webhookService)
            .createOrderInDatabase(any(WebhookPayload.class));

        // Act
        ResultActions result = mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPayload)));

        // Assert
        result.andExpect(status().isInternalServerError())
              .andExpect(jsonPath("$").doesNotExist());

        verify(webhookService, times(1)).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Handle webhook with no content")
    void handleWebhook_WithNoContent_ShouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(webhookService, never()).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Handle webhook with empty JSON object")
    void handleWebhook_WithEmptyJsonObject_ShouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        verify(webhookService, never()).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Handle webhook with missing required fields")
    void handleWebhook_WithMissingRequiredFields_ShouldReturn400() throws Exception {
        // Arrange - create payload with missing required fields
        WebhookPayload invalidPayload = new WebhookPayload();
        // Don't set any fields, leaving them as null/default

        // Act & Assert
        mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPayload)))
                .andExpect(status().isBadRequest());

        verify(webhookService, never()).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Handle webhook with unsupported media type")
    void handleWebhook_WithUnsupportedMediaType_ShouldReturn415() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.TEXT_PLAIN)
                .content(objectMapper.writeValueAsString(testPayload)))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(header().string("Accept", "application/json, application/*+json"));

        verify(webhookService, never()).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Handle webhook with null fields in payload")
    void handleWebhook_WithNullFields_ShouldReturn400() throws Exception {
        // Arrange
        testPayload.setBilling(null);
        testPayload.setItems(null);

        // Act & Assert
        mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPayload)))
                .andExpect(status().isBadRequest());

        verify(webhookService, never()).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Handle webhook with zero ID")
    void handleWebhook_WithZeroId_ShouldReturn400() throws Exception {
        // Arrange
        testPayload.setId(0L);

        // Act & Assert
        mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPayload)))
                .andExpect(status().isBadRequest());

        verify(webhookService, never()).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Handle webhook with empty items list")
    void handleWebhook_WithEmptyItems_ShouldReturn400() throws Exception {
        // Arrange
        testPayload.setItems(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPayload)))
                .andExpect(status().isBadRequest());

        verify(webhookService, never()).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Handle webhook with null items causing potential NPE")
    void handleWebhook_WithNullItemsCausingNPE_ShouldReturn400() throws Exception {
        // Arrange
        WebhookPayload payload = new WebhookPayload();
        payload.setId(123L);
        payload.setBilling(testPayload.getBilling());
        // Explicitly set items to null to test NPE branch
        payload.setItems(null);

        // Act & Assert
        mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());

        verify(webhookService, never()).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Handle webhook with all valid fields returning payload")
    void handleWebhook_WithAllValidFields_ShouldReturnPayload() throws Exception {
        // Arrange
        doNothing().when(webhookService).createOrderInDatabase(any(WebhookPayload.class));

        // Act & Assert
        mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPayload)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.billing").exists())
                .andExpect(jsonPath("$.line_items").isArray())
                .andExpect(jsonPath("$.line_items[0]").exists());

        verify(webhookService, times(1)).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Handle webhook with missing id field in JSON")
    void handleWebhook_WithMissingIdField_ShouldReturn400() throws Exception {
        // Arrange - Create JSON without id field using Map
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("billing", testPayload.getBilling());
        payloadMap.put("items", testPayload.getItems());
        String jsonContent = objectMapper.writeValueAsString(payloadMap);

        // Act & Assert
        mockMvc.perform(post("/webhooks/wooOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isBadRequest());

        verify(webhookService, never()).createOrderInDatabase(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Handle webhook with null payload directly")
    void handleWebhook_WithNullPayloadDirectly_ShouldReturn400() {
        // Act
        ResponseEntity<WebhookPayload> response = webhookController.handleWebhook(null);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(webhookService, never()).createOrderInDatabase(any(WebhookPayload.class));
    }
}
