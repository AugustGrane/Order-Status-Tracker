package gruppe2.backend.repository;

import gruppe2.backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderProductTypeRepository extends JpaRepository<OrderProductType, Long> {
    List<OrderProductType> findByOrderId(Long orderId);
    List<OrderProductType> findByCurrentStepIndex(Integer currentStepIndex);
}