package gruppe2.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import gruppe2.backend.model.Test;
import gruppe2.backend.model.Test.TestStatus;

public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByTestNameContaining(String testName);
    List<Test> findByStatus(TestStatus status);
}
