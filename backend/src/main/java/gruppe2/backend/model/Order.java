package gruppe2.backend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "orders")
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "item_mapping",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private Set<Item> items = new HashSet<>();

    public Order() {}

    public Order(Long id, String customerName, boolean priority, String notes, 
                LocalDateTime orderCreated, int totalEstimatedTime) {
        this.id = id;
        this.customerName = customerName;
        this.priority = priority;
        this.notes = notes;
        this.orderCreated = orderCreated;
        this.totalEstimatedTime = totalEstimatedTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getOrderCreated() {
        return orderCreated;
    }

    public void setOrderCreated(LocalDateTime orderCreated) {
        this.orderCreated = orderCreated;
    }

    public int getTotalEstimatedTime() {
        return totalEstimatedTime;
    }

    public void setTotalEstimatedTime(int totalEstimatedTime) {
        this.totalEstimatedTime = totalEstimatedTime;
    }

    public String getShippingUrl() {
        return shippingUrl;
    }

    public void setShippingUrl(String shippingUrl) {
        this.shippingUrl = shippingUrl;
    }

    public Set<Item> getItems() {
        return items != null ? new HashSet<>(items) : new HashSet<>();
    }

    public void setItems(Set<Item> items) {
        this.items = items != null ? new HashSet<>(items) : new HashSet<>();
    }
}