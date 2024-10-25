package gruppe2.backend.model;

import java.util.ArrayList;
import java.util.List;

public class ProductType {
    private int id;
    private String name;
    private int amount;
    private List<Status> statuses;
    private StatusHistory statusHistory;

    public ProductType() {
        this.statuses = new ArrayList<>();
        this.statusHistory = new StatusHistory();
    }

    public ProductType(int id, String name, int amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.statuses = new ArrayList<>();
        this.statusHistory = new StatusHistory();
    }

    public void changeStatus(int statusIndex) {
        if (statusIndex >= 0 && statusIndex < statuses.size()) {
            statusHistory.update();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public StatusHistory getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(StatusHistory statusHistory) {
        this.statusHistory = statusHistory;
    }
}
