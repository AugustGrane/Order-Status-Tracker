package gruppe2.backend.repository;

import gruppe2.backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductTypeRepository extends JpaRepository<OrderDetails, Long> {
    List<OrderDetails> findByOrderId(Long orderId);
    List<OrderDetails> findByCurrentStepIndex(Integer currentStepIndex);
    List<OrderDetails> findByItemId(Long itemId);
}