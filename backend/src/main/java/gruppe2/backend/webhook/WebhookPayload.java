package gruppe2.backend.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookPayload {
    private long id;

    @JsonProperty("billing")
    private BillingInfo billing;

    @Override
    public String toString() {
        return "WebhookPayload{" +
                "id=" + id +
                ", billing=" + billing.toString() +
                ", items=" + items.toString() +
                '}';
    }

    @JsonProperty("line_items")
    private List<LineItem> items;

    // Getters and setters...

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BillingInfo getBilling() {
        return billing;
    }

    public void setBilling(BillingInfo billing) {
        this.billing = billing;
    }

    public List<LineItem> getItems() {
        return items;
    }

    public void setItems(List<LineItem> items) {
        this.items = items;
    }
}