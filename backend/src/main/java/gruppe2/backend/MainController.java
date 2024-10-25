package gruppe2.backend;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class MainController {

    @GetMapping("/api/hello")
    public String getHello() {
        return ("Hello World from Spring Boot!");
    }
}

