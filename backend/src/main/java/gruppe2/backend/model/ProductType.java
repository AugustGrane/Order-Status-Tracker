package gruppe2.backend.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Entity
@Table(name = "product_types")
public class ProductType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "productType", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("differentStepsOrder ASC, stepOrder ASC")
    private List<ProductTypeStep> steps = new ArrayList<>();

    public ProductType() {}

    public ProductType(Long id, String name, List<Long> stepIds) {
        this.id = id;
        this.name = name;
        setSteps(stepIds);
    }

    // Constructor for backward compatibility
    public ProductType(Long id, String name, Long[] stepIds) {
        this.id = id;
        this.name = name;
        if (stepIds != null) {
            setSteps(Arrays.asList(stepIds));
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // For backward compatibility with existing code
    public List<Long> getDifferentSteps() {
        return steps.stream()
                   .map(ProductTypeStep::getStepId)
                   .collect(Collectors.toList());
    }

    public void setDifferentSteps(List<Long> stepIds) {
        setSteps(stepIds);
    }

    private void setSteps(List<Long> stepIds) {
        // Clear existing steps
        this.steps.clear();
        
        // Add new steps
        if (stepIds != null) {
            for (int i = 0; i < stepIds.size(); i++) {
                ProductTypeStep step = new ProductTypeStep(this, stepIds.get(i), i);
                this.steps.add(step);
            }
        }
    }
    
    // Backward compatibility methods
    public Long[] getDifferentStepsArray() { 
        return getDifferentSteps().toArray(new Long[0]); 
    }
    
    public void setDifferentSteps(Long[] stepIds) { 
        if (stepIds != null) {
            setSteps(Arrays.asList(stepIds));
        }
    }
}
