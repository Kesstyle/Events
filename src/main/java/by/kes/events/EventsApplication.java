package by.kes.events;

import by.kes.events.repository.EventMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${spring.data.mongodb.host}")
  private String mongoHost;

  @Value("${spring.data.mongodb.port}")
  private Integer mongoPort;

  @Value("${spring.data.mongodb.database}")
  private String database;

  public static void main(String[] args) {
    SpringApplication.run(EventsApplication.class, args);
  }

  @Bean
  @Order(1)
  CommandLineRunner mongoRunner() {
    return args -> {
      final MongoClient mongoClient = new MongoClient(mongoHost, mongoPort);
      final MongoDatabase db = mongoClient.getDatabase(database);

      final List<String> collections = db.listCollectionNames().into(new ArrayList<>());
      if (!collections.contains("event_stream")) {
        final CreateCollectionOptions options = new CreateCollectionOptions();
        options.capped(true);
        options.maxDocuments(100000);
        options.sizeInBytes(256 * 256);
        db.createCollection("event_stream", options);
        db.getCollection("event_stream").insertOne(new Document("id", "test"));
      }
      createCollectionIfNotExists(db, collections, "event");
      createCollectionIfNotExists(db, collections, "event_users");
    };
  }

  private void createCollectionIfNotExists(final MongoDatabase db,
                                           final List<String> collections, final String name) {
    if (!collections.contains(name)) {
      db.createCollection(name);
    }
  }

}

