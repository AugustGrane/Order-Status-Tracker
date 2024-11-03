package gruppe2.backend.controller;

import gruppe2.backend.model.Order;
import gruppe2.backend.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void moveToNextStep_WithValidId_ShouldReturnUpdatedStatus() throws Exception {
        // Arrange
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Successfully moved to next step");
        
        when(orderService.moveToNextStep(1L)).thenReturn(result);

        // Act & Assert
        mockMvc.perform(put("/api/order-product-types/1/next-step"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void moveToPrevStep_WithValidId_ShouldReturnUpdatedStatus() throws Exception {
        // Arrange
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Successfully moved to previous step");
        
        when(orderService.moveToPrevStep(1L)).thenReturn(result);

        // Act & Assert
        mockMvc.perform(put("/api/order-product-types/1/prev-step"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getProgress_WithValidId_ShouldReturnProgress() throws Exception {
        // Arrange
        Map<String, Object> progress = new HashMap<>();
        progress.put("currentStep", 2);
        progress.put("totalSteps", 5);
        
        when(orderService.getProgress(1L)).thenReturn(progress);

        // Act & Assert
        mockMvc.perform(get("/api/order-product-types/1/progress"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentStep").value(2))
                .andExpect(jsonPath("$.totalSteps").value(5));
    }
}
