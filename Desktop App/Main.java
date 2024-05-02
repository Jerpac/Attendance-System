import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));

        // Set up the primary stage
        primaryStage.setTitle("UTD Attendance System");
        primaryStage.setScene(new Scene(root, 1200, 950));
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(950);
        primaryStage.setMaxWidth(1200);
        primaryStage.setMaxHeight(950);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
