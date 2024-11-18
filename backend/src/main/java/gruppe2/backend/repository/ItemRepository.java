package gruppe2.backend.repository;

import gruppe2.backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByProductTypeId(Long productTypeId);
    List<Item> findByNameContainingIgnoreCase(String name);
    void deleteItemById(Long id);
}