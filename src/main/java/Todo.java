/**
 * Represents a task without any date or time attached to it.
 */
public class Todo extends Task {
    private String sym = "[T]";

    /**
     * Creates a todo task with the given description.
     *
     * @param des description of the task
     */
    public Todo(String des) {
        super(des);
    }

    @Override
    public String toString() {
        return sym + super.toString();
    }
}
