package gruppe2.backend.domain.event;

public class ProductTypeChangedEvent extends OrderEvent {
    private final Long itemId;
    private final Long oldProductTypeId;
    private final Long newProductTypeId;

    public ProductTypeChangedEvent(Long orderId, Long itemId, Long oldProductTypeId, Long newProductTypeId) {
        super(orderId);
        this.itemId = itemId;
        this.oldProductTypeId = oldProductTypeId;
        this.newProductTypeId = newProductTypeId;
    }

    public Long getItemId() {
        return itemId;
    }

    public Long getOldProductTypeId() {
        return oldProductTypeId;
    }

    public Long getNewProductTypeId() {
        return newProductTypeId;
    }

    public boolean isInitialAssignment() {
        return oldProductTypeId == 0;
    }

    @Override
    public String getEventType() {
        return "PRODUCT_TYPE_CHANGED";
    }
}
