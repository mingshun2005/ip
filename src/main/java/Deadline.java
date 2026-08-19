/**
 * Represents a task that needs to be completed by a specific date or time.
 */
public class Deadline extends Task {
    protected String by;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param des description of the task
     * @param by deadline as text
     */
    public Deadline(String des, String by) {
        super(des);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
