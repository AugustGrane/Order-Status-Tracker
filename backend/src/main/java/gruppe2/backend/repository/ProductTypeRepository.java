package gruppe2.backend.repository;

import gruppe2.backend.model.*;
import gruppe2.backend.repository.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
    List<ProductType> findByNameContainingIgnoreCase(String name);
    List<ProductTypeProjection> findAllProjectedBy();
}
