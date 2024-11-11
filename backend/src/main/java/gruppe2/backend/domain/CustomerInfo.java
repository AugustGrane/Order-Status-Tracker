package gruppe2.backend.domain;

import java.util.Objects;

public class CustomerInfo {
    private final String name;
    private final String notes;
    private final boolean priority;

    public CustomerInfo(String name, String notes, boolean priority) {
        this.name = name;
        this.notes = notes;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerInfo that = (CustomerInfo) o;
        return priority == that.priority &&
               Objects.equals(name, that.name) &&
               Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, notes, priority);
    }

    @Override
    public String toString() {
        return String.format("CustomerInfo{name='%s', priority=%s}", name, priority);
    }
}
