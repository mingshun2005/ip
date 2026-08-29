package duck.ui;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Displays one wrapped message and an optional avatar aligned according to its speaker.
 */
public class DialogBox extends HBox {
    /** Text shown in this dialog box. */
    @FXML
    private Label text;

    /** Avatar shown beside the message. */
    @FXML
    private ImageView displayPicture;

    /** Creates a dialog box from FXML containing the supplied message and avatar. */
    private DialogBox(String message, Image image) {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                DialogBox.class.getResource("/view/DialogBox.fxml"),
                "Dialog box FXML resource is missing."));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box layout.", e);
        }

        this.text.setText(message);
        this.displayPicture.setImage(image);
    }

    /**
     * Creates a right-aligned dialog box for a user command.
     *
     * @param message User command to display.
     * @param image User avatar to display.
     * @return right-aligned user dialog box
     */
    public static DialogBox getUserDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.text.getStyleClass().add("user-bubble");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog box for a Duck response.
     *
     * @param message Duck response to display.
     * @param image Duck avatar to display.
     * @return left-aligned Duck dialog box
     */
    public static DialogBox getDuckDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.setAlignment(Pos.TOP_LEFT);
        dialogBox.getStyleClass().add("duck-dialog");
        dialogBox.text.getStyleClass().add("duck-bubble");
        dialogBox.getChildren().setAll(dialogBox.displayPicture, dialogBox.text);
        return dialogBox;
    }
}
