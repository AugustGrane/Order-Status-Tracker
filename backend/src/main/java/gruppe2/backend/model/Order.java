package gruppe2.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private Long id;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "priority")
    private boolean priority;

    @Column(name = "notes")
    private String notes;

    @Column(name = "order_created")
    private LocalDateTime orderCreated;

    @Column(name = "total_estimated_time")
    private int totalEstimatedTime;

    @Column(name = "shipping_url")
    private String shippingUrl;

//    @ElementCollection
//    @CollectionTable(name = "item_mapping",
//                    joinColumns = @JoinColumn(name = "order_id"))
//    @MapKeyColumn(name = "item_id")
//    @Column(name = "amount")
//    private Map<Long, Integer> itemMapping;

    public Order() {}

    public Order(Long id, String customerName, boolean priority, String notes, 
                LocalDateTime orderCreated, int totalEstimatedTime, Map<Long, Integer> itemMapping) {
        this.id = id;
        this.customerName = customerName;
        this.priority = priority;
        this.notes = notes;
        this.orderCreated = orderCreated;
        this.totalEstimatedTime = totalEstimatedTime;
//        this.itemMapping = itemMapping;
    }

    // Getters and Setters


    public String getShippingUrl() {
        return shippingUrl;
    }

    public void setShippingUrl(String shippingUrl) {
        this.shippingUrl = shippingUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public boolean isPriority() { return priority; }
    public void setPriority(boolean priority) { this.priority = priority; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getOrderCreated() { return orderCreated; }
    public void setOrderCreated(LocalDateTime orderCreated) { this.orderCreated = orderCreated; }

    public int getTotalEstimatedTime() { return totalEstimatedTime; }
    public void setTotalEstimatedTime(int totalEstimatedTime) { this.totalEstimatedTime = totalEstimatedTime; }

}