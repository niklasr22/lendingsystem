package data;

import java.time.LocalDate;

public interface CalendarEvent {
    LocalDate getStartDate();
    LocalDate getEndDate();
}
