package gruppe2.backend.dto;

import gruppe2.backend.model.Item;
import gruppe2.backend.model.StatusDefinition;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record OrderDashboardDTO(
        Long orderId,
        LocalDateTime orderCreated,
        boolean priority,
        String customerName,
        String notes,
        List<OrderDetailsWithStatusDTO> items
) {}
