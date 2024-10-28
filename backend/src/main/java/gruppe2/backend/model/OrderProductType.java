package gruppe2.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "order_product_types")
public class OrderProductType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "name")
    private String name;

    @ElementCollection
    @CollectionTable(name = "order_product_type_steps", 
                    joinColumns = @JoinColumn(name = "order_product_type_id"))
    @Column(name = "step_id")
    private Long[] differentSteps;

    @ElementCollection
    @CollectionTable(name = "order_product_type_updated", 
                    joinColumns = @JoinColumn(name = "order_product_type_id"))
    @MapKeyColumn(name = "status_definition_id")
    @Column(name = "updated")
    private Map<Long, LocalDateTime> updated;

    public OrderProductType() {}

    public OrderProductType(Long id, Long orderId, String name, Long[] differentSteps, 
                          Map<Long, LocalDateTime> updated) {
        this.id = id;
        this.orderId = orderId;
        this.name = name;
        this.differentSteps = differentSteps;
        this.updated = updated;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long[] getDifferentSteps() { return differentSteps; }
    public void setDifferentSteps(Long[] differentSteps) { this.differentSteps = differentSteps; }

    public Map<Long, LocalDateTime> getUpdated() { return updated; }
    public void setUpdated(Map<Long, LocalDateTime> updated) { this.updated = updated; }
}
