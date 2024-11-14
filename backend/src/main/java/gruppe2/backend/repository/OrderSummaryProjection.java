package gruppe2.backend.repository;

import java.time.LocalDateTime;

public interface OrderSummaryProjection {
    Long getOrderId();
    String getCustomerName();
    LocalDateTime getOrderCreated();
    Boolean getPriority();
    Long getTotalItems();
}
