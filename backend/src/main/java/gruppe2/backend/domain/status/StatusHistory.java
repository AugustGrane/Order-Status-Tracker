package gruppe2.backend.domain.status;

import java.util.Date;

public class StatusHistory {
    private Date timestamp;
    private Date updatedBy;

    public StatusHistory() {
        this.timestamp = new Date();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Date updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date update() {
        this.timestamp = new Date();
        return this.timestamp;
    }
}
