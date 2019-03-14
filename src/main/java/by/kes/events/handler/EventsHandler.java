package by.kes.events.handler;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

import static by.kes.events.model.EventModifyType.ADD;
import static by.kes.events.model.EventModifyType.REMOVE;
import static by.kes.events.model.EventModifyType.UPDATE;

import by.kes.events.model.Event;
import by.kes.events.model.EventStream;
import by.kes.events.model.EventUser;
import by.kes.events.repository.EventMongoRepository;
import by.kes.events.repository.EventStreamMongoRepository;
import by.kes.events.repository.EventUserMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.Duration;
import java.util.Optional;

@Component
public class EventsHandler {

  @Autowired
  private EventMongoRepository eventMongoRepository;

  @Autowired
  private EventUserMongoRepository eventUserMongoRepository;

  @Autowired
  private EventStreamMongoRepository eventStreamMongoRepository;

  public Mono<ServerResponse> getEvents(final ServerRequest request) {
    final Long timestamp = System.currentTimeMillis();
    final String userId = request.queryParam("UID_H")
        .orElse(null);
    return ServerResponse.ok().contentType(TEXT_EVENT_STREAM)
        .body(eventMongoRepository
                .findByTimestampLessThanAndUserId(timestamp, userId)
                .mergeWith(
            eventStreamMongoRepository
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
                )),
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
            .map(e -> {
              final EventStream eventStream = new EventStream();
              eventStream.setRefId(e.getId());
              eventStream.setAction(ADD.toString());
              eventStream.setTimestamp(timestamp);
              eventStream.setUserId(e.getUserId());
              return eventStream;
            })
            .flatMap(es -> eventStreamMongoRepository.save(es))
            .then(), Void.class);
  }

  public Mono<ServerResponse> removeEvent(final ServerRequest serverRequest) {
    final Long timestamp = System.currentTimeMillis();
    return ServerResponse.ok().contentType(TEXT_EVENT_STREAM)
        .body(Flux.just(serverRequest.pathVariable("id"))
            .flatMap(id -> eventMongoRepository.findById(id))
            .map(event -> {
              final EventStream eventStream = new EventStream();
              eventStream.setRefId(event.getId());
              eventStream.setAction(REMOVE.toString());
              eventStream.setTimestamp(timestamp);
              eventStream.setUserId(event.getUserId());
              return eventStream;
            })
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
            .map(e -> {
              final EventStream eventStream = new EventStream();
              eventStream.setRefId(e.getId());
              eventStream.setAction(UPDATE.toString());
              eventStream.setTimestamp(timestamp);
              eventStream.setUserId(e.getUserId());
              return eventStream;
            })
            .flatMap(es -> eventStreamMongoRepository.save(es))
            .then(), Void.class);
  }

  public Mono<ServerResponse> getUsers(final ServerRequest serverRequest) {
    return ServerResponse.ok().contentType(TEXT_EVENT_STREAM)
        .body(eventUserMongoRepository.findAll(), EventUser.class);
  }

  public Mono<ServerResponse> getUser(final ServerRequest serverRequest) {
    return ServerResponse.ok().contentType(TEXT_EVENT_STREAM)
        .body(eventUserMongoRepository.findByUserId(serverRequest.pathVariable("id")), EventUser.class);
  }

  public Mono<ServerResponse> saveUser(final ServerRequest serverRequest) {
    return ServerResponse.ok().contentType(TEXT_EVENT_STREAM)
        .body(eventUserMongoRepository.saveAll(serverRequest.bodyToFlux(EventUser.class)).then(), Void.class);
  }
}
