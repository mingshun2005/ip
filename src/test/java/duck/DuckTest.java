package duck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests Duck's graphical-interface response boundary.
 */
public class DuckTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getWelcomeMessage_missingDataFile_returnsExistingGreeting() {
        Duck duck = createDuck();

        assertEquals("Quack! I'm Duck. What shall we get done today? "
                + "Try: todo read a book, list, or find book. "
                + "Type help for every command.",
                duck.getWelcomeMessage());
    }

    @Test
    public void getResponse_validCommands_preservesTaskStateBetweenCommands() {
        Duck duck = createDuck();

        String addResponse = duck.getResponse("todo read book");
        boolean isAddResponseError = duck.isLastResponseError();
        String listResponse = duck.getResponse("list");

        assertEquals("✓ Got it—this task is now under my wing:\n"
                + "[T][ ] read book\n"
                + "You now have 1 task in your pond.", addResponse);
        assertFalse(isAddResponseError);
        assertEquals("Here are the tasks in your pond:\n"
                + "1.[T][ ] read book", listResponse);
        assertFalse(duck.isLastResponseError());
    }

    @Test
    public void getResponse_unknownOrEmptyCommand_returnsUserFacingError() {
        Duck duck = createDuck();
        String expectedMessage =
                "Quack? I didn't understand that command. Try help to see what I can do.";

        assertEquals(expectedMessage, duck.getResponse("unknown"));
        assertTrue(duck.isLastResponseError());
        assertEquals(expectedMessage, duck.getResponse(""));
        assertTrue(duck.isLastResponseError());
        assertFalse(duck.isExitRequested());
    }

    @Test
    public void isLastResponseError_errorFollowedByValidCommand_tracksLatestCommand() {
        Duck duck = createDuck();

        duck.getResponse("unknown");
        assertTrue(duck.isLastResponseError());

        duck.getResponse("list");
        assertFalse(duck.isLastResponseError());
    }

    @Test
    public void getResponse_bye_returnsFarewellAndRequestsExit() {
        Duck duck = createDuck();

        assertEquals("Goodbye! Keep your ducks in a row!", duck.getResponse("bye"));
        assertTrue(duck.isExitRequested());
    }

    @Test
    public void getResponse_help_returnsCommandGuide() {
        Duck duck = createDuck();

        assertEquals("Here are the commands I can help with:\n"
                + "  todo DESCRIPTION\n"
                + "  deadline DESCRIPTION /by DATE\n"
                + "  event DESCRIPTION /from START /to END\n"
                + "  list\n"
                + "  find KEYWORD\n"
                + "  mark NUMBER\n"
                + "  unmark NUMBER\n"
                + "  delete NUMBER\n"
                + "  bye", duck.getResponse("help"));
        assertFalse(duck.isLastResponseError());
    }

    /** Creates a Duck using a fresh task file for each test. */
    private Duck createDuck() {
        return new Duck(this.temporaryDirectory.resolve("duck.txt").toString());
    }
}
