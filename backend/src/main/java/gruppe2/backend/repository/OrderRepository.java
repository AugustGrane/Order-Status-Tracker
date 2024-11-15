package gruppe2.backend.repository;

import gruppe2.backend.model.*;
import gruppe2.backend.dto.OrderDashboardDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items")
    List<Order> findAll();

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.customerName LIKE %:customerName%")
    List<Order> findByCustomerNameContainingIgnoreCase(@Param("customerName") String customerName);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.priority = true")
    List<Order> findByPriorityTrue();
    
    @Query("SELECT NEW gruppe2.backend.dto.OrderDashboardDTO(o.id, o.orderCreated, o.priority, o.customerName, o.notes) " +
           "FROM Order o " +
           "ORDER BY o.orderCreated ASC")
    List<OrderDashboardDTO> findAllForDashboard();
    
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items ORDER BY o.orderCreated ASC")
    List<Order> findAllByOrderByOrderCreatedAsc();
}