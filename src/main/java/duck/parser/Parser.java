package duck.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

import duck.DuckException;
import duck.command.AddCommand;
import duck.command.Command;
import duck.command.DeleteCommand;
import duck.command.ExitCommand;
import duck.command.FindCommand;
import duck.command.HelpCommand;
import duck.command.ListCommand;
import duck.command.MarkCommand;
import duck.command.UnmarkCommand;
import duck.task.Deadline;
import duck.task.DeadlineDateParser;
import duck.task.Event;
import duck.task.Task;
import duck.task.Todo;

/**
 * Recognizes user commands and converts their arguments into domain values.
 */
public class Parser {
    /** Error shown when a command contains an invalid deadline date. */
    private static final String INVALID_COMMAND_DATE_MESSAGE =
            "Please enter a valid deadline date in yyyy-MM-dd format or as "
                    + "Mon, Tue, Wed, Thu, Fri, Sat, or Sun.";

    /** Example that demonstrates the required todo command syntax. */
    private static final String TODO_COMMAND_EXAMPLE = " Try: todo read a book.";

    /** Example that demonstrates the required deadline command syntax. */
    private static final String DEADLINE_COMMAND_EXAMPLE =
            " Try: deadline submit report /by 2026-10-15.";

    /** Example that demonstrates the comparable event date-time format. */
    private static final String EVENT_RANGE_EXAMPLE =
            " Try: event meeting /from 2026-10-15 1400 /to 2026-10-15 1500.";

    /** Error shown when structured event endpoints contain an invalid date-time. */
    private static final String INVALID_EVENT_DATE_TIME_MESSAGE =
            "Please enter valid event times in yyyy-MM-dd HHmm format."
                    + EVENT_RANGE_EXAMPLE;

    /** Strict formatter for event endpoints that can be ordered reliably. */
    private static final DateTimeFormatter EVENT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Shape required before free-form event endpoints are parsed as date-times. */
    private static final Pattern STRUCTURED_EVENT_TIME_PATTERN =
            Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{4}");

    /** Recovery guidance for commands that require a task number. */
    private static final String TASK_NUMBER_GUIDANCE =
            " Use list to check the available task numbers.";

    /**
     * Creates a command parser.
     */
    public Parser() {
    }

    /**
     * Parses user input into the concrete command that should handle it.
     *
     * @param input Normalized user input.
     * @return executable command containing its parsed arguments
     * @throws DuckException if the command or any of its arguments is invalid
     */
    public Command parse(String input) throws DuckException {
        CommandType commandType = parseCommand(input);
        return switch (commandType) {
            case BYE -> new ExitCommand();
            case LIST -> new ListCommand();
            case HELP -> new HelpCommand();
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
     * @param input Normalized user input.
     * @return recognized command type
     * @throws DuckException if the command word is not recognized
     */
    private CommandType parseCommand(String input) throws DuckException {
        for (CommandType commandType : CommandType.values()) {
            String commandWord = commandType.getCommandWord();
            if (input.equals(commandWord)
                    || (commandType.canAcceptArguments() && input.startsWith(commandWord + " "))) {
                return commandType;
            }
        }
        throw new DuckException(
                "I didn't understand that command. Try help to see what I can do.");
    }

    /**
     * Parses a one-based task number following a mark, unmark, or delete command.
     *
     * @param input Full command input.
     * @param commandType Recognized command type.
     * @return parsed task number
     * @throws DuckException if the task number is missing or not an integer
     */
    private int parseTaskNumber(String input, CommandType commandType) throws DuckException {
        String taskNumberText = extractArguments(input, commandType);
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new DuckException("Please enter a valid task number."
                    + TASK_NUMBER_GUIDANCE);
        }
    }

    /**
     * Parses a todo command into a task.
     *
     * @param input Full todo command.
     * @return parsed todo task
     * @throws DuckException if the description is empty
     */
    private Task parseTodo(String input) throws DuckException {
        String description = extractArguments(input, CommandType.TODO);
        if (description.isEmpty()) {
            throw new DuckException("The description of a todo cannot be empty."
                    + TODO_COMMAND_EXAMPLE);
        }
        return new Todo(description);
    }

    /**
     * Parses an event command into a task.
     *
     * @param input Full event command.
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
        String startTime = times[0].trim();
        String endTime = times[1].trim();
        validateEventRange(startTime, endTime);
        return new Event(descriptionAndTimes[0].trim(), startTime, endTime);
    }

    /**
     * Parses a deadline command into a task.
     *
     * @param input Full deadline command.
     * @return parsed deadline task
     * @throws DuckException if its description or date is invalid
     */
    private Task parseDeadline(String input) throws DuckException {
        String deadlineDetails = extractArguments(input, CommandType.DEADLINE);
        if (deadlineDetails.isEmpty() || deadlineDetails.startsWith("/by ")) {
            throw new DuckException("The description of a deadline cannot be empty."
                    + DEADLINE_COMMAND_EXAMPLE);
        }

        String[] descriptionAndDeadline = deadlineDetails.split(" /by ", 2);
        if (descriptionAndDeadline.length < 2 || descriptionAndDeadline[1].trim().isEmpty()) {
            throw new DuckException("The deadline command needs a non-empty /by date."
                    + DEADLINE_COMMAND_EXAMPLE);
        }
        LocalDate deadlineDate = parseDeadlineDate(descriptionAndDeadline[1].trim());
        return new Deadline(descriptionAndDeadline[0].trim(), deadlineDate);
    }

    /** Returns the normalized text following a command word. */
    private String extractArguments(String input, CommandType commandType) {
        String commandWord = commandType.getCommandWord();
        assert input.equals(commandWord) || input.startsWith(commandWord + " ")
                : "Input must begin with the expected command word.";

        return input.substring(commandWord.length()).trim();
    }

    /** Parses a deadline date, translating validation failures into a command error. */
    private LocalDate parseDeadlineDate(String dateText) throws DuckException {
        try {
            return DeadlineDateParser.parseCommandDate(dateText, LocalDate.now());
        } catch (DateTimeParseException e) {
            throw new DuckException(INVALID_COMMAND_DATE_MESSAGE);
        }
    }

    /**
     * Rejects invalid or reversed event endpoints when both use the structured format.
     * Other endpoint text remains free-form and is not compared.
     */
    private void validateEventRange(String startText, String endText) throws DuckException {
        if (!STRUCTURED_EVENT_TIME_PATTERN.matcher(startText).matches()
                || !STRUCTURED_EVENT_TIME_PATTERN.matcher(endText).matches()) {
            return;
        }

        LocalDateTime startDateTime = parseEventDateTime(startText);
        LocalDateTime endDateTime = parseEventDateTime(endText);
        if (endDateTime.isBefore(startDateTime)) {
            throw new DuckException("The event end cannot be earlier than its start."
                    + EVENT_RANGE_EXAMPLE);
        }
    }

    /** Parses one structured event endpoint into a date-time. */
    private LocalDateTime parseEventDateTime(String dateTimeText) throws DuckException {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeText, EVENT_DATE_TIME_FORMAT);
            if (dateTime.getYear() < 1) {
                throw new DuckException(INVALID_EVENT_DATE_TIME_MESSAGE);
            }
            return dateTime;
        } catch (DateTimeParseException e) {
            throw new DuckException(INVALID_EVENT_DATE_TIME_MESSAGE);
        }
    }
}
