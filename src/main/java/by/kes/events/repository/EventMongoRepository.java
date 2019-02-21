package by.kes.events.repository;

import by.kes.events.model.Event;
import reactor.core.publisher.Flux;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface EventMongoRepository extends ReactiveCrudRepository<Event, String> {

  Flux<Event> findByTimestampLessThan(final Long timestamp);
}
