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

        assertEquals("Hello! I'm Duck. Quack~\nWhat can I do for you?",
                duck.getWelcomeMessage());
    }

    @Test
    public void getResponse_validCommands_preservesTaskStateBetweenCommands() {
        Duck duck = createDuck();

        String addResponse = duck.getResponse("todo read book");
        boolean isAddResponseError = duck.isLastResponseError();
        String listResponse = duck.getResponse("list");

        assertEquals("Got it. I've added this task:\n"
                + "[T][ ] read book\n"
                + "Now you have 1 tasks in the list.", addResponse);
        assertFalse(isAddResponseError);
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book", listResponse);
        assertFalse(duck.isLastResponseError());
    }

    @Test
    public void getResponse_unknownOrEmptyCommand_returnsUserFacingError() {
        Duck duck = createDuck();
        String expectedMessage = "OOPS!!! I'm sorry, but I don't know what that means :-(";

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

        assertEquals("Bye. Hope to see you again soon!", duck.getResponse("bye"));
        assertTrue(duck.isExitRequested());
    }

    /** Creates a Duck using a fresh task file for each test. */
    private Duck createDuck() {
        return new Duck(this.temporaryDirectory.resolve("duck.txt").toString());
    }
}
