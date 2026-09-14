package duck.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import duck.task.Task;
import duck.task.Todo;

/**
 * Tests Duck's user-facing personality messages.
 */
public class UiTest {
    private ByteArrayOutputStream outputBuffer;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        this.outputBuffer = new ByteArrayOutputStream();
        this.ui = new Ui(
                new ByteArrayInputStream(new byte[0]),
                new PrintStream(this.outputBuffer, true, StandardCharsets.UTF_8));
    }

    @Test
    public void showTaskList_emptyList_describesClearPond() {
        this.ui.showTaskList(List.of());

        assertEquals("The pond is clear—there are no tasks yet.", getOutput());
    }

    @Test
    public void showMatchingTasks_emptyList_describesNoMatches() {
        this.ui.showMatchingTasks(List.of());

        assertEquals("No matching tasks surfaced in the pond.", getOutput());
    }

    @Test
    public void showTaskAdded_oneTask_usesSingularTaskCount() {
        Task task = new Todo("read book");

        this.ui.showTaskAdded(task, 1);

        assertEquals("✓ Got it—this task is now under my wing:\n"
                + "[T][ ] read book\n"
                + "You now have 1 task in your pond.", getOutput());
    }

    @Test
    public void showTaskDeleted_multipleTasks_usesPluralTaskCount() {
        Task task = new Todo("read book");

        this.ui.showTaskDeleted(task, 2);

        assertEquals("✓ Removed! That task has left the pond:\n"
                + "  [T][ ] read book\n"
                + "You now have 2 tasks in your pond.", getOutput());
    }

    /** Returns captured output without its final platform line separator. */
    private String getOutput() {
        return this.outputBuffer.toString(StandardCharsets.UTF_8).stripTrailing();
    }
}
