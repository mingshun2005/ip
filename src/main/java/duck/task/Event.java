package duck.task;

/**
 * Represents a task that starts and ends at specific dates or times.
 */
public class Event extends Task {
    /** Start and end times of this event. */
    private final String[] times;

    /**
     * Creates an event task with the given description and time range.
     *
     * @param description Description of the task.
     * @param timeRange Time range in the format start /to end.
     */
    public Event(String description, String timeRange) {
        super(description);
        this.times = timeRange.split("/to ");
    }

    /**
     * Creates an event task with separate start and end times, as stored in the data file.
     *
     * @param description Description of the task.
     * @param from Start date or time as text.
     * @param to End date or time as text.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.times = new String[] { from, to };
    }

    @Override
    public String toString() {
        return TaskType.EVENT.getTag() + super.toString() + " (from: " + this.times[0].trim() + " to: "
                + this.times[1].trim() + ")";
    }

    @Override
    public String toFileString() {
        return TaskType.EVENT.getFileCode() + " | " + this.getFileStatus() + " | "
                + this.escapeFileField(this.description) + " | " + this.escapeFileField(this.times[0].trim())
                + " | " + this.escapeFileField(this.times[1].trim());
    }
}
