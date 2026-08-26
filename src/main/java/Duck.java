import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * A simple chatbot that greets the user and echoes commands until the user exits.
 */
public class Duck {
    /** Exact, ASCII-only shape accepted for deadline dates. */
    private static final Pattern DEADLINE_DATE_PATTERN =
            Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

    /** Strict formatter used for deadline command input. */
    private static final DateTimeFormatter DEADLINE_DATE_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE;

    /** Error shown when a command contains an invalid deadline date. */
    private static final String INVALID_COMMAND_DATE_MESSAGE =
            "Please enter a valid deadline date in yyyy-MM-dd format.";

    /**
     * Starts the chatbot, then reads and responds to user commands.
     *
     * @param args command line arguments, currently unused
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("data/duck.txt");
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
                if (input.equals("bye")) {
                    ui.showGoodbye();
                    break;
                } else if (input.equals("list")) {
                    ui.showTaskList(tasks.asList());
                } else if (input.equals("mark") || input.startsWith("mark ")) {
                    int taskNumber = parseTaskNumber(input, "mark");
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new DuckException("That task number does not exist.");
                    }
                    setTaskDoneAndSave(tasks, taskNumber - 1, true, storage);
                    ui.showTaskMarked(tasks.get(taskNumber - 1));
                } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                    int taskNumber = parseTaskNumber(input, "unmark");
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new DuckException("That task number does not exist.");
                    }
                    setTaskDoneAndSave(tasks, taskNumber - 1, false, storage);
                    ui.showTaskUnmarked(tasks.get(taskNumber - 1));
                } else if (input.equals("todo")) {
                    throw new DuckException("The description of a todo cannot be empty.");
                } else if (input.startsWith("todo ")) {
                    String description = input.substring(5).trim();
                    if (description.isEmpty()) {
                        throw new DuckException("The description of a todo cannot be empty.");
                    }
                    addTaskAndSave(tasks, new Todo(description), storage);
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
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
                    addTaskAndSave(tasks, event, storage);
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                } else if (input.equals("deadline")) {
                    throw new DuckException("The description of a deadline cannot be empty.");
                } else if (input.startsWith("deadline ")) {
                    String deadlineDetails = input.substring(9).trim();
                    if (deadlineDetails.isEmpty() || deadlineDetails.startsWith("/by ")) {
                        throw new DuckException("The description of a deadline cannot be empty.");
                    }
                    String[] descriptionAndDeadline = deadlineDetails.split(" /by ", 2);
                    if (descriptionAndDeadline.length < 2 || descriptionAndDeadline[1].trim().isEmpty()) {
                        throw new DuckException("The deadline command needs a non-empty /by date.");
                    }
                    LocalDate deadlineDate = parseDeadlineDate(descriptionAndDeadline[1].trim(),
                            INVALID_COMMAND_DATE_MESSAGE);
                    Deadline deadline = new Deadline(descriptionAndDeadline[0].trim(),
                            deadlineDate);
                    addTaskAndSave(tasks, deadline, storage);
                    ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                } else if (input.equals("delete") || input.equals("delete ")) {
                    throw new DuckException("Please enter task number to delete task!");
                } else if (input.startsWith("delete ")) {
                    int taskNumber = parseTaskNumber(input, "delete");
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new DuckException("That task number does not exist.");
                    }
                    Task removedTask = deleteTaskAndSave(tasks, taskNumber - 1, storage);
                    ui.showTaskDeleted(removedTask, tasks.size());
                }
                else {
                    throw new DuckException("I'm sorry, but I don't know what that means :-(");
                }
            } catch (DuckException e) {
                ui.showError(e.getMessage());
            }
            ui.showSeparator();
        }
    }

    /**
     * Parses a canonical deadline date. The shape check rejects abbreviated, signed,
     * extended, and non-ASCII years before strict calendar validation is attempted.
     *
     * @param dateText date in yyyy-MM-dd format
     * @param errorMessage contextual message to show when parsing fails
     * @return parsed date
     * @throws DuckException if the text is not a valid date from year 0001 to 9999
     */
    private static LocalDate parseDeadlineDate(String dateText, String errorMessage)
            throws DuckException {
        if (!DEADLINE_DATE_PATTERN.matcher(dateText).matches()) {
            throw new DuckException(errorMessage);
        }

        try {
            LocalDate date = LocalDate.parse(dateText, DEADLINE_DATE_FORMAT);
            if (date.getYear() == 0) {
                throw new DuckException(errorMessage);
            }
            return date;
        } catch (DateTimeParseException e) {
            throw new DuckException(errorMessage);
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
