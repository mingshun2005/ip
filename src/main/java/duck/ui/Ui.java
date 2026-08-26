package duck.ui;

import java.util.List;
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

    /** Reads commands from the console. */
    private final Scanner scanner;

    /** Creates a UI connected to the standard input stream. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Shows the startup logo and greeting. */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Duck. Quack~");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
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
        System.out.println(SEPARATOR);
    }

    /** Shows the farewell message. The command loop prints the closing divider. */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Shows all tasks with one-based list numbers.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /** Shows confirmation that a task was marked as done. */
    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /** Shows confirmation that a task was marked as not done. */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /** Shows confirmation and the new task count after adding a task. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println(task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Shows confirmation and the new task count after deleting a task. */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Shows an error without exposing exception-handling details to this class. */
    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
    }
}
