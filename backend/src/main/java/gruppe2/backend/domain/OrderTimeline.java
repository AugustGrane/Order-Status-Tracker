package gruppe2.backend.domain;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;

public class OrderTimeline {
    private final LocalDateTime orderCreated;
    private final Map<Long, Map<Long, LocalDateTime>> itemStatusTimestamps;
    private final boolean isPriority;

    public OrderTimeline(LocalDateTime orderCreated, boolean isPriority) {
        this.orderCreated = orderCreated;
        this.itemStatusTimestamps = new HashMap<>();
        this.isPriority = isPriority;
    }

    public void recordItemStatus(Long itemId, Long statusId, LocalDateTime timestamp) {
        itemStatusTimestamps.computeIfAbsent(itemId, k -> new HashMap<>())
                          .put(statusId, timestamp);
    }

    public Duration getItemDuration(Long itemId) {
        Map<Long, LocalDateTime> timestamps = itemStatusTimestamps.get(itemId);
        if (timestamps == null || timestamps.isEmpty()) {
            return Duration.ZERO;
        }

        LocalDateTime earliest = timestamps.values().stream()
                .min(LocalDateTime::compareTo)
                .orElse(orderCreated);
        LocalDateTime latest = timestamps.values().stream()
                .max(LocalDateTime::compareTo)
                .orElse(orderCreated);

        return Duration.between(earliest, latest);
    }

    public Duration getTotalDuration() {
        if (itemStatusTimestamps.isEmpty()) {
            return Duration.ZERO;
        }

        LocalDateTime latest = itemStatusTimestamps.values().stream()
                .flatMap(map -> map.values().stream())
                .max(LocalDateTime::compareTo)
                .orElse(orderCreated);

        return Duration.between(orderCreated, latest);
    }

    public LocalDateTime getLatestUpdate() {
        return itemStatusTimestamps.values().stream()
                .flatMap(map -> map.values().stream())
                .max(LocalDateTime::compareTo)
                .orElse(orderCreated);
    }

    public Map<Long, LocalDateTime> getItemTimeline(Long itemId) {
        return new HashMap<>(itemStatusTimestamps.getOrDefault(itemId, new HashMap<>()));
    }

    public boolean isPriority() {
        return isPriority;
    }

    public LocalDateTime getOrderCreated() {
        return orderCreated;
    }

    public boolean hasStatus(Long itemId, Long statusId) {
        return itemStatusTimestamps.containsKey(itemId) &&
               itemStatusTimestamps.get(itemId).containsKey(statusId);
    }

    public Optional<LocalDateTime> getStatusTimestamp(Long itemId, Long statusId) {
        return Optional.ofNullable(itemStatusTimestamps.get(itemId))
                      .map(timestamps -> timestamps.get(statusId));
    }

    public List<Map.Entry<Long, LocalDateTime>> getItemStatusesSorted(Long itemId) {
        Map<Long, LocalDateTime> timestamps = itemStatusTimestamps.getOrDefault(itemId, new HashMap<>());
        List<Map.Entry<Long, LocalDateTime>> sortedEntries = new ArrayList<>(timestamps.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue());
        return sortedEntries;
    }
}
