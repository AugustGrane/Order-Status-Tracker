package gruppe2.backend.dto;

import gruppe2.backend.model.Item;
import gruppe2.backend.model.StatusDefinition;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDashboardDTO {
    private final Long orderId;
    private final LocalDateTime orderCreated;
    private final boolean priority;
    private final String customerName;
    private final String notes;
    private List<OrderDetailsWithStatusDTO> items;

    // This constructor must be public for JPA to use it
    public OrderDashboardDTO(Long orderId, LocalDateTime orderCreated, boolean priority, String customerName, String notes) {
        this.orderId = orderId;
        this.orderCreated = orderCreated;
        this.priority = priority;
        this.customerName = customerName;
        this.notes = notes;
        this.items = new ArrayList<>();
    }

    public Long getOrderId() {
        return orderId;
    }

    public LocalDateTime getOrderCreated() {
        return orderCreated;
    }

    public boolean isPriority() {
        return priority;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getNotes() {
        return notes;
    }

    public List<OrderDetailsWithStatusDTO> getItems() {
        return items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public void setItems(List<OrderDetailsWithStatusDTO> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }
}
