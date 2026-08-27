package duck.task;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents a task in the chatbot's task list.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon that shows whether this task is done.
     *
     * @return "X" if this task is done, or a space otherwise
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if this task is done
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns whether this task's description contains a keyword, ignoring case.
     *
     * @param keyword Non-blank keyword to find.
     * @return True if the description contains the keyword.
     */
    public boolean hasKeyword(String keyword) {
        Objects.requireNonNull(keyword, "Find keyword cannot be null.");
        if (keyword.isBlank()) {
            throw new IllegalArgumentException("Find keyword cannot be blank.");
        }
        return this.description.toLowerCase(Locale.ROOT)
                .contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns the status value used in the save file.
     *
     * @return "1" if this task is done, or "0" otherwise
     */
    protected String getFileStatus() {
        return this.isDone ? "1" : "0";
    }

    /**
     * Escapes characters that have special meaning in the storage format.
     *
     * @param field task text to store
     * @return escaped text that can be parsed without losing characters
     */
    protected String escapeFileField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsUndone() {
        this.isDone = false;
    }

    /**
     * Returns this task in the display format used by the chatbot.
     *
     * @return formatted task string
     */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }

    /**
     * Returns this task in the plain-text format used for storage.
     *
     * @return save file representation of this task
     */
    public abstract String toFileString();
}
