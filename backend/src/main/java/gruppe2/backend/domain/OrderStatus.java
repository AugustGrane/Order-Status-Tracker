package gruppe2.backend.domain;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class OrderStatus {
    private final Long[] steps;
    private int currentStepIndex;
    private final Map<Long, LocalDateTime> statusUpdates;

    public OrderStatus(Long[] steps) {
        this.steps = steps.clone();
        this.currentStepIndex = 0;
        this.statusUpdates = new HashMap<>();
        this.statusUpdates.put(steps[0], LocalDateTime.now());
    }

    public OrderStatus(Long[] steps, int currentStepIndex, Map<Long, LocalDateTime> statusUpdates) {
        this.steps = steps.clone();
        this.currentStepIndex = currentStepIndex;
        this.statusUpdates = new HashMap<>(statusUpdates);
    }

    public boolean canMoveToNextStep() {
        return currentStepIndex < steps.length - 1;
    }

    public boolean canMoveToPreviousStep() {
        return currentStepIndex > 0;
    }

    public void moveToNextStep() {
        if (!canMoveToNextStep()) {
            throw new IllegalStateException("Already at final step");
        }
        currentStepIndex++;
        recordCurrentStep();
    }

    public void moveToPreviousStep() {
        if (!canMoveToPreviousStep()) {
            throw new IllegalStateException("Already at first step");
        }
        currentStepIndex--;
        recordCurrentStep();
    }

    private void recordCurrentStep() {
        statusUpdates.put(getCurrentStepId(), LocalDateTime.now());
    }

    public Long getCurrentStepId() {
        return steps[currentStepIndex];
    }

    public int getCurrentStepIndex() {
        return currentStepIndex;
    }

    public Long[] getSteps() {
        return steps.clone();
    }

    public Map<Long, LocalDateTime> getStatusUpdates() {
        return new HashMap<>(statusUpdates);
    }

    public OrderProgress toProgress() {
        return new OrderProgress(
            currentStepIndex,
            steps.length,
            getCurrentStepId(),
            getStatusUpdates()
        );
    }
}
