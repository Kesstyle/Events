package by.kes.events.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "event_stream")
public class EventStream {

  private String refId;
  private String action;
  private Long timestamp;
  private String userId;

  public String getRefId() {
    return refId;
  }

  public void setRefId(String refId) {
    this.refId = refId;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
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
}
