package gruppe2.backend.domain;

import gruppe2.backend.model.Item;
import java.util.Objects;

public class OrderItem {
    private final Item item;
    private final int quantity;
    private final String productTypeName;
    private final OrderStatus status;

    public OrderItem(Item item, int quantity, String productTypeName, OrderStatus status) {
        validateItem(item);
        validateQuantity(quantity);
        this.item = item;
        this.quantity = quantity;
        this.productTypeName = productTypeName;
        this.status = status;
    }

    private void validateItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    public boolean isGenericType() {
        return item.getProductTypeId() == 0;
    }

    public boolean canChangeProductType() {
        return isGenericType();
    }

    public boolean canChangeStatus(OrderStatus newStatus) {
        if (isGenericType()) {
            return false;
        }
        
        int currentIndex = status.getCurrentStepIndex();
        int newIndex = newStatus.getCurrentStepIndex();
        
        // Can only move one step at a time, forward or backward
        return Math.abs(newIndex - currentIndex) == 1;
    }

    public OrderItem withStatus(OrderStatus newStatus) {
        if (!canChangeStatus(newStatus)) {
            throw new IllegalArgumentException("Invalid status transition");
        }
        return new OrderItem(item, quantity, productTypeName, newStatus);
    }

    public OrderItem withNewProductType(Long productTypeId, String newProductTypeName, OrderStatus newStatus) {
        if (!canChangeProductType()) {
            throw new IllegalArgumentException("Cannot change product type for non-generic items");
        }
        
        Item updatedItem = new Item();
        updatedItem.setId(item.getId());
        updatedItem.setName(item.getName());
        updatedItem.setImage(item.getImage());
        updatedItem.setProductTypeId(productTypeId);
        updatedItem.setDeleted(item.isDeleted());
        
        return new OrderItem(updatedItem, quantity, newProductTypeName, newStatus);
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Long getCurrentStepId() {
        return status.getCurrentStepId();
    }

    public int getTotalSteps() {
        return status.getSteps().length;
    }

    public int getCurrentStepIndex() {
        return status.getCurrentStepIndex();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return quantity == orderItem.quantity &&
               Objects.equals(item.getId(), orderItem.item.getId()) &&
               Objects.equals(productTypeName, orderItem.productTypeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item.getId(), quantity, productTypeName);
    }

    @Override
    public String toString() {
        return String.format(
            "OrderItem{itemId=%d, name='%s', quantity=%d, productType='%s', currentStep=%d/%d, isDeleted=%b}",
            item.getId(),
            item.getName(),
            quantity,
            productTypeName,
            getCurrentStepIndex() + 1,
            getTotalSteps(),
            item.isDeleted()
        );
    }
}
