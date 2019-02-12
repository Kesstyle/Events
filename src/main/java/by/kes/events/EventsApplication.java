package by.kes.events;

import by.kes.events.model.Event;
import by.kes.events.model.GetEventsResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@Controller
public class EventsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventsApplication.class, args);
	}

	@GetMapping("/api/events")
	@ResponseBody
	@CrossOrigin(exposedHeaders="Access-Control-Allow-Origin")
	public GetEventsResponse getEvents() {
		final Event event1 = new Event();
		event1.setDate("2019-02-02T12:12:00");
		event1.setDone(false);
		event1.setId(5L);
		event1.setName("Event First");

		final Event event2 = new Event();
		event2.setDate("2019-02-02T14:14:00");
		event2.setDone(true);
		event2.setId(6L);
		event2.setName("Event Second");

		final GetEventsResponse response = new GetEventsResponse();
		response.setEvents(Arrays.asList(event1, event2));
		return response;
	}

}

