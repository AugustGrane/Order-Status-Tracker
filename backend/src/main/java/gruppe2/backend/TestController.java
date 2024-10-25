package gruppe2.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@RestController
public class TestController {

    @Autowired
    private EntityManager entityManager;

    @GetMapping("/test-db")
    public String testConnection() {
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT 'Connection successful! Connected to ' || current_database()"
            );
            return (String) query.getSingleResult();
        } catch (Exception e) {
            return "Connection failed: " + e.getMessage();
        }
    }
}