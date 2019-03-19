package by.kes.events.handler;

import by.kes.events.model.Event;
import by.kes.events.model.EventModifyType;
import by.kes.events.model.EventSchedule;
import by.kes.events.model.EventStream;
import by.kes.events.repository.EventMongoRepository;
import by.kes.events.repository.EventScheduleMongoRepository;
import by.kes.events.repository.EventStreamMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Function;

import static by.kes.events.model.EventModifyType.*;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

@Component
public class EventsHandler {

    @Autowired
    private EventMongoRepository eventMongoRepository;

    @Autowired
    private EventScheduleMongoRepository eventScheduleMongoRepository;

    @Autowired
    private EventStreamMongoRepository eventStreamMongoRepository;

    public Mono<ServerResponse> getEvents(final ServerRequest request) {
        final Long timestamp = System.currentTimeMillis();
        final String userId = request.queryParam("UID_H")
                .orElse(null);
        return ServerResponse.ok().contentType(TEXT_EVENT_STREAM)
                .body(existingEvents(timestamp, userId)
                                .mergeWith(newEventsStream(timestamp, userId)),
                        Event.class);
    }

    public Mono<ServerResponse> addEvent(final ServerRequest serverRequest) {
        final Long timestamp = System.currentTimeMillis();
        return ServerResponse.ok().contentType(TEXT_EVENT_STREAM)
                .body(eventMongoRepository.saveAll(serverRequest.bodyToMono(Event.class)
                        .map(e -> {
                            e.setTimestamp(timestamp);
                            return e;
                        }))
                        .flatMap(e -> {
                            if (e.getSchedule() != null && e.getSchedule().getScheduledDateTimes() != null) {
                                return Flux.fromIterable(e.getSchedule().getScheduledDateTimes())
                                        .map(sch -> buildSchedule(sch, e))
                                        .flatMap(eSch -> eventScheduleMongoRepository.save(eSch))
                                        .then(Mono.just(e));
                            } else {
                                return Mono.just(e);
                            }
                        })
                        .map(e -> buildEventStream(e, timestamp, ADD))
                        .flatMap(es -> eventStreamMongoRepository.save(es))
                        .then(), Void.class);
    }

    public Mono<ServerResponse> removeEvent(final ServerRequest serverRequest) {
        final Long timestamp = System.currentTimeMillis();
        return ServerResponse.ok().contentType(TEXT_EVENT_STREAM)
                .body(Flux.just(serverRequest.pathVariable("id"))
                        .flatMap(id -> eventMongoRepository.findById(id))
                        .map(event -> buildEventStream(event, timestamp, REMOVE))
                        .flatMap(es -> eventStreamMongoRepository.save(es))
                        .delayElements(Duration.ofSeconds(1))
                        // TODO: FIX ORDER!
                        .flatMap(es -> eventMongoRepository.deleteById(es.getRefId()))
                        .then(), Void.class);
    }

    public Mono<ServerResponse> updateEvent(final ServerRequest serverRequest) {
        final Long timestamp = System.currentTimeMillis();
        return ServerResponse.ok().contentType(TEXT_EVENT_STREAM)
                .body(eventMongoRepository.saveAll(serverRequest.bodyToFlux(Event.class)
                        .map(e -> {
                            e.setTimestamp(timestamp);
                            return e;
                        }))
                        .map(e -> buildEventStream(e, timestamp, UPDATE))
                        .flatMap(es -> eventStreamMongoRepository.save(es))
                        .then(), Void.class);
    }

    private Flux<Event> existingEvents(final Long timestamp, final String userId) {
        return eventMongoRepository.findByTimestampLessThanAndUserId(timestamp, userId)
                .flatMap(updateWithSchedules());
    }

    private Flux<Event> newEventsStream(final Long timestamp, final String userId) {
        return eventStreamMongoRepository
                .findWithTailableCursorByTimestampGreaterThanAndUserId(timestamp, userId)
                .flatMap(es ->
                        eventMongoRepository.findAllById(Mono.just(es.getRefId()))
                                .map(e -> {
                                    if (UPDATE.toString().equals(es.getAction())) {
                                        e.setId("*" + e.getId());
                                    }
                                    if (REMOVE.toString().equals(es.getAction())) {
                                        e.setId("--" + e.getId());
                                    }
                                    return e;
                                })
                )
                .flatMap(updateWithSchedules());
    }

    private Function<Event, Mono<Event>> updateWithSchedules() {
        return e -> eventScheduleMongoRepository.findByEventId(e.getId())
                .map(sch -> {
                    e.getSchedule().getScheduledDateTimes().add(sch.getDate());
                    return sch;
                }).then(Mono.just(e));
    }

    private EventSchedule buildSchedule(final String sch, final Event e) {
        final EventSchedule eventSchedule = new EventSchedule();
        eventSchedule.setEventId(e.getId());
        eventSchedule.setDate(sch);
        return eventSchedule;
    }

    private EventStream buildEventStream(final Event e, final Long timestamp, final EventModifyType eventModifyType) {
        final EventStream eventStream = new EventStream();
        eventStream.setRefId(e.getId());
        eventStream.setAction(eventModifyType.toString());
        eventStream.setTimestamp(timestamp);
        eventStream.setUserId(e.getUserId());
        return eventStream;
    }
}
