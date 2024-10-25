package gruppe2.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .load();
        
        System.setProperty("NEON_DB_USERNAME", dotenv.get("NEON_DB_USERNAME"));
        System.setProperty("NEON_DB_PASSWORD", dotenv.get("NEON_DB_PASSWORD"));
        
        SpringApplication.run(BackendApplication.class, args);
    }
}
