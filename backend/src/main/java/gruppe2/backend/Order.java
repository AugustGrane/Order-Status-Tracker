package gruppe2.backend;

public class Order {
    private String id;
    private int productType;
    private int quantity;

    public void setId(String id) {
        this.id = id;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Order(String id, int productType, int quantity) {
        this.id = id;
        this.productType = productType;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public int getProductType() {
        return productType;
    }

    public int getQuantity() {
        return quantity;
    }
}


