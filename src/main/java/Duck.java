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
                case MARK -> new MarkCommand(parser.parseTaskNumber(input, commandType))
                        .execute(tasks, ui, storage);
                case UNMARK -> new UnmarkCommand(parser.parseTaskNumber(input, commandType))
                        .execute(tasks, ui, storage);
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

}
