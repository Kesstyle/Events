package by.kes.events;

import by.kes.events.model.Event;
import by.kes.events.repository.EventMongoRepository;
import reactor.core.publisher.Flux;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class,
    MongoRepositoriesAutoConfiguration.class
})
@Controller
public class EventsApplication {

  @Autowired
  private EventMongoRepository mongoRepository;

  public static void main(String[] args) {
    SpringApplication.run(EventsApplication.class, args);
  }

  @Bean
  @Order(2)
  CommandLineRunner stubRunner() {
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
      mongoRepository.saveAll(Flux.just(event1, event2, event3));
    };
  }

  @Bean
  @Order(1)
  CommandLineRunner mongoRunner() {
    return args -> {
      final MongoClient mongoClient = new MongoClient("localhost", 27017);
      final MongoDatabase db = mongoClient.getDatabase("events");

//      db.getCollection("event_stream").drop();
//      db.getCollection("event").drop();

      final List<String> collections = db.listCollectionNames().into(new ArrayList<>());
      if (!collections.contains("event_stream")) {
        final CreateCollectionOptions options = new CreateCollectionOptions();
        options.capped(true);
        options.maxDocuments(100000);
        options.sizeInBytes(256 * 256);
        db.createCollection("event_stream", options);
        db.getCollection("event_stream").insertOne(new Document("id", "test"));
      }
//      db.getCollection("event_stream").insertOne(new Document("id", "test"));
      if (!collections.contains("event")) {
        db.createCollection("event");
      }
    };
  }

}

