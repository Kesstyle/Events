package by.kes.events.configuration;

import by.kes.events.handler.CommonHandler;
import by.kes.events.handler.EventsHandler;
import by.kes.events.handler.EventsScheduleHandler;
import by.kes.events.handler.UsersHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class EventsConfiguration implements WebFluxConfigurer {

    @Bean
    public RouterFunction<ServerResponse> routes(final EventsHandler eventsHandler, final UsersHandler usersHandler,
                                                 final EventsScheduleHandler eventsScheduleHandler, final CommonHandler commonHandler) {
        return RouterFunctions.route(GET("/api/events"), eventsHandler::getEvents)
                .andRoute(POST("/api/event"), eventsHandler::addEvent)
                .andRoute(OPTIONS("/api/**"), commonHandler::getOptions)
                .andRoute(DELETE("/api/event/{id}"), eventsHandler::removeEvent)
                .andRoute(PUT("/api/event"), eventsHandler::updateEvent)
                .andRoute(GET("/api/users"), usersHandler::getUsers)
                .andRoute(GET("/api/user/{id}"), usersHandler::getUser)
                .andRoute(POST("/api/user"), usersHandler::saveUser);
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedMethods("*")
                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Request-Headers", "Access-Control-Request-Method")
                .maxAge(3600);
    }
}
