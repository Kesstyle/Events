package by.kes.events.repository;

import by.kes.events.model.EventStream;
import reactor.core.publisher.Flux;

import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface EventStreamMongoRepository extends ReactiveCrudRepository<EventStream, String> {

  @Tailable
  Flux<EventStream> findWithTailableCursorByTimestampGreaterThanAndUserId(final Long timestamp,
                                                                          final String userId);
}
