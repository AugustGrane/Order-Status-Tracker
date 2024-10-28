package gruppe2.backend.model;

import jakarta.persistence.*;

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
    private Long[] differentSteps;

    public ProductType() {}

    public ProductType(Long id, String name, Long[] differentSteps) {
        this.id = id;
        this.name = name;
        this.differentSteps = differentSteps;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long[] getDifferentSteps() { return differentSteps; }
    public void setDifferentSteps(Long[] differentSteps) { this.differentSteps = differentSteps; }
}
