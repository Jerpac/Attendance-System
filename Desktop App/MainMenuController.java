import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;

public class MainMenuController {
    @FXML
    private GridPane classesPane; // This should match the fx:id of your Pane in the FXML file
    private int currentRow = 0;
    private int currentCol = 0;

    // Method to add a button dynamically
    public void addNewClassButton() {
        TextInputDialog dialog = new TextInputDialog("Class Section");
        dialog.setTitle("Add new course");
        dialog.setHeaderText("Enter the Course Section:");
        dialog.setContentText("Course:");

        // Show the dialog and wait for user input
        Optional<String> result = dialog.showAndWait();

        // If the user entered text, create the new button with the entered text
        result.ifPresent(text -> {
            System.out.println("Code update with text: " + text);
            Button newClassButton = new Button(text);
            newClassButton.setStyle("-fx-font:24 Calibri");
            newClassButton.setPrefWidth(200);
            newClassButton.setPrefHeight(200);

            // Add the new button to the grid pane at the current row and column
            classesPane.add(newClassButton, currentCol, currentRow);

            // Increment the current column
            currentCol++;

            // Check if we need to move to the next row
            if (currentCol >= 7) {
                currentCol = 0; // Reset column count
                currentRow++; // Move to the next row
            }
        });
    }

    // Method to handle the button click event
    @FXML
    private void handleAaddNewClassButton() {
        System.out.println("yeah");
        addNewClassButton();
    }

    // Your other controller methods
}
