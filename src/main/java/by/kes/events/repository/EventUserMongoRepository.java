package by.kes.events.repository;

import by.kes.events.model.EventUser;
import reactor.core.publisher.Mono;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface EventUserMongoRepository extends ReactiveCrudRepository<EventUser, String> {

  Mono<EventUser> findByUserId(final String userId);
}
