package gruppe2.backend.dto;

// DTOs for clean request/response handling
public record ItemDTO(
    String name,
    Long id,
    Long productTypeId,
    String item_image
) {}
