package by.kes.events.handler;

import by.kes.events.model.EventUser;
import by.kes.events.repository.EventUserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

@Component
public class UsersHandler {

    @Autowired
    private EventUserMongoRepository eventUserMongoRepository;

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
