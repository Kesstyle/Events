package by.kes.events.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "event_users")
public class EventUser {

  private String name;
  private String userId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
