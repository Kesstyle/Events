package by.kes.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetEventsResponse {

  @JsonProperty("items")
  private List<Event> events;

  public List<Event> getEvents() {
    return events;
  }

  public void setEvents(List<Event> events) {
    this.events = events;
  }
}
