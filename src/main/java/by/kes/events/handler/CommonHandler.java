package by.kes.events.handler;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class CommonHandler {

  public Mono<ServerResponse> getOptions(final ServerRequest request) {
    return ServerResponse.ok().header("Access-Control-Allow-Origin", "*").build();
  }
}
