package gruppe2.backend.domain.order;

import gruppe2.backend.domain.common.Notifiable;
import gruppe2.backend.domain.status.Status;
import java.util.ArrayList;
import java.util.List;

public class Order implements Notifiable {
    private int id;
    private String customerName;
    private int date;
    private List<OrderItem> items;
    private int priority;
    private String notes;
    private int totalEstimatedTime;

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(int id, String customerName, int date, int priority, String notes) {
        this.id = id;
        this.customerName = customerName;
        this.date = date;
        this.priority = priority;
        this.notes = notes;
        this.items = new ArrayList<>();
        calculateTotalEstimatedTime();
    }

    @Override
    public void notifyStatusChange() {
        calculateTotalEstimatedTime();
    }

    private void calculateTotalEstimatedTime() {
        int total = 0;
        for (OrderItem item : items) {
            if (item.getProductType() != null && !item.getProductType().getStatuses().isEmpty()) {
                Status currentStatus = item.getProductType().getStatuses().get(0); // Get first status
                total += currentStatus.getEstimatedTime() * item.getAmount();
            }
        }
        this.totalEstimatedTime = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        calculateTotalEstimatedTime();
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getTotalEstimatedTime() {
        return totalEstimatedTime;
    }
}
