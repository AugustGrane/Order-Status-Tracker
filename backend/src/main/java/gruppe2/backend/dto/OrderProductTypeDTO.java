package gruppe2.backend.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record OrderProductTypeDTO(
        Long orderId,
        Long itemId,
        String name,
        Long[] differentSteps,
        Long currentStepId,  // Added this field
        Map<Long, LocalDateTime> statusUpdates
) {}

