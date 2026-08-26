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
                    Command command = new ExitCommand();
                    command.execute(tasks, ui, storage);
                    if (command.isExit()) {
                        return;
                    }
                }
                case LIST -> new ListCommand().execute(tasks, ui, storage);
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
                case TODO -> new AddCommand(parser.parseTodo(input))
                        .execute(tasks, ui, storage);
                case EVENT -> new AddCommand(parser.parseEvent(input))
                        .execute(tasks, ui, storage);
                case DEADLINE -> new AddCommand(parser.parseDeadline(input))
                        .execute(tasks, ui, storage);
                case DELETE -> new DeleteCommand(parser.parseTaskNumber(input, commandType))
                        .execute(tasks, ui, storage);
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
