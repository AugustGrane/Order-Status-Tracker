package gruppe2.backend.model;

import jakarta.persistence.*;
import org.hibernate.usertype.UserType;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "product_types")
public class ProductType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ElementCollection
    @CollectionTable(name = "product_type_steps",
                    joinColumns = @JoinColumn(name = "product_type_id"))
    @Column(name = "step_id")
    @OrderColumn(name = "step_order")
    private List<Long> differentSteps = new ArrayList<>();

    public ProductType() {}

    public ProductType(Long id, String name, List<Long> differentSteps) {
        this.id = id;
        this.name = name;
        this.differentSteps = differentSteps != null ? new ArrayList<>(differentSteps) : new ArrayList<>();
    }

    // Constructor for backward compatibility
    public ProductType(Long id, String name, Long[] differentSteps) {
        this.id = id;
        this.name = name;
        this.differentSteps = new ArrayList<>();
        if (differentSteps != null) {
            for (Long step : differentSteps) {
                this.differentSteps.add(step);
            }
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Long> getDifferentSteps() { return differentSteps != null ? new ArrayList<>(differentSteps) : new ArrayList<>(); }
    public void setDifferentSteps(List<Long> differentSteps) { this.differentSteps = differentSteps != null ? new ArrayList<>(differentSteps) : new ArrayList<>(); }
    
    // Backward compatibility methods
    public Long[] getDifferentStepsArray() { return differentSteps.toArray(new Long[0]); }
    public void setDifferentSteps(Long[] differentSteps) { 
        this.differentSteps = new ArrayList<>();
        if (differentSteps != null) {
            for (Long step : differentSteps) {
                this.differentSteps.add(step);
            }
        }
    }
}
