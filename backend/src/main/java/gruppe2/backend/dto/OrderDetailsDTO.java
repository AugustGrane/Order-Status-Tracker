package gruppe2.backend.dto;

import gruppe2.backend.model.OrderDetails;

import java.util.List;

public record OrderDetailsDTO(
        List<OrderDetails> orderDetails
) {}

