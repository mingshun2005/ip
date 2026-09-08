package duck.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import duck.DuckException;
import duck.command.AddCommand;
import duck.command.DeleteCommand;
import duck.command.ExitCommand;
import duck.command.FindCommand;
import duck.command.ListCommand;
import duck.command.MarkCommand;
import duck.command.UnmarkCommand;

/**
 * Tests command recognition and argument validation performed by {@link Parser}.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parse_commandsWithoutArguments_returnsMatchingCommandTypes() throws DuckException {
        assertInstanceOf(ExitCommand.class, this.parser.parse("bye"));
        assertInstanceOf(ListCommand.class, this.parser.parse("list"));
    }

    @Test
    public void parse_taskNumberCommandsWithInteger_returnsMatchingCommandTypes() throws DuckException {
        assertInstanceOf(MarkCommand.class, this.parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, this.parser.parse("unmark 2"));
        assertInstanceOf(DeleteCommand.class, this.parser.parse("delete 3"));
    }

    @Test
    public void parse_validAddCommands_returnsAddCommands() throws DuckException {
        assertInstanceOf(AddCommand.class, this.parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("deadline return book /by 2026-08-30"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("deadline submit report /by Mon"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("deadline buy groceries /by fRi"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("event meeting /from Monday 2pm /to 4pm"));
    }

    @Test
    public void parse_findWithKeyword_returnsFindCommand() throws DuckException {
        assertInstanceOf(FindCommand.class, this.parser.parse("find book"));
        assertInstanceOf(FindCommand.class, this.parser.parse("find project meeting"));
    }

    @Test
    public void parse_findWithoutKeyword_throwsDescriptionError() {
        assertParseError("find", "The keyword for a find command cannot be empty.");
    }

    @Test
    public void parse_unknownCommandsAndPrefixCollisions_throwsUnknownCommandError() {
        String expectedMessage = "I'm sorry, but I don't know what that means :-(";

        assertParseError("", expectedMessage);
        assertParseError("unknown", expectedMessage);
        assertParseError("todoish read book", expectedMessage);
        assertParseError("marking 1", expectedMessage);
        assertParseError("finder book", expectedMessage);
        assertParseError("bye later", expectedMessage);
        assertParseError("list now", expectedMessage);
    }

    @Test
    public void parse_missingOrInvalidTaskNumbers_throwsTaskNumberError() {
        assertParseError("mark", "Please enter a valid task number.");
        assertParseError("unmark abc", "Please enter a valid task number.");
        assertParseError("delete", "Please enter task number to delete task!");
        assertParseError("delete 1 2", "Please enter a valid task number.");
        assertParseError("delete 2147483648", "Please enter a valid task number.");
    }

    @Test
    public void parse_emptyTodoDescription_throwsDescriptionError() {
        assertParseError("todo", "The description of a todo cannot be empty.");
    }

    @Test
    public void parse_invalidDeadlineSyntax_throwsRelevantError() {
        assertParseError("deadline", "The description of a deadline cannot be empty.");
        assertParseError("deadline /by 2026-08-30",
                "The description of a deadline cannot be empty.");
        assertParseError("deadline return book",
                "The deadline command needs a non-empty /by date.");
        assertParseError("deadline return book /by",
                "The deadline command needs a non-empty /by date.");
    }

    @Test
    public void parse_invalidDeadlineDates_throwsDateFormatError() {
        String expectedMessage = "Please enter a valid deadline date in yyyy-MM-dd format or as "
                + "Mon, Tue, Wed, Thu, Fri, Sat, or Sun.";

        assertParseError("deadline wrong format /by 30-08-2026", expectedMessage);
        assertParseError("deadline impossible /by 2026-02-30", expectedMessage);
        assertParseError("deadline abbreviated /by 2026-8-3", expectedMessage);
        assertParseError("deadline year zero /by 0000-01-01", expectedMessage);
        assertParseError("deadline unsupported time /by 2026-08-30 1800", expectedMessage);
        assertParseError("deadline full weekday /by Monday", expectedMessage);
        assertParseError("deadline informal weekday /by Tues", expectedMessage);
        assertParseError("deadline relative phrase /by next Mon", expectedMessage);
    }

    @Test
    public void parse_validDeadlineBoundaryDates_returnsAddCommands() throws DuckException {
        assertInstanceOf(AddCommand.class,
                this.parser.parse("deadline leap day /by 2024-02-29"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("deadline earliest /by 0001-01-01"));
        assertInstanceOf(AddCommand.class,
                this.parser.parse("deadline latest /by 9999-12-31"));
    }

    @Test
    public void parse_invalidEventSyntax_throwsRelevantError() {
        assertParseError("event", "The description of an event cannot be empty.");
        assertParseError("event /from Monday /to Tuesday",
                "The description of an event cannot be empty.");
        assertParseError("event meeting", "The event command needs a /from and /to time.");
        assertParseError("event meeting /from Monday",
                "The event command needs a /from and /to time.");
        assertParseError("event meeting /from  /to 4pm",
                "The event command needs a non-empty /from and /to time.");
        assertParseError("event meeting /from Monday /to",
                "The event command needs a /from and /to time.");
    }

    /** Verifies that parsing fails with the exact user-facing error message. */
    private void assertParseError(String input, String expectedMessage) {
        DuckException exception = assertThrows(DuckException.class, () -> this.parser.parse(input));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
