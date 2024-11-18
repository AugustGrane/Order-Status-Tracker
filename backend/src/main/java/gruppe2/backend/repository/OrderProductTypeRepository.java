package gruppe2.backend.repository;

import gruppe2.backend.model.*;
import gruppe2.backend.dto.OrderDetailsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface OrderProductTypeRepository extends JpaRepository<OrderDetails, Long> {
    @EntityGraph(attributePaths = {"item", "updated"})
    @QueryHints({
        @QueryHint(name = "org.hibernate.readOnly", value = "true"),
        @QueryHint(name = "org.hibernate.cacheable", value = "true")
    })
    List<OrderDetails> findByOrderId(Long orderId);
    
    List<OrderDetails> findByCurrentStepIndex(Integer currentStepIndex);
    List<OrderDetails> findByItemId(Long itemId);
    
    @Query(value = """
           SELECT od FROM OrderDetails od 
           LEFT JOIN FETCH od.item i 
           LEFT JOIN FETCH od.updated 
           WHERE od.orderId IN :orderIds 
           ORDER BY od.id""")
    @EntityGraph(attributePaths = {"item", "updated"})
    @QueryHints({
        @QueryHint(name = "org.hibernate.readOnly", value = "true"),
        @QueryHint(name = "org.hibernate.cacheable", value = "true")
    })
    List<OrderDetails> findByOrderIdIn(@Param("orderIds") List<Long> orderIds);
    
    default List<OrderDetailsDTO> findOrderDetailsDTOsByOrderIds(List<Long> orderIds) {
        return findByOrderIdIn(orderIds).stream()
            .map(od -> new OrderDetailsDTO(
                od.getId(),
                od.getOrderId(),
                od.getItem(),
                od.getItemAmount(),
                od.getCurrentStepIndex(),
                od.getDifferentSteps(),
                od.getUpdated()
            ))
            .collect(Collectors.toList());
    }
    
    default Map<Long, List<OrderDetailsDTO>> findByOrderIdsGrouped(List<Long> orderIds) {
        return findOrderDetailsDTOsByOrderIds(orderIds).stream()
            .collect(Collectors.groupingBy(dto -> dto.orderId()));
    }

    void deleteAllItemsByOrderId(Long orderId);
}