package gruppe2.backend.controller.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LineItem {
    private String name;
    private int quantity;
    private long product_id;

    @JsonProperty("image")
    private Img img;

    public Img getImg() {
        return img;
    }

    public void setImg(Img img) {
        this.img = img;
    }

    public LineItem(String name, int quantity, long product_id, Img img) {
        this.name = name;
        this.quantity = quantity;
        this.product_id = product_id;
        this.img = img;
    }

    public long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(long product_id) {
        this.product_id = product_id;
    }

// Getters and setters...

    @Override
    public String toString() {
        return "LineItem{" +
                "name='" + name + '\'' +
                ", quantity=" + quantity +
                ", product_id=" + product_id +
                ", img=" + img.toString() +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}