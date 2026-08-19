/**
 * Represents an error caused by invalid user input in Duck.
 */
public class DuckException extends Exception {
    /**
     * Creates a Duck-specific exception with the given error message.
     *
     * @param message explanation of the error
     */
    public DuckException(String message) {
        super(message);
    }
}
