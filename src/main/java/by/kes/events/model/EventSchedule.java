package by.kes.events.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "event_schedule")
public class EventSchedule {

    private String id;
    private String eventId;
    private String date;
    private String dateStart;
    private String dateEnd;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(final String eventId) {
        this.eventId = eventId;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(final String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(final String dateEnd) {
        this.dateEnd = dateEnd;
    }
}
