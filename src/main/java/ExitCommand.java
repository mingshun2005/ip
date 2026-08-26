/**
 * Ends the current Duck session after displaying the farewell message.
 */
public class ExitCommand extends Command {
    /** Displays Duck's farewell response. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** Indicates that the command loop should stop after this command. */
    @Override
    public boolean isExit() {
        return true;
    }
}
