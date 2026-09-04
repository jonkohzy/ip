package jonk;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controls the main Jonk chatbot window.
 */
public class MainWindow extends AnchorPane {
    private static final Duration EXIT_DELAY = Duration.millis(800);

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private Jonk jonk;

    /**
     * Configures automatic scrolling after FXML fields have been injected.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(
                (observable, oldHeight, newHeight) -> scrollPane.setVvalue(1.0));
    }

    /**
     * Injects the chatbot used to respond to commands.
     *
     * @param jonk Jonk chatbot instance.
     */
    public void setJonk(Jonk jonk) {
        this.jonk = jonk;
        dialogContainer.getChildren().add(DialogBox.getJonkDialog(jonk.getWelcomeMessage()));
    }

    /**
     * Displays the user's command and Jonk's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = jonk.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getJonkDialog(response));
        userInput.clear();

        if (input.equals("bye")) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition exitPause = new PauseTransition(EXIT_DELAY);
            exitPause.setOnFinished(event -> Platform.exit());
            exitPause.play();
        }
    }
}
