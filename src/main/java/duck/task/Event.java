package duck.task;

/**
 * Represents a task that starts and ends at specific dates or times.
 */
public class Event extends Task {
    /** Start date or time of this event. */
    private final String startTime;

    /** End date or time of this event. */
    private final String endTime;

    /**
     * Creates an event task with the given description and time range.
     *
     * @param description Description of the task.
     * @param timeRange Time range in the format start /to end.
     */
    public Event(String description, String timeRange) {
        super(description);
        String[] parsedTimes = timeRange.split("/to ");
        this.startTime = parsedTimes[0];
        this.endTime = parsedTimes[1];
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
        this.startTime = from;
        this.endTime = to;
    }

    /**
     * Returns this event with its start and end times in the user-visible format.
     *
     * @return formatted event with its type, status, description, and time range
     */
    @Override
    public String toString() {
        return TaskType.EVENT.getTag() + super.toString() + " (from: " + this.startTime.trim()
                + " to: " + this.endTime.trim() + ")";
    }

    /**
     * Returns this event with its description and time fields escaped for storage.
     *
     * @return storage record containing the type, status, description, and time range
     */
    @Override
    public String toFileString() {
        return TaskType.EVENT.getFileCode() + " | " + this.getFileStatus() + " | "
                + this.escapeFileField(this.description) + " | "
                + this.escapeFileField(this.startTime.trim()) + " | "
                + this.escapeFileField(this.endTime.trim());
    }
}
