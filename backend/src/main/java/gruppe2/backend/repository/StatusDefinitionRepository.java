package gruppe2.backend.repository;

import gruppe2.backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Set;

@Repository
public interface StatusDefinitionRepository extends JpaRepository<StatusDefinition, Long> {
    List<StatusDefinition> findByNameContainingIgnoreCase(String name);
    
    @Query(value = """
        SELECT DISTINCT sd FROM StatusDefinition sd 
        WHERE sd.id IN :ids 
        ORDER BY sd.id""")
    List<StatusDefinition> findAllByIds(@Param("ids") Set<Long> ids);
}