import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that needs to be completed by a specific date.
 */
public class Deadline extends Task {
    /** Format used when showing deadline dates to users. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param des description of the task
     * @param by deadline date
     */
    public Deadline(String des, LocalDate by) {
        super(des);
        this.by = by;
    }

    @Override
    public String toString() {
        return TaskType.DEADLINE.getTag() + super.toString() + " (by: "
                + this.by.format(DISPLAY_DATE_FORMAT) + ")";
    }

    @Override
    public String toFileString() {
        return TaskType.DEADLINE.getFileCode() + " | " + this.getFileStatus() + " | "
                + this.escapeFileField(this.description) + " | " + this.by;
    }
}
