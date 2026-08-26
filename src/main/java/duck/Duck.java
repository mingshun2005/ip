package duck;

import duck.command.Command;
import duck.parser.Parser;
import duck.storage.Storage;
import duck.task.TaskList;
import duck.ui.Ui;

/**
 * Coordinates Duck's user interface, task list, command parser, and storage.
 */
public class Duck {
    /** User interface used for console input and output. */
    private final Ui ui;

    /** Storage used to load and persist tasks. */
    private final Storage storage;

    /** Parser used to convert user input into commands. */
    private final Parser parser;

    /** Task collection owned by this Duck session. */
    private final TaskList tasks;

    /** Loading error deferred until after the welcome message, or null if loading succeeded. */
    private final String loadingErrorMessage;

    /**
     * Creates a chatbot backed by the given task file and loads its initial tasks.
     * An invalid or unreadable file produces an empty task list while retaining an
     * error message for the user.
     *
     * @param filePath path to the task data file
     */
    public Duck(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.parser = new Parser();

        TaskList loadedTasks;
        String loadErrorMessage = null;
        try {
            loadedTasks = new TaskList(this.storage.load());
        } catch (DuckException e) {
            loadedTasks = new TaskList();
            loadErrorMessage = e.getMessage();
        }
        this.tasks = loadedTasks;
        this.loadingErrorMessage = loadErrorMessage;
    }

    /**
     * Shows the greeting, reports any loading error, and executes commands until exit.
     */
    public void run() {
        this.ui.showWelcome();
        if (this.loadingErrorMessage != null) {
            this.ui.showError(this.loadingErrorMessage);
            this.ui.showSeparator();
        }

        boolean isExit = false;
        while (!isExit && this.ui.hasNextCommand()) {
            String input = this.ui.readCommand();
            this.ui.showSeparator();
            try {
                Command command = this.parser.parse(input);
                command.execute(this.tasks, this.ui, this.storage);
                isExit = command.isExit();
            } catch (DuckException e) {
                this.ui.showError(e.getMessage());
            } finally {
                this.ui.showSeparator();
            }
        }
    }

    /**
     * Starts Duck using the default task data file.
     *
     * @param args command line arguments, currently unused
     */
    public static void main(String[] args) {
        new Duck("data/duck.txt").run();
    }
}
