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

    @Override
    public String toString() {
        return TaskType.TODO.getTag() + super.toString();
    }

    @Override
    public String toFileString() {
        return TaskType.TODO.getFileCode() + " | " + this.getFileStatus() + " | "
                + this.escapeFileField(this.description);
    }
}
