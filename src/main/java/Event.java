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

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + time[0].trim() + " to: " + time[1].trim() + ")";
    }
}
