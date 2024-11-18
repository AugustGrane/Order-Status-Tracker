package gruppe2.backend.dto;

public class UpdateStepDTO {
    private Long orderDetailsId;
    private int newStepIndex;

    // Getters og Setters

    public Long getOrderDetailsId() {
        return orderDetailsId;
    }

    public void setOrderDetailsId(Long orderDetailsId) {
        this.orderDetailsId = orderDetailsId;
    }

    public int getNewStepIndex() {
        return newStepIndex;
    }

    public void setNewStepIndex(int newStepIndex) {
        this.newStepIndex = newStepIndex;
    }
}
