package duck;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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

    /** Whether the latest graphical-interface command requested that Duck exit. */
    private boolean isExitRequested;

    /**
     * Creates a chatbot backed by the given task file and loads its initial tasks.
     * An invalid or unreadable file produces an empty task list while retaining an
     * error message for the user.
     *
     * @param filePath Path to the task data file.
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
        this.isExitRequested = false;
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
     * Returns the greeting for a graphical session, including any task-loading error.
     *
     * @return startup message to display in Duck's first dialog box
     */
    public String getWelcomeMessage() {
        if (this.loadingErrorMessage == null) {
            return Ui.getGreeting();
        }
        return Ui.getGreeting() + "\nOOPS!!! " + this.loadingErrorMessage;
    }

    /**
     * Executes one graphical-interface command and returns all output as text.
     *
     * @param input User command to parse and execute.
     * @return response generated while handling the command
     */
    public String getResponse(String input) {
        Objects.requireNonNull(input, "User input cannot be null.");
        this.isExitRequested = false;

        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
        try (PrintStream responseOutput = new PrintStream(
                responseBuffer, true, StandardCharsets.UTF_8)) {
            Ui responseUi = new Ui(InputStream.nullInputStream(), responseOutput);
            try {
                Command command = this.parser.parse(input.strip());
                command.execute(this.tasks, responseUi, this.storage);
                this.isExitRequested = command.isExit();
            } catch (DuckException e) {
                responseUi.showError(e.getMessage());
            }
        }
        return responseBuffer.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Returns whether the latest graphical-interface command requested an exit.
     *
     * @return true when the latest command was a valid bye command
     */
    public boolean isExitRequested() {
        return this.isExitRequested;
    }

    /**
     * Starts Duck using the default task data file.
     *
     * @param args Command line arguments, currently unused.
     */
    public static void main(String[] args) {
        new Duck("data/duck.txt").run();
    }
}
