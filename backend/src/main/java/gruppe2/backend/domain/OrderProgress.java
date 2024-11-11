package gruppe2.backend.domain;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class OrderProgress {
    private final int currentStep;
    private final int totalSteps;
    private final Long currentStepId;
    private final Map<Long, LocalDateTime> stepHistory;

    public OrderProgress(int currentStep, int totalSteps, Long currentStepId, Map<Long, LocalDateTime> stepHistory) {
        this.currentStep = currentStep;
        this.totalSteps = totalSteps;
        this.currentStepId = currentStepId;
        this.stepHistory = new HashMap<>(stepHistory);
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public double getPercentComplete() {
        return ((double)(currentStep) / totalSteps) * 100;
    }

    public Long getCurrentStepId() {
        return currentStepId;
    }

    public Map<Long, LocalDateTime> getStepHistory() {
        return new HashMap<>(stepHistory);
    }
}
