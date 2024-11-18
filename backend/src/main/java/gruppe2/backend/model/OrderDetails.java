package gruppe2.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

@Entity
@Table(name = "order_details")
public class OrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = true)
    private Item item;

    @Column(name = "item_amount")
    private Integer itemAmount;

    @Column(name = "product_type")
    private String product_type;

    @Column(name = "current_step_index")
    private Integer currentStepIndex;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "order_product_type_steps",
            joinColumns = @JoinColumn(name = "order_product_type_id", nullable = true)
    )
    @Column(name = "step_id")
    @OrderColumn(name = "step_order")
    private List<Long> differentSteps = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "order_product_type_updated",
            joinColumns = @JoinColumn(name = "order_product_type_id", nullable = true)
    )
    @MapKeyColumn(name = "status_definition_id")
    @Column(name = "updated")
    private Map<Long, LocalDateTime> updated = new HashMap<>();

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Item getItem() {
        return item;
    }

    public Integer getItemAmount() {
        return itemAmount;
    }

    public Integer getCurrentStepIndex() {
        return currentStepIndex;
    }

    public List<Long> getDifferentSteps() {
        return differentSteps != null ? differentSteps : new ArrayList<>();
    }

    public Map<Long, LocalDateTime> getUpdated() {
        return updated != null ? updated : new HashMap<>();
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setItemAmount(Integer itemAmount) {
        this.itemAmount = itemAmount;
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public void setOrderId(Long orderId) { 
        this.orderId = orderId; 
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public void setCurrentStepIndex(Integer currentStepIndex) {
        this.currentStepIndex = currentStepIndex;
    }

    public void setDifferentSteps(List<Long> differentSteps) {
        this.differentSteps = differentSteps != null ? differentSteps : new ArrayList<>();
    }

    public void setUpdated(Map<Long, LocalDateTime> updated) {
        this.updated = updated != null ? updated : new HashMap<>();
    }
}
