package gruppe2.backend.domain;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;

public class OrderEstimation {
    private final Map<Long, Integer> itemQuantities;
    private final Map<Long, Integer> itemProcessingTimes;
    private final boolean isPriority;
    private static final int DEFAULT_PROCESSING_TIME = 10; // minutes per item
    private static final double PRIORITY_MULTIPLIER = 0.75; // 25% faster for priority orders

    public OrderEstimation(Map<Long, Integer> itemQuantities, Map<Long, Integer> itemProcessingTimes, boolean isPriority) {
        this.itemQuantities = new HashMap<>(itemQuantities);
        this.itemProcessingTimes = new HashMap<>(itemProcessingTimes);
        this.isPriority = isPriority;
    }

    public int calculateTotalEstimatedTime() {
        int totalMinutes = itemQuantities.entrySet().stream()
            .mapToInt(entry -> {
                int processingTime = itemProcessingTimes.getOrDefault(entry.getKey(), DEFAULT_PROCESSING_TIME);
                return entry.getValue() * processingTime;
            })
            .sum();

        return isPriority ? 
            (int) (totalMinutes * PRIORITY_MULTIPLIER) : 
            totalMinutes;
    }

    public LocalDateTime calculateEstimatedCompletion(LocalDateTime startTime) {
        return startTime.plusMinutes(calculateTotalEstimatedTime());
    }

    public Map<Long, Duration> getItemEstimations() {
        Map<Long, Duration> estimations = new HashMap<>();
        
        itemQuantities.forEach((itemId, quantity) -> {
            int processingTime = itemProcessingTimes.getOrDefault(itemId, DEFAULT_PROCESSING_TIME);
            int totalTime = quantity * processingTime;
            if (isPriority) {
                totalTime = (int) (totalTime * PRIORITY_MULTIPLIER);
            }
            estimations.put(itemId, Duration.ofMinutes(totalTime));
        });

        return estimations;
    }

    public Duration getEstimatedDuration() {
        return Duration.ofMinutes(calculateTotalEstimatedTime());
    }

    public boolean isDelayed(OrderTimeline timeline) {
        LocalDateTime estimatedCompletion = calculateEstimatedCompletion(timeline.getOrderCreated());
        return LocalDateTime.now().isAfter(estimatedCompletion);
    }

    public double getCompletionPercentage(OrderTimeline timeline) {
        Duration totalEstimated = getEstimatedDuration();
        Duration elapsed = Duration.between(timeline.getOrderCreated(), LocalDateTime.now());
        
        return Math.min(100.0, (elapsed.toMinutes() * 100.0) / totalEstimated.toMinutes());
    }

    public Map<Long, Boolean> getItemDelayStatus(OrderTimeline timeline) {
        Map<Long, Boolean> delayStatus = new HashMap<>();
        Map<Long, Duration> itemEstimations = getItemEstimations();
        
        itemEstimations.forEach((itemId, estimatedDuration) -> {
            LocalDateTime startTime = timeline.getItemTimeline(itemId).values().stream()
                .min(LocalDateTime::compareTo)
                .orElse(timeline.getOrderCreated());
                
            LocalDateTime estimatedCompletion = startTime.plus(estimatedDuration);
            delayStatus.put(itemId, LocalDateTime.now().isAfter(estimatedCompletion));
        });

        return delayStatus;
    }
}
