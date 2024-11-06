package gruppe2.backend.dto;

import gruppe2.backend.model.Item;
import gruppe2.backend.model.StatusDefinition;

import java.time.LocalDateTime;
import java.util.Map;

public record OrderDashboardDTO (
        Long index,
        Long orderId,
        LocalDateTime orderCreated,
        Boolean priority,
        Item item,
        Integer itemAmount,
        String product_type,
        String customerName,
        String notes,
        Integer currentStepIndex,
        StatusDefinition[] differentSteps,
        Map<Long, LocalDateTime> updated
) {}
