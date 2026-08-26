package duck.command;

import duck.DuckException;
import duck.storage.Storage;
import duck.task.Task;
import duck.task.TaskList;
import duck.ui.Ui;

/**
 * Provides the shared execution flow for commands that change a task's status.
 */
public abstract class TaskStatusCommand extends Command {
    /** One-based task number supplied by the user. */
    private final int taskNumber;

    /** Status to apply when this command executes. */
    private final boolean shouldMarkAsDone;

    /**
     * Creates a command that applies the requested status to one task.
     *
     * @param taskNumber one-based task number
     * @param shouldMarkAsDone true to mark the task done, or false to mark it undone
     */
    protected TaskStatusCommand(int taskNumber, boolean shouldMarkAsDone) {
        this.taskNumber = taskNumber;
        this.shouldMarkAsDone = shouldMarkAsDone;
    }

    /**
     * Changes and saves the status, restoring the original status if saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DuckException {
        if (this.taskNumber < 1 || this.taskNumber > tasks.size()) {
            throw new DuckException("That task number does not exist.");
        }

        int taskIndex = this.taskNumber - 1;
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        setTaskStatus(tasks, taskIndex, this.shouldMarkAsDone);
        try {
            storage.save(tasks.asList());
        } catch (DuckException e) {
            setTaskStatus(tasks, taskIndex, wasDone);
            throw e;
        }
        showConfirmation(ui, task);
    }

    /** Applies a status through TaskList so the collection remains the mutation owner. */
    private void setTaskStatus(TaskList tasks, int taskIndex, boolean isDone) {
        if (isDone) {
            tasks.markAsDone(taskIndex);
        } else {
            tasks.markAsUndone(taskIndex);
        }
    }

    /** Displays the confirmation specific to the concrete status command. */
    protected abstract void showConfirmation(Ui ui, Task task);
}
