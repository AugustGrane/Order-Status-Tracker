package gruppe2.backend.domain;

import gruppe2.backend.domain.exception.OrderException;
import java.util.Objects;

public class OrderId {
    private final Long value;

    public OrderId(Long value) {
        if (value == null || value <= 0) {
            throw new OrderException("OrderModel ID must be a positive number") {};
        }
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(value, orderId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
