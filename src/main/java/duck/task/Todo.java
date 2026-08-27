package duck.task;

/**
 * Represents a task without any date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description Description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo in the user-visible task format.
     *
     * @return formatted todo with its type and completion status
     */
    @Override
    public String toString() {
        return TaskType.TODO.getTag() + super.toString();
    }

    /**
     * Returns this todo in the escaped format used by task storage.
     *
     * @return storage record containing the type, status, and description
     */
    @Override
    public String toFileString() {
        return TaskType.TODO.getFileCode() + " | " + this.getFileStatus() + " | "
                + this.escapeFileField(this.description);
    }
}
