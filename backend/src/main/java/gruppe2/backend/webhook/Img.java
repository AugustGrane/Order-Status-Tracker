package gruppe2.backend.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Img {
    private int id;
    private String src;


    @Override
    public String toString() {
        return "Img{" +
                "id=" + id +
                ", src='" + src + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Img(int id, String src) {
        this.id = id;
        this.src = src;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
