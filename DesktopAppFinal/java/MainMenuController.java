import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class MainMenuController implements Initializable{
    @FXML
    private GridPane classesPane; // This should match the fx:id of your Pane in the FXML file
    private int currentRow = 0;
    private int currentCol = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add existing classes to the grid pane
        List<courseSec> courseSecs = AttendanceApp.getClassList();
        for (courseSec cs : courseSecs) {
            addNewClassButton(cs.getCourseSec(), cs.getCourseSecID());
        }
    }

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
            addNewClassButton(text);
        });
    }

    public void addNewClassButton(String text) {
        int newClassID = AttendanceApp.addNewClass(text);
        if (newClassID == -1) {
            System.out.println("Error adding new class");
            return;
        } else {
            System.out.println("New class added with ID: " + newClassID);
        }
        addNewClassButton(text, newClassID);
    }

    public void addNewClassButton(String text, int classID) {
        Button newClassButton = new Button(text);
        newClassButton.setStyle("-fx-font:24 Calibri; -fx-cursor: hand; -fx-background-color: #f2dc9b;");
        newClassButton.setPrefWidth(250);
        newClassButton.setPrefHeight(250);

        // add an event handler to the button
        newClassButton.setOnAction(event -> {
            System.out.println("Button clicked: " + text);
            handleTakeAttendanceButton(classID);
        });

        // add a right-click event handler to the button
        newClassButton.setOnContextMenuRequested(event -> {
            System.out.println("Right-clicked on button: " + text);
            // Add code to handle right-click event
            showClassModifyMenu(classID);
        });

        classesPane.setHgap(35); // Horizontal gap
        classesPane.setVgap(35); // Vertical gap
        // Add the new button to the grid pane at the current row and column
        classesPane.add(newClassButton, currentCol, currentRow);

        // Increment the current column
        currentCol++;

        // Check if we need to move to the next row
        if (currentCol >= 4) {
            currentCol = 0; // Reset column count
            currentRow++; // Move to the next row
        }
    }

    // Method to handle the button click event
    @FXML
    private void handleAaddNewClassButton() {
        System.out.println("yeah");
        addNewClassButton();
    }

    // Method to handle the button click event
    @FXML
    private void handleTakeAttendanceButton(int classID) {
        System.out.println("Take Attendance button clicked for class ID: " + classID);
        // Add code to handle Take Attendance button click event
        Stage stage = (Stage) classesPane.getScene().getWindow();
        stage.close();
        AttendanceApp app = new AttendanceApp();
        AttendanceApp.activeClass = classID;
        try{
            app.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to handle the button click event
    @FXML
    private void showClassModifyMenu(int classID) {
        System.out.println("Show class modify menu for class ID: " + classID);

        VBox vbox = new VBox();
        Scene scene = new Scene(vbox, 300, 300);

        Label label = new Label();
        courseSec classInfo = AttendanceApp.getClassInfo(classID);

        try{
            String className = classInfo.getCourseSec();
            label.setText("Modifying class: " + className);

            Label startDateLabel = new Label("Class Start Date (YYYY-MM-DD):");
            TextField startDateField = new TextField();
            startDateField.setText(classInfo.getStartDate().toString());

            Label endDateLabel = new Label("Class End Date (YYYY-MM-DD):");
            TextField endDateField = new TextField();
            endDateField.setText(classInfo.getEndDate().toString());

            Label quizPassLabel = new Label("Quiz Password:");
            TextField quizPassField = new TextField();
            quizPassField.setText(classInfo.getQuizPass());

            Label quizOpenLabel = new Label("Quiz Open Status:");
            // Yes/No radio buttons
            ToggleGroup quizOpenGroup = new ToggleGroup();
            RadioButton yesButton = new RadioButton("Yes");
            yesButton.setToggleGroup(quizOpenGroup);
            RadioButton noButton = new RadioButton("No");
            noButton.setToggleGroup(quizOpenGroup);
            if (classInfo.isQuizOpen()) {
                yesButton.setSelected(true);
            } else {
                noButton.setSelected(true);
            }
            
            Button saveButton = new Button("Save Changes");
            saveButton.setOnAction(event -> {
                // Ensure that dates are in the correct format before continuing
                if (!AttendanceApp.isValidSQLDate(startDateField.getText()) || !AttendanceApp.isValidSQLDate(endDateField.getText())) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid date format");
                    alert.setContentText("Please enter dates in the format YYYY-MM-DD.");
                    alert.showAndWait();
                    return;
                }

                // Ensure that password is under 15 characters
                if (quizPassField.getText().length() > 15) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Password too long");
                    alert.setContentText("Please enter a password that is 15 characters or less.");
                    alert.showAndWait();
                    return;
                }

                // Add code to handle Save Changes button click event
                String startDate = startDateField.getText();
                String endDate = endDateField.getText();
                String quizPass = quizPassField.getText();
                boolean quizOpen = yesButton.isSelected();

                AttendanceApp.updateClass(classID, startDate, endDate, quizPass, quizOpen);
                Stage stage2 = (Stage) vbox.getScene().getWindow();
                stage2.close();
            });

            Button doNotSaveButton = new Button("Do Not Save Changes");
            doNotSaveButton.setOnAction(event -> {
                // Add code to handle Do Not Save Changes button click event
                Stage stage = (Stage) vbox.getScene().getWindow();
                stage.close();
            });

            Button deleteButton = new Button("Delete Class");
            deleteButton.setOnAction(event -> {
                // Show user a yes or no alert to confirm deletion
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Delete Class");
                alert.setHeaderText("Are you sure you want to delete this class?");
                alert.setContentText("This action cannot be undone.");
                alert.getButtonTypes().clear();
                ButtonType yesDeleteButton = new ButtonType("Yes");
                ButtonType noDeleteButton = new ButtonType("No");

                alert.getButtonTypes().addAll(yesDeleteButton, noDeleteButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == yesDeleteButton) {
                    // User clicked Yes, proceed with deletion
                    Stage stage = (Stage) vbox.getScene().getWindow();
                    stage.close();
                } else {
                    // User clicked No or closed the dialog, do nothing
                    return;
                }

                System.out.println("Delete class button clicked for class ID: " + classID);
                // Add code to handle Delete Class button click event
                AttendanceApp.deleteClass(classID);
                Stage stage = (Stage) classesPane.getScene().getWindow();
                stage.close();
                AttendanceApp app = new AttendanceApp();
                try{
                    app.start(stage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            vbox.getChildren().addAll(label, startDateLabel, startDateField, endDateLabel, endDateField, quizPassLabel, quizPassField, quizOpenLabel, yesButton, noButton, saveButton, doNotSaveButton, deleteButton);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
