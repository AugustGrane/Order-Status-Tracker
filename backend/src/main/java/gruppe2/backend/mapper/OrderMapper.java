package gruppe2.backend.mapper;

import gruppe2.backend.domain.Order;
import gruppe2.backend.dto.OrderDTO;
import gruppe2.backend.model.OrderModel;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class OrderMapper {
    
    public OrderModel toModelOrder(Order domainOrder) {
        OrderModel orderModelEntity = new OrderModel();
        orderModelEntity.setId(domainOrder.getId().getValue());
        orderModelEntity.setCustomerName(domainOrder.getCustomerInfo().getName());
        orderModelEntity.setPriority(domainOrder.getCustomerInfo().isPriority());
        orderModelEntity.setNotes(domainOrder.getCustomerInfo().getNotes());
        orderModelEntity.setOrderCreated(domainOrder.getTimeline().getOrderCreated());
        orderModelEntity.setTotalEstimatedTime(domainOrder.getEstimation().calculateTotalEstimatedTime());
        return orderModelEntity;
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
