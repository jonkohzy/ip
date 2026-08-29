package jonk;

/**
 * Represents a task that takes place between a start and end time.
 */
public class Event extends Task {

    private final String from;
    private final String to;

    /**
     * Creates an event with the specified description and time range.
     *
     * @param description Description of the event.
     * @param from Start time of the event.
     * @param to End time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String toFileString() {
        return "E | " + super.toFileString() + " | " + encodeFileField(from)
                + " | " + encodeFileField(to);
    }
}
