package gruppe2.backend.mapper;

import gruppe2.backend.domain.OrderItem;
import gruppe2.backend.domain.OrderStatus;
import gruppe2.backend.dto.OrderDashboardDTO;
import gruppe2.backend.dto.OrderDetailsWithStatusDTO;
import gruppe2.backend.model.OrderDetails;
import gruppe2.backend.model.OrderModel;
import gruppe2.backend.model.StatusDefinition;
import gruppe2.backend.repository.StatusDefinitionRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDetailsMapper {
    private final StatusDefinitionRepository statusDefinitionRepository;

    public OrderDetailsMapper(StatusDefinitionRepository statusDefinitionRepository) {
        this.statusDefinitionRepository = statusDefinitionRepository;
    }

    public OrderDetailsWithStatusDTO toOrderDetailsDTO(OrderDetails details) {
        OrderStatus status = new OrderStatus(
            details.getDifferentSteps(),
            details.getCurrentStepIndex(),
            details.getUpdated()
        );

        OrderItem orderItem = new OrderItem(
            details.getItem(),
            details.getItemAmount(),
            details.getProduct_type(),
            status
        );

        StatusDefinition[] statusDefinitions = Arrays.stream(details.getDifferentSteps())
                .map(stepId -> statusDefinitionRepository.findById(stepId)
                        .orElseThrow(() -> new RuntimeException("Status definition not found: " + stepId)))
                .toArray(StatusDefinition[]::new);

        return new OrderDetailsWithStatusDTO(
            details.getId(),
            details.getOrderId(),
            orderItem.getItem(),
            orderItem.getQuantity(),
            orderItem.getProductTypeName(),
            orderItem.getCurrentStepIndex(),
            statusDefinitions,
            orderItem.getStatus().getStatusUpdates()
        );
    }

    public OrderDashboardDTO toOrderDashboardDTO(OrderModel orderModelEntity, List<OrderDetails> orderDetails) {
        List<OrderDetailsWithStatusDTO> items = orderDetails.stream()
                .map(this::toOrderDetailsDTO)
                .sorted(Comparator.comparing(OrderDetailsWithStatusDTO::id))
                .collect(Collectors.toList());

        return new OrderDashboardDTO(
            orderModelEntity.getId(),
            orderModelEntity.getOrderCreated(),
            orderModelEntity.isPriority(),
            orderModelEntity.getCustomerName(),
            orderModelEntity.getNotes(),
            items
        );
    }
}
