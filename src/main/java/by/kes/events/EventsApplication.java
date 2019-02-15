package by.kes.events;

import static java.util.Arrays.asList;

import by.kes.events.dao.EventDao;
import by.kes.events.model.Event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import java.util.Arrays;

@SpringBootApplication
@Controller
public class EventsApplication {

  @Autowired
  private EventDao eventDao;

  public static void main(String[] args) {
    SpringApplication.run(EventsApplication.class, args);
  }

  @Bean
  CommandLineRunner runner() {
    return args -> {
      final Event event1 = new Event();
      event1.setDate("2019-02-02T12:12:00");
      event1.setDone(false);
      event1.setId("5");
      event1.setName("Event First");

      final Event event2 = new Event();
      event2.setDate("2019-02-02T14:14:00");
      event2.setDone(true);
      event2.setId("6");
      event2.setName("Event Second");

      final Event event3 = new Event();
      event3.setDate("2019-01-01T01:04:00");
      event3.setDone(false);
      event3.setId("7");
      event3.setName("Event Third");
      eventDao.addAll(asList(event1, event2, event3));
    };
  }
}

