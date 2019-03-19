package by.kes.events.repository;

import by.kes.events.model.EventSchedule;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface EventScheduleMongoRepository extends ReactiveCrudRepository<EventSchedule, String> {

    Flux<EventSchedule> findByEventId(final String eventId);
}
