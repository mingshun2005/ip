package duck.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Owns the chatbot's task collection and provides operations for accessing and
 * changing its contents.
 */
public class TaskList {
    /** Tasks in their user-visible order. */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>(100);
    }

    /**
     * Creates a task list containing the supplied tasks in the same order.
     * A defensive copy prevents callers from changing the collection directly.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(Objects.requireNonNull(tasks,
                "Initial task list cannot be null."));
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return task at the index
     */
    public Task get(int index) {
        return this.tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        this.tasks.add(Objects.requireNonNull(task, "Task cannot be null."));
    }

    /**
     * Restores a task at a specific position, preserving the original order.
     *
     * @param index Zero-based insertion index.
     * @param task Task to restore.
     */
    public void add(int index, Task task) {
        this.tasks.add(index, Objects.requireNonNull(task, "Task cannot be null."));
    }

    /**
     * Deletes and returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return deleted task
     */
    public Task delete(int index) {
        return this.tasks.remove(index);
    }

    /**
     * Marks the task at a zero-based index as done.
     *
     * @param index Zero-based task index.
     */
    public void markAsDone(int index) {
        this.tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at a zero-based index as not done.
     *
     * @param index Zero-based task index.
     */
    public void markAsUndone(int index) {
        this.tasks.get(index).markAsUndone();
    }

    /**
     * Returns an immutable snapshot for display or persistence.
     *
     * @return tasks in their current order
     */
    public List<Task> asList() {
        return List.copyOf(this.tasks);
    }
}
