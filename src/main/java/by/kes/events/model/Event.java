package by.kes.events.model;

import by.kes.events.model.ui.EventScheduleUI;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "event")
public class Event {

    private String id;
    private String name;
    private boolean done;
    private String userId;

    @Transient
    private EventScheduleUI schedule;

    @JsonProperty("dateExpire")
    private String date;

    @JsonIgnore
    private Long timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public EventScheduleUI getSchedule() {
        if (schedule == null) {
            schedule = new EventScheduleUI();
        }
        return schedule;
    }

    public void setSchedule(final EventScheduleUI schedule) {
        this.schedule = schedule;
    }
}
