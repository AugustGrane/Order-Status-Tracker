package gruppe2.backend.dto;

import gruppe2.backend.model.Item;
import gruppe2.backend.model.StatusDefinition;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderDetailsWithStatusDTO(
    Long id,
    Long orderId,
    Item item,
    Integer itemAmount,
    String productTypeName,
    Integer currentStepIndex,
    StatusDefinition[] differentSteps,
    Map<Long, LocalDateTime> updated
) {
    public OrderDetailsWithStatusDTO {
        // Ensure non-null arrays and maps
        differentSteps = differentSteps != null ? differentSteps : new StatusDefinition[0];
        updated = updated != null ? updated : Map.of();
    }
}
