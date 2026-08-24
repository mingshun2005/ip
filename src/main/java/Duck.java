import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A simple chatbot that greets the user and echoes commands until the user exits.
 */
public class Duck {
    /** Location of the task data file, relative to the project root. */
    private static final Path DATA_FILE_PATH = Path.of("data", "duck.txt");

    /**
     * Starts the chatbot, then reads and responds to user commands.
     *
     * @param args command line arguments, currently unused
     */
    public static void main(String[] args) {
        String line = "____________________________________________________________";
        String banner = " ____             _    \n"
                + "|  _ \\ _   _  ___| | __\n"
                + "| | | | | | |/ __| |/ /\n"
                + "| |_| | |_| | (__|   < \n"
                + "|____/ \\__,_|\\___|_|\\_\\\n";
        System.out.println(line);
        System.out.println(banner);
        System.out.println("Hello! I'm Duck. Quack~");
        System.out.println("What can I do for you?");
        System.out.println(line);
        ArrayList<Task> tasks = new ArrayList<>(100);
        try {
            tasks.addAll(loadTasks());
        } catch (DuckException e) {
            System.out.println("OOPS!!! " + e.getMessage());
            System.out.println(line);
        }

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(line);
            try {
                if (input.equals("bye")) {
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(line);
                    break;
                } else if (input.equals("list")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i));
                    }
                } else if (input.startsWith("mark ")) {
                    int taskNumber = Integer.parseInt(input.substring(5));
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new DuckException("That task number does not exist.");
                    }
                    tasks.get(taskNumber - 1).markAsDone();
                    saveTasks(tasks);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks.get(taskNumber - 1));
                } else if (input.startsWith("unmark ")) {
                    int taskNumber = Integer.parseInt(input.substring(7));
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new DuckException("That task number does not exist.");
                    }
                    tasks.get(taskNumber - 1).markAsUndone();
                    saveTasks(tasks);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks.get(taskNumber - 1));
                } else if (input.equals("todo")) {
                    throw new DuckException("The description of a todo cannot be empty.");
                } else if (input.startsWith("todo ")) {
                    String description = input.substring(5);
                    if (description.trim().isEmpty()) {
                        throw new DuckException("The description of a todo cannot be empty.");
                    }
                    tasks.add(new Todo(description));
                    saveTasks(tasks);
                    System.out.println("Got it. I've added this task:");
                    System.out.println(tasks.get(tasks.size() - 1));
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else if (input.equals("event")) {
                    throw new DuckException("The description of an event cannot be empty.");
                } else if (input.startsWith("event ")) {
                    String[] split = input.substring(6).split(" /from ", 2);
                    if (split.length < 2 || split[0].trim().isEmpty()) {
                        throw new DuckException("The description of an event cannot be empty.");
                    }
                    if (!split[1].contains(" /to ")) {
                        throw new DuckException("The event command needs a /from and /to time.");
                    }
                    tasks.add(new Event(split[0], split[1]));
                    saveTasks(tasks);
                    System.out.println("Got it. I've added this task:");
                    System.out.println(tasks.get(tasks.size() - 1));
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else if (input.equals("deadline")) {
                    throw new DuckException("The description of a deadline cannot be empty.");
                } else if (input.startsWith("deadline ")) {
                    String[] split = input.substring(9).split(" /by ", 2);
                    if (split.length < 2 || split[0].trim().isEmpty()) {
                        throw new DuckException("The description of a deadline cannot be empty.");
                    }
                    tasks.add(new Deadline(split[0], split[1]));
                    saveTasks(tasks);
                    System.out.println("Got it. I've added this task:");
                    System.out.println(tasks.get(tasks.size() - 1));
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else if (input.equals("delete") || input.equals("delete ")) {
                    throw new DuckException("Please enter task number to delete task!");
                } else if (input.startsWith("delete ")) {
                    int taskNumber = Integer.parseInt(input.substring(7));
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new DuckException("That task number does not exist.");
                    }
                    Task removedTask = tasks.remove(taskNumber - 1);
                    saveTasks(tasks);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removedTask);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                }
                else {
                    throw new DuckException("I'm sorry, but I don't know what that means :-(");
                }
            } catch (NumberFormatException e) {
                System.out.println("OOPS!!! Please enter a valid task number.");
            } catch (DuckException e) {
                System.out.println("OOPS!!! " + e.getMessage());
            }
            System.out.println(line);
        }
    }

    /**
     * Writes the current task list to the hard disk, replacing the previous contents.
     *
     * @param tasks tasks to save
     * @throws DuckException if the task list cannot be saved
     */
    private static void saveTasks(ArrayList<Task> tasks) throws DuckException {
        ArrayList<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            taskLines.add(task.toFileString());
        }

        try {
            Files.createDirectories(DATA_FILE_PATH.getParent());
            Files.write(DATA_FILE_PATH, taskLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DuckException("Unable to save tasks to the hard disk.");
        }
    }

    /**
     * Reads saved tasks from the hard disk. A missing file represents an empty task list.
     *
     * @return tasks reconstructed from the data file
     * @throws DuckException if the data file cannot be read or contains an invalid task
     */
    private static ArrayList<Task> loadTasks() throws DuckException {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        if (!Files.exists(DATA_FILE_PATH)) {
            return loadedTasks;
        }

        try {
            for (String taskLine : Files.readAllLines(DATA_FILE_PATH, StandardCharsets.UTF_8)) {
                loadedTasks.add(parseTask(taskLine));
            }
            return loadedTasks;
        } catch (IOException e) {
            throw new DuckException("Unable to load tasks from the hard disk.");
        }
    }

    /**
     * Reconstructs one task from its plain-text storage representation.
     *
     * @param taskLine one line from the data file
     * @return reconstructed task
     * @throws DuckException if the line does not match the expected storage format
     */
    private static Task parseTask(String taskLine) throws DuckException {
        String[] parts = taskLine.split(" \\| ", -1);
        Task task;

        if (TaskType.TODO.getFileCode().equals(parts[0]) && parts.length == 3) {
            task = new Todo(parts[2]);
        } else if (TaskType.DEADLINE.getFileCode().equals(parts[0]) && parts.length == 4) {
            task = new Deadline(parts[2], parts[3]);
        } else if (TaskType.EVENT.getFileCode().equals(parts[0]) && parts.length == 5) {
            task = new Event(parts[2], parts[3], parts[4]);
        } else {
            throw new DuckException("Unable to load tasks from the hard disk.");
        }

        if ("1".equals(parts[1])) {
            task.markAsDone();
        } else if (!"0".equals(parts[1])) {
            throw new DuckException("Unable to load tasks from the hard disk.");
        }
        return task;
    }
}
