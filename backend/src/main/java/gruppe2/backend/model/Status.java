package gruppe2.backend.model;

public class Status {
    private String name;
    private String image;
    private int estimatedTime;

    public Status() {
    }

    public Status(String name, String image, int estimatedTime) {
        this.name = name;
        this.image = image;
        this.estimatedTime = estimatedTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}
