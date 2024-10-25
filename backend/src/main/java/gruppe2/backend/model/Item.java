package gruppe2.backend.model;

public class Item {
    private int id;
    private String name;
    private int amount;
    private String product;
    private ProductType productType;

    public Item() {
    }

    public Item(int id, String name, int amount, String product) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.product = product;
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

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }
}
