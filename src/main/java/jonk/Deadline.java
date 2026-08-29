package jonk;

/**
 * Represents a task that must be completed by a specific time.
 */
public class Deadline extends Task {

    private final String by;

    /**
     * Creates a deadline with the specified description and due time.
     *
     * @param description Description of the deadline.
     * @param by Due time of the deadline.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }

    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + encodeFileField(by);
    }
}
