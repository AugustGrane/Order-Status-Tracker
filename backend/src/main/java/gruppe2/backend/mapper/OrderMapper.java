package gruppe2.backend.mapper;

import gruppe2.backend.domain.Order;
import gruppe2.backend.dto.OrderDTO;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class OrderMapper {
    
    public gruppe2.backend.model.Order toModelOrder(Order domainOrder) {
        gruppe2.backend.model.Order orderEntity = new gruppe2.backend.model.Order();
        orderEntity.setId(domainOrder.getId().getValue());
        orderEntity.setCustomerName(domainOrder.getCustomerInfo().getName());
        orderEntity.setPriority(domainOrder.getCustomerInfo().isPriority());
        orderEntity.setNotes(domainOrder.getCustomerInfo().getNotes());
        orderEntity.setOrderCreated(domainOrder.getTimeline().getOrderCreated());
        orderEntity.setTotalEstimatedTime(domainOrder.getEstimation().calculateTotalEstimatedTime());
        return orderEntity;
    }

    public OrderDTO toOrderDTO(Order order) {
        return new OrderDTO(
            order.getId().getValue(),
            order.getCustomerInfo().getName(),
            order.getCustomerInfo().isPriority(),
            order.getCustomerInfo().getNotes(),
            order.getItems().stream()
                .collect(HashMap::new, 
                    (m, item) -> m.put(item.getItem().getId(), item.getQuantity()),
                    HashMap::putAll),
            ""
        );
    }
}
