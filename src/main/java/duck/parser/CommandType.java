package duck.parser;

/**
 * Represents the commands understood by Duck and their command words.
 */
public enum CommandType {
    BYE("bye", false),
    LIST("list", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    TODO("todo", true),
    EVENT("event", true),
    DEADLINE("deadline", true),
    DELETE("delete", true);

    /** Word that identifies this command. */
    private final String commandWord;

    /** Whether text may follow the command word. */
    private final boolean acceptsArguments;

    /**
     * Creates a command type with its input syntax.
     *
     * @param commandWord Word that identifies the command.
     * @param acceptsArguments Whether the command accepts trailing arguments.
     */
    CommandType(String commandWord, boolean acceptsArguments) {
        this.commandWord = commandWord;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Returns the word that identifies this command.
     *
     * @return command word
     */
    public String getCommandWord() {
        return this.commandWord;
    }

    /**
     * Returns whether text may follow this command word.
     *
     * @return true when the command accepts arguments
     */
    public boolean acceptsArguments() {
        return this.acceptsArguments;
    }
}
