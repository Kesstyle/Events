package by.kes.events.configuration;

import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories(basePackages = "by.kes.events.repository")
public abstract class MongoConfiguration extends AbstractReactiveMongoConfiguration {
}
