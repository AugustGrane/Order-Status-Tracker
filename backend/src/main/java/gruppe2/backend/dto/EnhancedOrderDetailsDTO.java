package gruppe2.backend.dto;

import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.model.StatusDefinition;

import java.util.List;
import java.util.Map;

public record EnhancedOrderDetailsDTO(
    List<OrderDetails> orderDetails,
    Map<Long, StatusDefinition> statusDefinitions
) {}
