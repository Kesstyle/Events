package by.kes.events.configuration;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.OPTIONS;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;

import by.kes.events.handler.CommonHandler;
import by.kes.events.handler.EventsHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class EventsConfiguration implements WebFluxConfigurer {

  @Bean
  public RouterFunction<ServerResponse> routes(final EventsHandler eventsHandler, final CommonHandler commonHandler) {
    return RouterFunctions.route(GET("/api/events"), eventsHandler::getEvents)
        .andRoute(POST("/api/event"), eventsHandler::addEvent)
        .andRoute(OPTIONS("/api/**"), commonHandler::getOptions)
        .andRoute(DELETE("/api/event/{id}"), eventsHandler::removeEvent)
        .andRoute(PUT("/api/event"), eventsHandler::updateEvent)
        .andRoute(GET("/api/users"), eventsHandler::getUsers)
        .andRoute(GET("/api/user/{id}"), eventsHandler::getUser)
        .andRoute(POST("/api/user"), eventsHandler::saveUser);
  }

  @Override
  public void addCorsMappings(CorsRegistry corsRegistry) {
    corsRegistry.addMapping("/**")
        .allowedMethods("*")
        .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Request-Headers", "Access-Control-Request-Method")
        .maxAge(3600);
  }
}
