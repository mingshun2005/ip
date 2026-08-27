package duck.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import duck.DuckException;
import duck.command.AddCommand;
import duck.command.Command;
import duck.command.DeleteCommand;
import duck.command.ExitCommand;
import duck.command.FindCommand;
import duck.command.ListCommand;
import duck.command.MarkCommand;
import duck.command.UnmarkCommand;
import duck.task.Deadline;
import duck.task.Event;
import duck.task.Task;
import duck.task.Todo;

/**
 * Recognizes user commands and converts their arguments into domain values.
 */
public class Parser {
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
     * Parses user input into the concrete command that should handle it.
     *
     * @param input normalized user input
     * @return executable command containing its parsed arguments
     * @throws DuckException if the command or any of its arguments is invalid
     */
    public Command parse(String input) throws DuckException {
        CommandType commandType = parseCommand(input);
        return switch (commandType) {
            case BYE -> new ExitCommand();
            case LIST -> new ListCommand();
            case FIND -> new FindCommand(parseFindKeyword(input));
            case MARK -> new MarkCommand(parseTaskNumber(input, commandType));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(input, commandType));
            case TODO -> new AddCommand(parseTodo(input));
            case EVENT -> new AddCommand(parseEvent(input));
            case DEADLINE -> new AddCommand(parseDeadline(input));
            case DELETE -> new DeleteCommand(parseTaskNumber(input, commandType));
        };
    }

    /**
     * Extracts and validates the keyword from a find command.
     *
     * @param input Full find command.
     * @return Non-blank keyword to search for.
     * @throws DuckException If the keyword is empty.
     */
    private String parseFindKeyword(String input) throws DuckException {
        String keyword = extractArguments(input, CommandType.FIND);
        if (keyword.isEmpty()) {
            throw new DuckException("The keyword for a find command cannot be empty.");
        }
        return keyword;
    }

    /**
     * Identifies the command represented by the input.
     *
     * @param input normalized user input
     * @return recognized command type
     * @throws DuckException if the command word is not recognized
     */
    private CommandType parseCommand(String input) throws DuckException {
        for (CommandType commandType : CommandType.values()) {
            String commandWord = commandType.getCommandWord();
            if (input.equals(commandWord)
                    || (commandType.acceptsArguments() && input.startsWith(commandWord + " "))) {
                return commandType;
            }
        }
        throw new DuckException("I'm sorry, but I don't know what that means :-(");
    }

    /**
     * Parses a one-based task number following a mark, unmark, or delete command.
     *
     * @param input full command input
     * @param commandType recognized command type
     * @return parsed task number
     * @throws DuckException if the task number is missing or not an integer
     */
    private int parseTaskNumber(String input, CommandType commandType) throws DuckException {
        String taskNumberText = extractArguments(input, commandType);
        if (commandType == CommandType.DELETE && taskNumberText.isEmpty()) {
            throw new DuckException("Please enter task number to delete task!");
        }
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new DuckException("Please enter a valid task number.");
        }
    }

    /**
     * Parses a todo command into a task.
     *
     * @param input full todo command
     * @return parsed todo task
     * @throws DuckException if the description is empty
     */
    private Task parseTodo(String input) throws DuckException {
        String description = extractArguments(input, CommandType.TODO);
        if (description.isEmpty()) {
            throw new DuckException("The description of a todo cannot be empty.");
        }
        return new Todo(description);
    }

    /**
     * Parses an event command into a task.
     *
     * @param input full event command
     * @return parsed event task
     * @throws DuckException if its description or time fields are invalid
     */
    private Task parseEvent(String input) throws DuckException {
        String eventDetails = extractArguments(input, CommandType.EVENT);
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
        return new Event(descriptionAndTimes[0].trim(), times[0].trim(), times[1].trim());
    }

    /**
     * Parses a deadline command into a task.
     *
     * @param input full deadline command
     * @return parsed deadline task
     * @throws DuckException if its description or date is invalid
     */
    private Task parseDeadline(String input) throws DuckException {
        String deadlineDetails = extractArguments(input, CommandType.DEADLINE);
        if (deadlineDetails.isEmpty() || deadlineDetails.startsWith("/by ")) {
            throw new DuckException("The description of a deadline cannot be empty.");
        }

        String[] descriptionAndDeadline = deadlineDetails.split(" /by ", 2);
        if (descriptionAndDeadline.length < 2 || descriptionAndDeadline[1].trim().isEmpty()) {
            throw new DuckException("The deadline command needs a non-empty /by date.");
        }
        LocalDate deadlineDate = parseDeadlineDate(descriptionAndDeadline[1].trim());
        return new Deadline(descriptionAndDeadline[0].trim(), deadlineDate);
    }

    /** Returns the normalized text following a command word. */
    private String extractArguments(String input, CommandType commandType) {
        return input.substring(commandType.getCommandWord().length()).trim();
    }

    /**
     * Parses a canonical deadline date. The shape check rejects abbreviated, signed,
     * extended, and non-ASCII years before strict calendar validation is attempted.
     *
     * @param dateText date in yyyy-MM-dd format
     * @return parsed date
     * @throws DuckException if the text is not a valid date from year 0001 to 9999
     */
    private LocalDate parseDeadlineDate(String dateText) throws DuckException {
        if (!DEADLINE_DATE_PATTERN.matcher(dateText).matches()) {
            throw new DuckException(INVALID_COMMAND_DATE_MESSAGE);
        }

        try {
            LocalDate date = LocalDate.parse(dateText, DEADLINE_DATE_FORMAT);
            if (date.getYear() == 0) {
                throw new DuckException(INVALID_COMMAND_DATE_MESSAGE);
            }
            return date;
        } catch (DateTimeParseException e) {
            throw new DuckException(INVALID_COMMAND_DATE_MESSAGE);
        }
    }
}
