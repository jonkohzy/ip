package jonk;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that takes place between a start and end date.
 */
public class Event extends Task {

    /** Formats dates for user-facing task descriptions. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy");

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event with the specified description and date range.
     *
     * @param description Description of the event.
     * @param from Start date of the event in {@code yyyy-MM-dd} format.
     * @param to End date of the event in {@code yyyy-MM-dd} format.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = LocalDate.parse(from);
        this.to = LocalDate.parse(to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }

    @Override
    public String toFileString() {
        return "E | " + super.toFileString() + " | " + encodeFileField(from.toString())
                + " | " + encodeFileField(to.toString());
    }
}
