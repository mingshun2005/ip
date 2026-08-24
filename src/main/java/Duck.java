import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
            String input = scanner.nextLine().trim();
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
                } else if (input.equals("mark") || input.startsWith("mark ")) {
                    int taskNumber = parseTaskNumber(input, "mark");
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new DuckException("That task number does not exist.");
                    }
                    setTaskDoneAndSave(tasks, taskNumber - 1, true);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks.get(taskNumber - 1));
                } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                    int taskNumber = parseTaskNumber(input, "unmark");
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new DuckException("That task number does not exist.");
                    }
                    setTaskDoneAndSave(tasks, taskNumber - 1, false);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks.get(taskNumber - 1));
                } else if (input.equals("todo")) {
                    throw new DuckException("The description of a todo cannot be empty.");
                } else if (input.startsWith("todo ")) {
                    String description = input.substring(5).trim();
                    if (description.isEmpty()) {
                        throw new DuckException("The description of a todo cannot be empty.");
                    }
                    addTaskAndSave(tasks, new Todo(description));
                    System.out.println("Got it. I've added this task:");
                    System.out.println(tasks.get(tasks.size() - 1));
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else if (input.equals("event")) {
                    throw new DuckException("The description of an event cannot be empty.");
                } else if (input.startsWith("event ")) {
                    String eventDetails = input.substring(6).trim();
                    if (eventDetails.isEmpty() || eventDetails.startsWith("/from ")) {
                        throw new DuckException("The description of an event cannot be empty.");
                    }
                    String[] descriptionAndTimes = eventDetails.split(" /from ", 2);
                    if (descriptionAndTimes.length < 2) {
                        throw new DuckException("The event command needs a /from and /to time.");
                    }
                    String[] times = descriptionAndTimes[1].split(" /to ", 2);
                    if (times.length < 2) {
                        throw new DuckException("The event command needs a /from and /to time.");
                    }
                    if (times[0].trim().isEmpty() || times[1].trim().isEmpty()) {
                        throw new DuckException("The event command needs a non-empty /from and /to time.");
                    }
                    Event event = new Event(descriptionAndTimes[0].trim(), times[0].trim(), times[1].trim());
                    addTaskAndSave(tasks, event);
                    System.out.println("Got it. I've added this task:");
                    System.out.println(tasks.get(tasks.size() - 1));
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else if (input.equals("deadline")) {
                    throw new DuckException("The description of a deadline cannot be empty.");
                } else if (input.startsWith("deadline ")) {
                    String deadlineDetails = input.substring(9).trim();
                    if (deadlineDetails.isEmpty() || deadlineDetails.startsWith("/by ")) {
                        throw new DuckException("The description of a deadline cannot be empty.");
                    }
                    String[] descriptionAndDeadline = deadlineDetails.split(" /by ", 2);
                    if (descriptionAndDeadline.length < 2 || descriptionAndDeadline[1].trim().isEmpty()) {
                        throw new DuckException("The deadline command needs a non-empty /by date or time.");
                    }
                    Deadline deadline = new Deadline(descriptionAndDeadline[0].trim(),
                            descriptionAndDeadline[1].trim());
                    addTaskAndSave(tasks, deadline);
                    System.out.println("Got it. I've added this task:");
                    System.out.println(tasks.get(tasks.size() - 1));
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else if (input.equals("delete") || input.equals("delete ")) {
                    throw new DuckException("Please enter task number to delete task!");
                } else if (input.startsWith("delete ")) {
                    int taskNumber = parseTaskNumber(input, "delete");
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new DuckException("That task number does not exist.");
                    }
                    Task removedTask = deleteTaskAndSave(tasks, taskNumber - 1);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + removedTask);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                }
                else {
                    throw new DuckException("I'm sorry, but I don't know what that means :-(");
                }
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

        Path temporaryFile = null;
        try {
            Files.createDirectories(DATA_FILE_PATH.getParent());
            temporaryFile = Files.createTempFile(DATA_FILE_PATH.getParent(), "duck-", ".tmp");
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, DATA_FILE_PATH, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, DATA_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException e) {
            throw new DuckException("Unable to save tasks to data/duck.txt.");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException ignored) {
                    // The original save error is more useful than a temporary-file cleanup error.
                }
            }
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
        if (Files.notExists(DATA_FILE_PATH)) {
            return loadedTasks;
        }

        try {
            ArrayList<String> taskLines = new ArrayList<>(
                    Files.readAllLines(DATA_FILE_PATH, StandardCharsets.UTF_8));
            for (int i = 0; i < taskLines.size(); i++) {
                String taskLine = taskLines.get(i);
                if (taskLine.isBlank()) {
                    continue;
                }
                try {
                    loadedTasks.add(parseTask(taskLine));
                } catch (DuckException e) {
                    throw new DuckException("Unable to load tasks from line " + (i + 1) + ": " + e.getMessage());
                }
            }
            return loadedTasks;
        } catch (IOException | SecurityException e) {
            throw new DuckException("Unable to read tasks from data/duck.txt.");
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
        ArrayList<String> parts = splitFileFields(taskLine);
        if (parts.size() < 3) {
            throw new DuckException("the record has too few fields.");
        }

        String taskType = parts.get(0);
        String status = parts.get(1);
        if (!"0".equals(status) && !"1".equals(status)) {
            throw new DuckException("the status must be 0 or 1.");
        }

        Task task;
        if (TaskType.TODO.getFileCode().equals(taskType) && parts.size() == 3) {
            requireNonBlank(parts.get(2), "todo description");
            task = new Todo(parts.get(2));
        } else if (TaskType.DEADLINE.getFileCode().equals(taskType) && parts.size() == 4) {
            requireNonBlank(parts.get(2), "deadline description");
            requireNonBlank(parts.get(3), "deadline date or time");
            task = new Deadline(parts.get(2), parts.get(3));
        } else if (TaskType.EVENT.getFileCode().equals(taskType) && parts.size() == 5) {
            requireNonBlank(parts.get(2), "event description");
            requireNonBlank(parts.get(3), "event start time");
            requireNonBlank(parts.get(4), "event end time");
            task = new Event(parts.get(2), parts.get(3), parts.get(4));
        } else if (!TaskType.TODO.getFileCode().equals(taskType)
                && !TaskType.DEADLINE.getFileCode().equals(taskType)
                && !TaskType.EVENT.getFileCode().equals(taskType)) {
            throw new DuckException("the task type is not recognized.");
        } else {
            throw new DuckException("the task type has the wrong number of fields.");
        }

        if ("1".equals(status)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits a stored line while decoding escaped backslashes and pipe characters.
     *
     * @param taskLine line to split
     * @return decoded storage fields
     */
    private static ArrayList<String> splitFileFields(String taskLine) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < taskLine.length(); i++) {
            char currentCharacter = taskLine.charAt(i);
            if (currentCharacter == '\\' && i + 1 < taskLine.length()) {
                char escapedCharacter = taskLine.charAt(i + 1);
                if (escapedCharacter == '\\' || escapedCharacter == '|') {
                    currentField.append(escapedCharacter);
                    i++;
                    continue;
                }
            }
            if (taskLine.startsWith(" | ", i)) {
                fields.add(currentField.toString());
                currentField.setLength(0);
                i += 2;
            } else {
                currentField.append(currentCharacter);
            }
        }
        fields.add(currentField.toString());
        return fields;
    }

    /**
     * Rejects required storage fields that contain no visible text.
     *
     * @param value field value
     * @param fieldName user-facing name of the field
     * @throws DuckException if the value is blank
     */
    private static void requireNonBlank(String value, String fieldName) throws DuckException {
        if (value.isBlank()) {
            throw new DuckException("the " + fieldName + " cannot be empty.");
        }
    }

    /**
     * Parses a one-based task number following a command word.
     *
     * @param input full command input
     * @param command command word to remove
     * @return parsed task number
     * @throws DuckException if no valid integer is provided
     */
    private static int parseTaskNumber(String input, String command) throws DuckException {
        String taskNumberText = input.substring(command.length()).trim();
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new DuckException("Please enter a valid task number.");
        }
    }

    /** Adds a task, saving it immediately and rolling it back if saving fails. */
    private static void addTaskAndSave(ArrayList<Task> tasks, Task task) throws DuckException {
        tasks.add(task);
        try {
            saveTasks(tasks);
        } catch (DuckException e) {
            tasks.remove(tasks.size() - 1);
            throw e;
        }
    }

    /** Removes a task, restoring it if the updated list cannot be saved. */
    private static Task deleteTaskAndSave(ArrayList<Task> tasks, int taskIndex) throws DuckException {
        Task removedTask = tasks.remove(taskIndex);
        try {
            saveTasks(tasks);
            return removedTask;
        } catch (DuckException e) {
            tasks.add(taskIndex, removedTask);
            throw e;
        }
    }

    /** Changes a task status, restoring the old status if the update cannot be saved. */
    private static void setTaskDoneAndSave(ArrayList<Task> tasks, int taskIndex, boolean isDone)
            throws DuckException {
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }

        try {
            saveTasks(tasks);
        } catch (DuckException e) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsUndone();
            }
            throw e;
        }
    }
}
