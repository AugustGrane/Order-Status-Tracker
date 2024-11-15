package gruppe2.backend.mapper;

import gruppe2.backend.domain.OrderItem;
import gruppe2.backend.domain.OrderStatus;
import gruppe2.backend.dto.OrderDashboardDTO;
import gruppe2.backend.dto.OrderDetailsWithStatusDTO;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.model.StatusDefinition;
import gruppe2.backend.repository.StatusDefinitionRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class OrderDetailsMapper {
    private final StatusDefinitionRepository statusDefinitionRepository;

    public OrderDetailsMapper(StatusDefinitionRepository statusDefinitionRepository) {
        this.statusDefinitionRepository = statusDefinitionRepository;
    }

    public OrderDetailsWithStatusDTO toOrderDetailsDTO(OrderDetails details, Map<Long, StatusDefinition> statusDefinitionsMap) {
        return toOrderDetailsDTO(details, statusDefinitionsMap, null);
    }

    public OrderDetailsWithStatusDTO toOrderDetailsDTO(
            OrderDetails details, 
            Map<Long, StatusDefinition> statusDefinitionsMap,
            String productTypeName) {
        OrderStatus status = new OrderStatus(
            details.getDifferentSteps().toArray(new Long[0]),
            details.getCurrentStepIndex(),
            details.getUpdated()
        );

        String finalProductTypeName = productTypeName != null ? productTypeName : details.getProduct_type();
        
        OrderItem orderItem = new OrderItem(
            details.getItem(),
            details.getItemAmount(),
            finalProductTypeName,
            status
        );

        StatusDefinition[] statusDefinitions = details.getDifferentSteps().stream()
                .map(stepId -> statusDefinitionsMap.getOrDefault(stepId,
                    new StatusDefinition(stepId, "Unknown", "Status not found", "placeholder.png")))
                .toArray(StatusDefinition[]::new);

        return new OrderDetailsWithStatusDTO(
            details.getId(),
            details.getOrderId(),
            orderItem.getItem(),
            orderItem.getQuantity(),
            orderItem.getProductTypeName(),
            orderItem.getStatus().getCurrentStepIndex(),
            statusDefinitions,
            orderItem.getStatus().getStatusUpdates()
        );
    }

    public OrderDashboardDTO toOrderDashboardDTO(gruppe2.backend.model.Order orderEntity, List<OrderDetails> orderDetails) {
        // Create the DTO with basic order information
        OrderDashboardDTO dto = new OrderDashboardDTO(
            orderEntity.getId(),
            orderEntity.getOrderCreated(),
            orderEntity.isPriority(),
            orderEntity.getCustomerName(),
            orderEntity.getNotes()
        );
        
        // Get all unique status definition IDs from all order details
        Set<Long> allStatusIds = orderDetails.stream()
                .flatMap(details -> details.getDifferentSteps().stream())
                .collect(Collectors.toSet());
        
        // Fetch all status definitions in a single query
        Map<Long, StatusDefinition> statusDefinitionsMap = statusDefinitionRepository.findAllById(allStatusIds)
                .stream()
                .collect(Collectors.toMap(
                    StatusDefinition::getId,
                    sd -> sd,
                    (existing, replacement) -> existing
                ));

        // Set the items after mapping them
        List<OrderDetailsWithStatusDTO> items = orderDetails.stream()
                .map(details -> toOrderDetailsDTO(details, statusDefinitionsMap, details.getProduct_type()))
                .sorted(Comparator.comparing(OrderDetailsWithStatusDTO::id))
                .collect(Collectors.toList());
        dto.setItems(items);
        
        return dto;
    }
}
