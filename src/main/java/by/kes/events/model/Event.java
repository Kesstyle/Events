package by.kes.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {

  private Long id;
  private String name;

  @JsonProperty("dateExpire")
  private String date;

  private boolean done;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
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
}
