package gruppe2.backend.dto;

import gruppe2.backend.model.Item;
import gruppe2.backend.model.StatusDefinition;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public record OrderDetailsDTO(
    Long id,
    Long orderId,
    Item item,
    Integer itemAmount,
    Integer currentStepIndex,
    List<Long> differentSteps,
    Map<Long, LocalDateTime> updated
) {
    public OrderDetailsDTO {
        // Ensure the map is never null
        updated = updated != null ? updated : new HashMap<>();
        // Ensure the list is never null
        differentSteps = differentSteps != null ? differentSteps : new ArrayList<>();
    }
}
