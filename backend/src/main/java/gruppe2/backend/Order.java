package gruppe2.backend;

public class Order {
    String id;
    int productType;
    int quantity;

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


