package by.kes.events.model.ui;

import java.util.ArrayList;
import java.util.List;

public class EventScheduleUI {

    private List<String> scheduledDateTimes;

    public List<String> getScheduledDateTimes() {
        if (scheduledDateTimes == null) {
            scheduledDateTimes = new ArrayList<>();
        }
        return scheduledDateTimes;
    }
}
