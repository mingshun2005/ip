package duck.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a task that needs to be completed by a specific date.
 */
public class Deadline extends Task {
    /** Format used when showing deadline dates to users. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Canonical format used for dates stored on disk. */
    private static final DateTimeFormatter STORAGE_DATE_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE;

    /** Date by which this task must be completed. */
    private final LocalDate by;

    /**
     * Creates a deadline task with the given description and deadline.
     *
     * @param description Description of the task.
     * @param by Deadline date.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = Objects.requireNonNull(by, "Deadline date cannot be null.");
        if (by.getYear() < 1 || by.getYear() > 9999) {
            throw new IllegalArgumentException("Deadline year must be between 1 and 9999.");
        }
    }

    @Override
    public String toString() {
        return TaskType.DEADLINE.getTag() + super.toString() + " (by: "
                + this.by.format(DISPLAY_DATE_FORMAT) + ")";
    }

    @Override
    public String toFileString() {
        return TaskType.DEADLINE.getFileCode() + " | " + this.getFileStatus() + " | "
                + this.escapeFileField(this.description) + " | "
                + this.by.format(STORAGE_DATE_FORMAT);
    }
}
