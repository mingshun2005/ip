package duck;

import javafx.application.Application;

/**
 * Starts the JavaFX application without extending {@link Application} directly.
 */
public class Launcher {

    /**
     * Launches Duck's JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
