package gruppe2.backend.domain;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderStatus {
    private final Long[] steps;
    private int currentStepIndex;
    private final Map<Long, LocalDateTime> statusUpdates;

    public OrderStatus(List<Long> steps) {
        this.steps = steps.toArray(new Long[0]);
        this.currentStepIndex = 0;
        this.statusUpdates = new HashMap<>();
        this.statusUpdates.put(this.steps[0], LocalDateTime.now());
    }

    public OrderStatus(Long[] steps, int currentStepIndex, Map<Long, LocalDateTime> statusUpdates) {
        this.steps = steps.clone();
        this.currentStepIndex = currentStepIndex;
        this.statusUpdates = new HashMap<>(statusUpdates);
    }

    public OrderStatus(List<Long> steps, int currentStepIndex, Map<Long, LocalDateTime> statusUpdates) {
        this.steps = steps.toArray(new Long[0]);
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

    public void moveToStep(int newStepIndex) {
        //LAV ERROR HANDELING
        //if (!canMoveToNextStep()) {
        //    throw new IllegalStateException("Failed in trying to update the step");
        //}
        currentStepIndex = newStepIndex ;
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

    public Map<Long, LocalDateTime> getStatusUpdates() {
        return new HashMap<>(statusUpdates);
    }

    public Long[] getSteps() {
        return steps.clone();
    }

    public List<Long> getStepsList() {
        return List.of(steps);
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
