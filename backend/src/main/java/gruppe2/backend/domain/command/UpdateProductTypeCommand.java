package gruppe2.backend.domain.command;

import gruppe2.backend.domain.Order;
import gruppe2.backend.domain.ProductTypeTransition;
import gruppe2.backend.domain.specification.CanChangeProductTypeSpecification;
import gruppe2.backend.domain.specification.HasItemSpecification;

/**
 * Command for updating a product type within an order.
 * Implements both Command and OrderCommand interfaces.
 */
public class UpdateProductTypeCommand implements Command<Void>, OrderCommand {
    private final Long itemId;
    private final ProductTypeTransition transition;

    public UpdateProductTypeCommand(Long itemId, ProductTypeTransition transition) {
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID cannot be null");
        }
        if (transition == null) {
            throw new IllegalArgumentException("Product type transition cannot be null");
        }
        this.itemId = itemId;
        this.transition = transition;
    }

    @Override
    public Void execute() {
        // This method is required by Command<T> interface but not used
        // The actual execution is handled by execute(Order)
        throw new UnsupportedOperationException("Use execute(Order) instead");
    }

    @Override
    public void execute(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        // Use specifications to validate the operation
        HasItemSpecification hasItem = new HasItemSpecification(itemId);
        CanChangeProductTypeSpecification canChange = new CanChangeProductTypeSpecification(itemId);

        if (!hasItem.and(canChange).isSatisfiedBy(order)) {
            throw new IllegalStateException(
                String.format("Cannot update product type for item %d: item not found or cannot change type", itemId)
            );
        }

        order.updateItemProductType(itemId, transition);
    }

    public Long getItemId() {
        return itemId;
    }

    public ProductTypeTransition getTransition() {
        return transition;
    }

    @Override
    public String toString() {
        return String.format("UpdateProductTypeCommand{itemId=%d, transition=%s}", itemId, transition);
    }
}
