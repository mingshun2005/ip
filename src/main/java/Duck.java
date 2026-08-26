/**
 * A simple chatbot that greets the user and echoes commands until the user exits.
 */
public class Duck {
    /**
     * Starts the chatbot, then reads and responds to user commands.
     *
     * @param args command line arguments, currently unused
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("data/duck.txt");
        Parser parser = new Parser();
        ui.showWelcome();
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load());
        } catch (DuckException e) {
            ui.showError(e.getMessage());
            ui.showSeparator();
            tasks = new TaskList();
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showSeparator();
            try {
                CommandType commandType = parser.parseCommand(input);
                switch (commandType) {
                case BYE -> {
                    ui.showGoodbye();
                    return;
                }
                case LIST -> ui.showTaskList(tasks.asList());
                case MARK -> {
                    int taskNumber = parser.parseTaskNumber(input, commandType);
                    requireExistingTaskNumber(taskNumber, tasks);
                    setTaskDoneAndSave(tasks, taskNumber - 1, true, storage);
                    ui.showTaskMarked(tasks.get(taskNumber - 1));
                }
                case UNMARK -> {
                    int taskNumber = parser.parseTaskNumber(input, commandType);
                    requireExistingTaskNumber(taskNumber, tasks);
                    setTaskDoneAndSave(tasks, taskNumber - 1, false, storage);
                    ui.showTaskUnmarked(tasks.get(taskNumber - 1));
                }
                case TODO -> {
                    addTaskAndSave(tasks, parser.parseTodo(input), storage);
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                }
                case EVENT -> {
                    addTaskAndSave(tasks, parser.parseEvent(input), storage);
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                }
                case DEADLINE -> {
                    addTaskAndSave(tasks, parser.parseDeadline(input), storage);
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                }
                case DELETE -> {
                    int taskNumber = parser.parseTaskNumber(input, commandType);
                    requireExistingTaskNumber(taskNumber, tasks);
                    Task removedTask = deleteTaskAndSave(tasks, taskNumber - 1, storage);
                    ui.showTaskDeleted(removedTask, tasks.size());
                }
                }
            } catch (DuckException e) {
                ui.showError(e.getMessage());
            }
            ui.showSeparator();
        }
    }

    /** Rejects task numbers outside the current one-based list range. */
    private static void requireExistingTaskNumber(int taskNumber, TaskList tasks)
            throws DuckException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new DuckException("That task number does not exist.");
        }
    }

    /** Adds a task, saving it immediately and rolling it back if saving fails. */
    private static void addTaskAndSave(TaskList tasks, Task task, Storage storage)
            throws DuckException {
        tasks.add(task);
        try {
            storage.save(tasks.asList());
        } catch (DuckException e) {
            tasks.delete(tasks.size() - 1);
            throw e;
        }
    }

    /** Removes a task, restoring it if the updated list cannot be saved. */
    private static Task deleteTaskAndSave(TaskList tasks, int taskIndex, Storage storage)
            throws DuckException {
        Task removedTask = tasks.delete(taskIndex);
        try {
            storage.save(tasks.asList());
            return removedTask;
        } catch (DuckException e) {
            tasks.add(taskIndex, removedTask);
            throw e;
        }
    }

    /** Changes a task status, restoring the old status if the update cannot be saved. */
    private static void setTaskDoneAndSave(TaskList tasks, int taskIndex, boolean isDone,
            Storage storage) throws DuckException {
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        if (isDone) {
            tasks.markAsDone(taskIndex);
        } else {
            tasks.markAsUndone(taskIndex);
        }

        try {
            storage.save(tasks.asList());
        } catch (DuckException e) {
            if (wasDone) {
                tasks.markAsDone(taskIndex);
            } else {
                tasks.markAsUndone(taskIndex);
            }
            throw e;
        }
    }
}
