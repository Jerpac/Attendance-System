import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import java.util.List;

public class MainMenu extends Application {
    private Stage primaryStage;

    // Provide a method to manually start the MainMenu
    public void startMenu(Stage stage) throws Exception {
        this.primaryStage = stage;
        start(stage);
    }

    public void launchMainMenu() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        primaryStage.setTitle("UTD Attendance System");
        Scene scene = new Scene(scrollPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
