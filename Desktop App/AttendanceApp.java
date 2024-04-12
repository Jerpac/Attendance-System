import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

public class AttendanceApp extends Application {
    private TableView<List<String>> tableView = new TableView<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("UTD Attendance System");

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openMenuItem = new MenuItem("Open CSV...");
        openMenuItem.setOnAction(e -> openCSV(primaryStage));
        fileMenu.getItems().add(openMenuItem);
        menuBar.getMenus().add(fileMenu);

        root.setTop(menuBar);
        root.setCenter(tableView);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openCSV(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            loadCsvData(file);
        }
    }

    private void loadCsvData(File file) {
        Charset charset = StandardCharsets.UTF_16LE;  // Set the charset to UTF-16LE
        try {
            List<List<String>> data = Files.lines(file.toPath(), charset)
                .map(line -> List.of(line.split(",")))  // You might need to adjust the delimiter if it doesn't work correctly
                .collect(Collectors.toList());

            tableView.getItems().clear();
            tableView.getColumns().clear();

            if (!data.isEmpty()) {
                // Create columns dynamically based on CSV header
                List<String> headers = data.get(0);
                for (int i = 0; i < headers.size(); i++) {
                    int colIdx = i;
                    TableColumn<List<String>, String> column = new TableColumn<>(headers.get(i));
                    column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(colIdx)));
                    tableView.getColumns().add(column);
                }
                tableView.getItems().addAll(data.subList(1, data.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}