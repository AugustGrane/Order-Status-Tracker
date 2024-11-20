package gruppe2.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product_type_steps")
public class ProductTypeStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductType productType;

    @Column(name = "step_id")
    private Long stepId;

    @Column(name = "different_steps_order", nullable = false)
    private Integer differentStepsOrder;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    public ProductTypeStep() {}

    public ProductTypeStep(ProductType productType, Long stepId, Integer order) {
        this.productType = productType;
        this.stepId = stepId;
        this.differentStepsOrder = order;
        this.stepOrder = order;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProductType getProductType() { return productType; }
    public void setProductType(ProductType productType) { this.productType = productType; }

    public Long getStepId() { return stepId; }
    public void setStepId(Long stepId) { this.stepId = stepId; }

    public Integer getDifferentStepsOrder() { return differentStepsOrder; }
    public void setDifferentStepsOrder(Integer differentStepsOrder) { this.differentStepsOrder = differentStepsOrder; }

    public Integer getStepOrder() { return stepOrder; }
    public void setStepOrder(Integer stepOrder) { this.stepOrder = stepOrder; }
}
