package gruppe2.backend.dto;

import java.util.Map;

public record OrderDTO(
        Long id,
        String customerName,
        boolean priority,
        String notes,
        Map<Long, Integer> items  // item_id -> quantity
) {
}
