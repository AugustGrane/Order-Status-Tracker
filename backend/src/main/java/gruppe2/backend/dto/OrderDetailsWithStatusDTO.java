package gruppe2.backend.dto;

import gruppe2.backend.model.Item;
import gruppe2.backend.model.StatusDefinition;

import java.time.LocalDateTime;
import java.util.Map;

public record OrderDetailsWithStatusDTO(
    Long id,
    Long orderId,
    Item item,
    Integer itemAmount,
    String product_type,
    Integer currentStepIndex,
    StatusDefinition[] differentSteps,
    Map<Long, LocalDateTime> updated
) {}
