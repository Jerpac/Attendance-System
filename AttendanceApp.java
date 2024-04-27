import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.MouseEvent;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

public class AttendanceApp extends Application {
    private TableView<Student> studentTableView = new TableView<>();
    private Label totalStudentsLabel = new Label("Total Students: 0");
    private Label presentStudentsLabel = new Label("Present Students: 0");
    private Label absentStudentsLabel = new Label("Absent Students: 0");
    private Label attendanceRateLabel = new Label("Attendance Rate: 0%");
    private ObservableList<Student> studentList = FXCollections.observableArrayList();

    private TableView<Quiz> quizQuestionTableView = new TableView<>();
    private ObservableList<Quiz> quizQuestionList = FXCollections.observableArrayList();
    private Label totalQuestionsLabel = new Label("Total Questions: 0");

    int activeTable = 0; // 0 for student, 1 for quiz

    private static final String DB_URL = "jdbc:mysql://localhost:3306/attendancesystem?useSSL=false";
    private static final String USER = "root";
    private static final String PASS = "4445";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("UTD Attendance System");
        setupStudentTableView();
        setupQuizQuestionTableView();
        Scene scene = new Scene(setupRootPane(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        loadStudentsFromDatabase();
        updateStudnetAnalytics();
        setupStudentTableClickEvent();

        loadQuizQuestionsFromDatabase();
        updateQuizAnalytics();
    }

    private BorderPane setupRootPane() {
        BorderPane root = new BorderPane();
        root.setTop(setupMenuBar());
        root.setCenter(studentTableView);
        root.setBottom(setupStudentAnalyticsPanel());
        return root;
    }

    private MenuBar setupMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().add(createLoadCSVMenuItem("Load Student Roster CSV"));
        fileMenu.getItems().add(createLoadCSVMenuItem("Load Quiz CSV"));
        fileMenu.getItems().add(createSaveCSVMenuItem("Save Student Table as CSV"));

        Menu viewMenu = new Menu("View");
        viewMenu.getItems().add(createTableToggleMenuItem("Toggle between Students and Quiz Questions"));

        menuBar.getMenus().add(fileMenu);
        menuBar.getMenus().add(viewMenu);
        return menuBar;
    }

    private MenuItem createLoadCSVMenuItem(String title) {
        MenuItem menuItem = new MenuItem(title);
        menuItem.setOnAction(e -> loadCSV());
        return menuItem;
    }

    private MenuItem createSaveCSVMenuItem(String title) {
        MenuItem menuItem = new MenuItem(title);
        menuItem.setOnAction(e -> saveCSV());
        return menuItem;
    }

    private MenuItem createTableToggleMenuItem(String title) {
        MenuItem menuItem = new MenuItem(title);
        menuItem.setOnAction(e -> toggleTable());
        return menuItem;
    }

    // toggle between student and quiz tables
    private void toggleTable() {
        System.out.println("Toggling table");
        if (activeTable == 0) {
            activeTable = 1;
            studentTableView.setVisible(false);
            quizQuestionTableView.setVisible(true);
            BorderPane root = (BorderPane) studentTableView.getParent();
            root.setCenter(quizQuestionTableView);
            root.setBottom(setupQuizAnalyticsPanel());
            System.out.println("Switched to quiz table");
        } else {
            activeTable = 0;
            quizQuestionTableView.setVisible(false);
            studentTableView.setVisible(true);
            BorderPane root = (BorderPane) quizQuestionTableView.getParent();
            root.setCenter(studentTableView);
            root.setBottom(setupStudentAnalyticsPanel());
            System.out.println("Switched to student table");
        }
    }

    private HBox setupStudentAnalyticsPanel() {
        HBox analyticsPanel = new HBox(10);
        analyticsPanel.getChildren().addAll(totalStudentsLabel, presentStudentsLabel, absentStudentsLabel, attendanceRateLabel);
        return analyticsPanel;
    }

    private HBox setupQuizAnalyticsPanel() {
        HBox analyticsPanel = new HBox(10);
        analyticsPanel.getChildren().addAll(totalQuestionsLabel);
        return analyticsPanel;
    }

    private void loadCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);
        // Print the file path to the console
        System.out.println(file.getAbsolutePath());
        if (file != null) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                CSVtoDB.fullUploadFromPath(file.getAbsolutePath(), conn);
                loadStudentsFromDatabase();
                updateStudnetAnalytics();

                loadQuizQuestionsFromDatabase();
                updateQuizAnalytics();
            } catch (SQLException e) {
                showErrorDialog("Failed to load the file and update the database: " + e.getMessage());
            }
        }
    }

    private void loadStudentsFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT studentID, studentFName, studentLName FROM student")) {
            studentList.clear();
            while (rs.next()) {
                studentList.add(new Student(rs.getString("studentFName"), rs.getString("studentLName"), rs.getString("studentID"), "Absent"));
            }
        } catch (SQLException e) {
            showErrorDialog("Failed to load students from database: " + e.getMessage());
        }
    }

    // This fn is called when the CSV Reader detects a Quiz Question CSV
    private void loadQuizQuestionsFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM quizquestions")) {
            quizQuestionList.clear();
            while (rs.next()) {
                List<String> answers = new ArrayList<>();
                answers.add(rs.getString("answer1"));
                answers.add(rs.getString("answer2"));
                answers.add(rs.getString("answer3"));
                answers.add(rs.getString("answer4"));
                quizQuestionList.add(new Quiz(rs.getInt("questionID"), rs.getString("questionContent"), answers, rs.getString("correct_answer")));
            }
            // print the quiz questions to the console
            for (Quiz q : quizQuestionList) {
                System.out.println(q);
            }
        } catch (SQLException e) {
            showErrorDialog("Failed to load quiz questions from database: " + e.getMessage());
        }
    }

    // Builds the table for student view 
    private void setupStudentTableView() {
        TableColumn<Student, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        TableColumn<Student, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        TableColumn<Student, String> studentIdCol = new TableColumn<>("Student ID");
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        TableColumn<Student, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        studentTableView.getColumns().addAll(firstNameCol, lastNameCol, studentIdCol, statusCol);
        studentTableView.setItems(studentList);
        studentTableView.setRowFactory(tv -> new TableRow<Student>() {
            @Override
            protected void updateItem(Student item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if ("Present".equals(item.getStatus())) {
                    setStyle("-fx-background-color: lightgreen;");
                } else {
                    setStyle("-fx-background-color: salmon;");
                }
            }
        });
    }

    private void setupQuizQuestionTableView() {
        // Database: (int) QuestionID, questionContent, answer1, answer2, answer3, answer4, correct_answer
        TableColumn<Quiz, Integer> questionIDCol = new TableColumn<>("Question ID");
        questionIDCol.setCellValueFactory(new PropertyValueFactory<>("questionNum"));
        TableColumn<Quiz, String> questionContentCol = new TableColumn<>("Question Content");
        questionContentCol.setCellValueFactory(new PropertyValueFactory<>("questionContent"));
        TableColumn<Quiz, String> answer1Col = new TableColumn<>("Answer 1");
        answer1Col.setCellValueFactory(cellData -> {
            List<String> answers = cellData.getValue().getAnswers();
            if (answers != null && answers.size() >= 1) {
                return new SimpleStringProperty(answers.get(0));
            } else {
                return new SimpleStringProperty("");
            }
        });
        TableColumn<Quiz, String> answer2Col = new TableColumn<>("Answer 2");
        answer2Col.setCellValueFactory(cellData -> {
            List<String> answers = cellData.getValue().getAnswers();
            if (answers != null && answers.size() >= 2) {
                return new SimpleStringProperty(answers.get(1));
            } else {
                return new SimpleStringProperty("");
            }
        });
        TableColumn<Quiz, String> answer3Col = new TableColumn<>("Answer 3");
        answer3Col.setCellValueFactory(cellData -> {
            List<String> answers = cellData.getValue().getAnswers();
            if (answers != null && answers.size() >= 3) {
                return new SimpleStringProperty(answers.get(2));
            } else {
                return new SimpleStringProperty("");
            }
        });
        TableColumn<Quiz, String> answer4Col = new TableColumn<>("Answer 4");
        answer4Col.setCellValueFactory(cellData -> {
            List<String> answers = cellData.getValue().getAnswers();
            if (answers != null && answers.size() >= 4) {
                return new SimpleStringProperty(answers.get(3));
            } else {
                return new SimpleStringProperty("");
            }
        });
        TableColumn<Quiz, String> correctAnswerCol = new TableColumn<>("Correct Answer");
        correctAnswerCol.setCellValueFactory(new PropertyValueFactory<>("CorrectAnswer"));
        quizQuestionTableView.getColumns().addAll(questionIDCol, questionContentCol, answer1Col, answer2Col, answer3Col, answer4Col, correctAnswerCol);
        // Print the quiz questions to the console
        for (Quiz q : quizQuestionList) {
            System.out.println(q);
        }
        quizQuestionTableView.setItems(quizQuestionList);
        quizQuestionTableView.setRowFactory(tv -> new TableRow<Quiz>() {
            @Override
            protected void updateItem(Quiz item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void updateStudnetAnalytics() {
        int totalStudents = studentList.size();
        int presentCount = (int) studentList.stream().filter(s -> "Present".equals(s.getStatus())).count();
        int absentCount = totalStudents - presentCount;
        double attendanceRate = totalStudents > 0 ? 100.0 * presentCount / totalStudents : 0;
        totalStudentsLabel.setText("Total Students: " + totalStudents);
        presentStudentsLabel.setText("Present Students: " + presentCount);
        absentStudentsLabel.setText("Absent Students: " + absentCount);
        attendanceRateLabel.setText(String.format("Attendance Rate: %.2f%%", attendanceRate));
    }

    private void updateQuizAnalytics() {
        int totalQuestions = quizQuestionList.size();
        totalQuestionsLabel.setText("Total Questions: " + totalQuestions);
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void clickStudent(MouseEvent event) {
        if (activeTable == 0 && event.getClickCount() == 1 && studentTableView.getSelectionModel().getSelectedItem() != null) {
            Student student = studentTableView.getSelectionModel().getSelectedItem();
            if (student != null) {
                // print student to console
                System.out.println(student);
                student.setStatus("Absent".equals(student.getStatus()) ? "Present" : "Absent");
                studentTableView.refresh();
                updateStudnetAnalytics();
            }
        }
    }

    // Add this method to setup the click event for the student table
    private void setupStudentTableClickEvent() {
        studentTableView.setOnMouseClicked(this::clickStudent);
    }

    private void saveCSV() {
        // make the filename AttendanceApp-MM-DD-YYYY-hh-mm-ss.csv
        String filename = "AttendanceApp-" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".csv";
        // Let the user choose the download folder
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(filename);
        File downloadPath = fileChooser.showSaveDialog(null);
        if (downloadPath == null) {
            return;
        } else {
            System.out.println("Saving to: " + downloadPath.getAbsolutePath());
        }
        List<String> studentData = new ArrayList<>();
        studentData.add("First Name,Last Name,Student ID,Status");
        for (Student s : studentList) {
            studentData.add(s.getFirstName() + "," + s.getLastName() + "," + s.getStudentId() + "," + s.getStatus());
        }
        // save the student data to the file
        try {
            FileWriter writer = new FileWriter(downloadPath);
            for (String data : studentData) {
                writer.write(data + "\n");
            }
            writer.close();
            System.out.println("CSV file saved successfully.");
        } catch (IOException e) {
            showErrorDialog("Failed to save the file: " + e.getMessage());
        }
    }
}