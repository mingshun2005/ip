package duck.task;

/**
 * Represents a task that starts and ends at specific dates or times.
 */
public class Event extends Task {

    private String[] time;

    /**
     * Creates an event task with the given description and time range.
     *
     * @param des description of the task
     * @param time time range in the format start /to end
     */
    public Event(String des, String time) {
        super(des);
        this.time = time.split("/to ");
    }

    /**
     * Creates an event task with separate start and end times, as stored in the data file.
     *
     * @param des description of the task
     * @param from start date or time as text
     * @param to end date or time as text
     */
    public Event(String des, String from, String to) {
        super(des);
        this.time = new String[] { from, to };
    }

    /**
     * Returns this event with its start and end times in the user-visible format.
     *
     * @return formatted event with its type, status, description, and time range
     */
    @Override
    public String toString() {
        return TaskType.EVENT.getTag() + super.toString() + " (from: " + time[0].trim() + " to: "
                + time[1].trim() + ")";
    }

    /**
     * Returns this event with its description and time fields escaped for storage.
     *
     * @return storage record containing the type, status, description, and time range
     */
    @Override
    public String toFileString() {
        return TaskType.EVENT.getFileCode() + " | " + this.getFileStatus() + " | "
                + this.escapeFileField(this.description) + " | " + this.escapeFileField(this.time[0].trim())
                + " | " + this.escapeFileField(this.time[1].trim());
    }
}
