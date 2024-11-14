package gruppe2.backend.repository;

import gruppe2.backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Order operations.
 * Provides methods for querying and managing orders in the database.
 * Extends JpaRepository to inherit basic CRUD operations.
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {

    // ==================== Basic Query Methods ====================

    /**
     * Finds orders by customer name, ignoring case.
     * 
     * @param customerName The customer name to search for
     * @return List of orders matching the customer name
     */
    List<OrderModel> findByCustomerNameContainingIgnoreCase(String customerName);

    /**
     * Finds all priority orders.
     * 
     * @return List of orders marked as priority
     */
    List<OrderModel> findByPriorityTrue();
    
    // ==================== Custom Query Methods ====================

    /**
     * Retrieves all orders with their details, ordered by creation date.
     * Uses a custom JPQL query to fetch orders with their associated details.
     * 
     * @return List of orders with their details
     */
    @Query(value = """
            SELECT DISTINCT o 
            FROM OrderModel o 
            WHERE 1=1 
            ORDER BY o.orderCreated DESC
            """)
    List<OrderModel> findAllWithDetailsOrderByOrderCreatedAsc();

    /**
     * Retrieves order summaries including basic order information and item count.
     * Uses a custom JPQL query to aggregate order information.
     * 
     * @return List of order summaries as projections
     */
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