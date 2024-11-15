package gruppe2.backend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "items")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item {
    @Id
    private Long id;

    @Column(name = "name")
    private String name;
    
    @Column(name = "product_type_id")
    private Long productTypeId;

    @Column(name = "item_image")
    private String image;

    public Item() {}
    
    public Item(Long id, String name, Long productTypeId) {
        this.id = id;
        this.name = name;
        this.productTypeId = productTypeId;
    }
    
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    
    public Long getProductTypeId() { 
        return productTypeId; 
    }
    
    public void setProductTypeId(Long productTypeId) { 
        this.productTypeId = productTypeId; 
    }
}