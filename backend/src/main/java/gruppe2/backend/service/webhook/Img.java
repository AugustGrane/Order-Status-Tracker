package gruppe2.backend.controller.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Img {
    @Override
    public String toString() {
        return "Img{" +
                "src='" + src + '\'' +
                '}';
    }

    private String src;


    public Img(int id, String src) {
        this.src = src;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
