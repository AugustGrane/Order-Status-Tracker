package gruppe2.backend.repository;

import gruppe2.backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {
    List<OrderModel> findByCustomerNameContainingIgnoreCase(String customerName);
    List<OrderModel> findByPriorityTrue();
    
    @Query(value = """
            SELECT DISTINCT o 
            FROM OrderModel o 
            WHERE 1=1 
            ORDER BY o.orderCreated DESC
            """)
    List<OrderModel> findAllWithDetailsOrderByOrderCreatedAsc();

    @Query(value = """
            SELECT o.id as orderId, 
                   o.customerName as customerName, 
                   o.orderCreated as orderCreated, 
                   o.priority as priority,
                   COUNT(od.id) as totalItems 
            FROM OrderModel o 
            LEFT JOIN o.orderDetails od 
            GROUP BY o.id, o.customerName, o.orderCreated, o.priority 
            ORDER BY o.orderCreated DESC
            """)
    List<OrderSummaryProjection> findAllOrderSummaries();
}