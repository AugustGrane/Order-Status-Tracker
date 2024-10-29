package gruppe2.backend.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LineItem {
    private String name;
    private int quantity;
    private int id;

    @JsonProperty("image")
    private Img img;

    public Img getImg() {
        return img;
    }

    public void setImg(Img img) {
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
// Getters and setters...

    @Override
    public String toString() {
        return "LineItem{" +
                "name='" + name + '\'' +
                ", quantity=" + quantity +
                ", id=" + id +
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