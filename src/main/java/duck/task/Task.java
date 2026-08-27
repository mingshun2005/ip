package duck.task;

/**
 * Represents a task in the chatbot's task list.
 */
public abstract class Task {
    /** Description shown to the user and stored on disk. */
    protected String description;

    /** Whether the task has been completed. */
    protected boolean isDone;

    /**
     * Creates a task with the given description.
     *
     * @param description Description of the task.
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
        return this.isDone ? "X" : " ";
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
     * @param field Task text to store.
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
