package by.kes.events.handler;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

import by.kes.events.dao.EventDao;
import by.kes.events.model.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class EventsHandler {

  @Autowired
  private EventDao eventDao;

  public Mono<ServerResponse> getEvents(final ServerRequest request) {
    return ServerResponse.ok().contentType(TEXT_EVENT_STREAM).body(eventDao.getAll(),
        Event.class);
  }

  public Mono<ServerResponse> addEvent(final ServerRequest serverRequest) {
    return ServerResponse.ok().contentType(TEXT_EVENT_STREAM)
        .body(eventDao.add(serverRequest.bodyToMono(Event.class)), Void.class);
  }

  public Mono<ServerResponse> removeEvent(final ServerRequest serverRequest) {
    return ServerResponse.ok().contentType(TEXT_EVENT_STREAM)
        .body(eventDao.remove(Flux.just(serverRequest.pathVariable("id"))), Void.class);
  }

  public Mono<ServerResponse> updateEvent(final ServerRequest serverRequest) {
    return ServerResponse.ok().contentType(TEXT_EVENT_STREAM)
        .body(eventDao.update(serverRequest.bodyToFlux(Event.class)), Void.class);
  }
}
