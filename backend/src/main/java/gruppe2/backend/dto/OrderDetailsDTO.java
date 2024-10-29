package gruppe2.backend.dto;

import gruppe2.backend.model.OrderProductType;

import java.util.List;
import java.util.Map;

public record OrderDetailsDTO(
        List<OrderProductType> productTypes,
        Map<Long, Integer> itemQuantities
) {}
