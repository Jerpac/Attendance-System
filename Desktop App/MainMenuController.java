import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class MainMenuController {

    @FXML
    private Pane bottomPane;

    @FXML
    private void addClassButtonClicked() {
        // Ensure bottomPane is not null before accessing its children
        if (bottomPane != null) {
            // Create a new button
            Button newButton = new Button("Course Section");

            // Customize the new button as needed
            newButton.setPrefSize(179.0, 62.0); // Set preferred size
            newButton.setFont(new javafx.scene.text.Font("Calibri", 24.0)); // Set font

            // Add the new button to the bottomPane
            bottomPane.getChildren().add(newButton);
        }
    }
}
