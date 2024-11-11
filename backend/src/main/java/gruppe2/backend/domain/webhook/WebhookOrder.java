package gruppe2.backend.domain.webhook;

import gruppe2.backend.domain.CustomerInfo;
import gruppe2.backend.service.webhook.WebhookPayload;
import gruppe2.backend.service.webhook.LineItem;
import java.util.Map;
import java.util.HashMap;

public class WebhookOrder {
    private final Long orderId;
    private final CustomerInfo customerInfo;
    private final Map<Long, Integer> items;

    private WebhookOrder(Long orderId, CustomerInfo customerInfo, Map<Long, Integer> items) {
        this.orderId = orderId;
        this.customerInfo = customerInfo;
        this.items = new HashMap<>(items);
    }

    public static WebhookOrder fromPayload(WebhookPayload payload) {
        return new WebhookOrder(
            payload.getId(),
            createCustomerInfo(payload),
            createItemsMap(payload)
        );
    }

    private static CustomerInfo createCustomerInfo(WebhookPayload payload) {
        String displayName = formatCustomerName(payload);
        return new CustomerInfo(
            displayName,
            "", // No notes from webhook
            false // Default priority
        );
    }

    private static String formatCustomerName(WebhookPayload payload) {
        String companyName = payload.getBilling().getCompany();
        String customerName = payload.getBilling().getFirstName() + " " + payload.getBilling().getLastName();
        
        if (companyName != null && !companyName.trim().isEmpty()) {
            return companyName + " | " + customerName;
        }
        return customerName;
    }

    private static Map<Long, Integer> createItemsMap(WebhookPayload payload) {
        Map<Long, Integer> itemsMap = new HashMap<>();
        for (LineItem item : payload.getItems()) {
            itemsMap.put(item.getProduct_id(), item.getQuantity());
        }
        return itemsMap;
    }

    public Long getOrderId() {
        return orderId;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public Map<Long, Integer> getItems() {
        return new HashMap<>(items);
    }

    public boolean hasItem(Long itemId) {
        return items.containsKey(itemId);
    }

    public int getItemQuantity(Long itemId) {
        return items.getOrDefault(itemId, 0);
    }
}
