package duck.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import duck.task.Task;

/**
 * Handles all console input and output for the chatbot.
 */
public class Ui {
    /** Horizontal divider used to separate chatbot responses. */
    private static final String SEPARATOR =
            "____________________________________________________________";

    /** Duck's startup logo. */
    private static final String BANNER = " ____             _    \n"
            + "|  _ \\ _   _  ___| | __\n"
            + "| | | | | | |/ __| |/ /\n"
            + "| |_| | |_| | (__|   < \n"
            + "|____/ \\__,_|\\___|_|\\_\\\n";

    /** Greeting shared by the console and graphical interfaces. */
    private static final String GREETING = "Hello! I'm Duck. Quack~\n"
            + "What can I do for you?";

    /** Reads commands from the console. */
    private final Scanner scanner;

    /** Destination for chatbot output. */
    private final PrintStream output;

    /** Creates a UI connected to the standard input stream. */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the supplied input and output streams.
     *
     * @param input Source of user commands.
     * @param output Destination for chatbot messages.
     */
    public Ui(InputStream input, PrintStream output) {
        this.scanner = new Scanner(Objects.requireNonNull(input));
        this.output = Objects.requireNonNull(output);
    }

    /** Shows the startup logo and greeting. */
    public void showWelcome() {
        this.output.println(SEPARATOR);
        this.output.println(BANNER);
        this.output.println(GREETING);
        this.output.println(SEPARATOR);
    }

    /**
     * Returns the greeting used at the start of a Duck session.
     *
     * @return Duck's greeting without console decoration
     */
    public static String getGreeting() {
        return GREETING;
    }

    /**
     * Returns whether another command is available from the console.
     *
     * @return true when another command can be read
     */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /**
     * Reads and normalizes the next command.
     *
     * @return next command with surrounding whitespace removed
     */
    public String readCommand() {
        return this.scanner.nextLine().trim();
    }

    /** Shows the divider between chatbot responses. */
    public void showSeparator() {
        this.output.println(SEPARATOR);
    }

    /** Shows the farewell message. The command loop prints the closing divider. */
    public void showGoodbye() {
        this.output.println("Bye. Hope to see you again soon!");
    }

    /**
     * Shows all tasks with one-based list numbers.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        this.output.println("Here are the tasks in your list:");
        showNumberedTasks(tasks);
    }

    /** Shows tasks whose descriptions match a find keyword. */
    public void showMatchingTasks(List<Task> tasks) {
        this.output.println("Here are the matching tasks in your list:");
        showNumberedTasks(tasks);
    }

    /** Shows tasks with one-based numbers relative to the supplied list. */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            this.output.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Shows confirmation that a task was marked as done.
     *
     * @param task task that was marked
     */
    public void showTaskMarked(Task task) {
        this.output.println("Nice! I've marked this task as done:");
        this.output.println("  " + task);
    }

    /**
     * Shows confirmation that a task was marked as not done.
     *
     * @param task task that was unmarked
     */
    public void showTaskUnmarked(Task task) {
        this.output.println("OK, I've marked this task as not done yet:");
        this.output.println("  " + task);
    }

    /**
     * Shows confirmation and the new task count after adding a task.
     *
     * @param task task that was added
     * @param taskCount number of tasks after the addition
     */
    public void showTaskAdded(Task task, int taskCount) {
        this.output.println("Got it. I've added this task:");
        this.output.println(task);
        this.output.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows confirmation and the new task count after deleting a task.
     *
     * @param task task that was deleted
     * @param taskCount number of tasks after the deletion
     */
    public void showTaskDeleted(Task task, int taskCount) {
        this.output.println("Noted. I've removed this task:");
        this.output.println("  " + task);
        this.output.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows an error without exposing exception-handling details to this class.
     *
     * @param message user-facing explanation of the error
     */
    public void showError(String message) {
        this.output.println("OOPS!!! " + message);
    }
}
