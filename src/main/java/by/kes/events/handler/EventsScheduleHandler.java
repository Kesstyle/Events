package by.kes.events.handler;

import by.kes.events.repository.EventScheduleMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventsScheduleHandler {

    @Autowired
    private EventScheduleMongoRepository eventScheduleMongoRepository;
}
