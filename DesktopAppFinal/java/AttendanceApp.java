import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import io.github.cdimascio.dotenv.Dotenv;

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
    static int activeClass = -1;

    // private ObservableList<Integer> classList = FXCollections.observableArrayList();
    // private ObservableList<String> classNames = FXCollections.observableArrayList();

    private ObservableList<courseSec> classList = FXCollections.observableArrayList();

    // private static final String DB_URL = "jdbc:mysql://localhost:3306/attendancesystem?useSSL=false";
    // private static final String USER = "root";
    // private static final String PASS = "4445";

    static Dotenv dotenv = Dotenv.load();
    private static final String DB_URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASS = dotenv.get("DB_PASS");

    public static void main(String[] args) {
        // Initialize MainMenu
        MainMenu mainMenu = new MainMenu();
        // Stage menuStage = new Stage();
        // try {
        //     mainMenu.startMenu(menuStage);
        // } catch (Exception e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // } // This shows the menu
        mainMenu.launchMainMenu(); // This shows the menu
    }
    
    public void start(Stage primaryStage, int chosenClass){
        activeClass = chosenClass;
        try {
            start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the data from the database
        loadClassesFromDatabase();
        loadQuizQuestionsFromDatabase();
        loadStudentsFromDatabase();

        // set activeCourse to the first class in the list
        if (!classList.isEmpty() && activeClass == -1) {
            // activeClass = classList.get(0);
            activeClass = classList.get(0).getCourseSecID();
            loadStudentsFromDatabase();
            loadQuizQuestionsFromDatabase();
        }

        primaryStage.setTitle("UTD Attendance System");
        setupStudentTableView();
        setupQuizQuestionTableView();

        Scene scene = new Scene(setupRootPane(), 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Setups the student table
        updateStudnetAnalytics();
        setupStudentTableClickEvent();

        // Setups the quiz table
        updateQuizAnalytics();
        setupQuizQuestionTableClickEvent();
    }

    private BorderPane setupRootPane() {
        BorderPane root = new BorderPane();
        root.setTop(setupMenuBar());
        root.setCenter(studentTableView);
        root.setBottom(setupStudentAnalyticsPanel());
        return root;
    }

    // Loads the top menu bar with "File", View", "Table", and "Class" options
    private MenuBar setupMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().add(createLoadStudentCSVMenuItem("Load Student Roster CSV"));
        fileMenu.getItems().add(createLoadQuizCSVMenuItem("Load Quiz CSV"));
        fileMenu.getItems().add(createSaveCSVMenuItem("Save Student Table as CSV"));
        fileMenu.getItems().add(createSaveandReturnToMainMenu("Save and Return to Main Menu"));
        fileMenu.getItems().add(createDoNotSaveandReturnToMainMenu("Do Not Save and Return to Main Menu"));

        Menu viewMenu = new Menu("View");
        viewMenu.getItems().add(createTableToggleMenuItem("Toggle between Students and Quiz Questions"));

        Menu tableMenu = new Menu("Table");
        tableMenu.getItems().add(createResetAttendanceMenuItem("Reset Attendance"));
        tableMenu.getItems().add(createLockInAttendanceMenuItem("Lock In Attendance"));
        tableMenu.getItems().add(createRefreshTableMenuItem("Refresh Table from Database"));

        Menu courseMenu = new Menu("Class");
        // for (int classID : classList) {
        //     MenuItem menuItem = new MenuItem("Class " + classNames.get(classList.indexOf(classID)));
        //     menuItem.setOnAction(e -> {
        //         activeClass = classID;
        //         System.out.println("Selected Class: " + activeClass);
        //         loadStudentsFromDatabase();
        //         loadQuizQuestionsFromDatabase();
        //     });
        //     courseMenu.getItems().add(menuItem);
        // }

        for (courseSec course : classList) {
            MenuItem menuItem = new MenuItem("Class " + course.getCourseSec());
            menuItem.setOnAction(e -> {
                activeClass = course.getCourseSecID();
                System.out.println("Selected Class: " + activeClass);
                loadStudentsFromDatabase();
                loadQuizQuestionsFromDatabase();
            });
            courseMenu.getItems().add(menuItem);
        }

        menuBar.getMenus().add(fileMenu);
        menuBar.getMenus().add(viewMenu);
        menuBar.getMenus().add(tableMenu);
        menuBar.getMenus().add(courseMenu);
        return menuBar;
    }

    // private MenuItem createLoadCSVMenuItem(String title) {
    //     MenuItem menuItem = new MenuItem(title);
    //     menuItem.setOnAction(e -> loadCSV());
    //     return menuItem;
    // }

    // Links button to load student roster from CSV
    private MenuItem createLoadStudentCSVMenuItem(String title) {
        MenuItem menuItem = new MenuItem(title);
        menuItem.setOnAction(e -> {
            loadStudentCSV();
            loadStudentsFromDatabase();
            updateStudnetAnalytics();
        });
        return menuItem;
    }

    // Links button to load quiz questions from CSV
    private MenuItem createLoadQuizCSVMenuItem(String title) {
        MenuItem menuItem = new MenuItem(title);
        menuItem.setOnAction(e -> {
            loadQuizCSV();
            loadQuizQuestionsFromDatabase();
            updateQuizAnalytics();
        });
        return menuItem;
    }

    // Links button to save the current table to a CSV file
    private MenuItem createSaveCSVMenuItem(String title) {
        MenuItem menuItem = new MenuItem(title);
        menuItem.setOnAction(e -> saveCSV());
        return menuItem;
    }

    // Link button to toggle between student and quiz tables
    private MenuItem createTableToggleMenuItem(String title) {
        MenuItem menuItem = new MenuItem(title);
        menuItem.setOnAction(e -> toggleTable());
        return menuItem;
    }

    // Link button to refresh tables with the most recent data from the database
    private MenuItem createRefreshTableMenuItem(String title) {
        MenuItem menuItem = new MenuItem(title);
        menuItem.setOnAction(e -> refreshTables());
        return menuItem;
    }

    // Button to reset the attendance of all students to absent
    private MenuItem createResetAttendanceMenuItem(String title) {
        MenuItem menuItem = new MenuItem(title);
        menuItem.setOnAction(e -> resetAttendance());
        return menuItem;
    }

    // Button to upload attendance to the database
    private MenuItem createLockInAttendanceMenuItem(String title) {
        MenuItem menuItem = new MenuItem(title);
        menuItem.setOnAction(e -> lockInAttendance());
        return menuItem;
    }

    // Create the menu button for the user to save the data to a CSV file and exit back to the main menu
    private MenuItem createSaveandReturnToMainMenu(String title) {
        MenuItem menuItem = new MenuItem(title);
        menuItem.setOnAction(e -> {
            // If not in the student table, switch to the student table
            if (activeTable == 1) {
                toggleTable();
            }

            // Save the data to the database
            for (Student s : studentList) {
                uploadStudentAttendance(Integer.parseInt(s.getStudentId()), s.getStatus(), activeClass);
            }
            // Close the current window
            Stage stage = (Stage) studentTableView.getScene().getWindow();
            stage.close();
            // Return to the main menu
            MainMenu mainMenu = new MainMenu();
            try{
                mainMenu.startMenu(stage);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        });
        return menuItem;
    }

    // Create the menu button for the user to return to the main menu without saving
    private MenuItem createDoNotSaveandReturnToMainMenu(String title) {
        MenuItem menuItem = new MenuItem(title);
        menuItem.setOnAction(e -> {
            // If not in the student table, switch to the student table
            if (activeTable == 1) {
                toggleTable();
            }

            // Close the current window
            Stage stage = (Stage) studentTableView.getScene().getWindow();
            stage.close();
            // Return to the main menu
            MainMenu mainMenu = new MainMenu();
            try{
                mainMenu.startMenu(stage);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        });
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

    // Footer for the student table
    private HBox setupStudentAnalyticsPanel() {
        HBox analyticsPanel = new HBox(10);
        analyticsPanel.getChildren().addAll(totalStudentsLabel, presentStudentsLabel, absentStudentsLabel, attendanceRateLabel);
        return analyticsPanel;
    }

    // footer for the quiz table
    private HBox setupQuizAnalyticsPanel() {
        HBox analyticsPanel = new HBox(10);
        analyticsPanel.getChildren().addAll(totalQuestionsLabel);
        return analyticsPanel;
    }

    // private void loadCSV() {
    //     FileChooser fileChooser = new FileChooser();
    //     fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    //     File file = fileChooser.showOpenDialog(null);
    //     // Print the file path to the console
    //     System.out.println(file.getAbsolutePath());
    //     if (file != null) {
    //         try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
    //             CSVtoDB.fullUploadFromPath(file.getAbsolutePath(), conn, activeClass);
    //         } catch (SQLException e) {
    //             showErrorDialog("Failed to load the file and update the database: " + e.getMessage());
    //         }
    //         loadStudentsFromDatabase();
    //         updateStudnetAnalytics();

    //         loadQuizQuestionsFromDatabase();
    //         updateQuizAnalytics();
    //     }
    // }

    // Save the students from a CSV to the database
    private void loadStudentCSV(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);
        // Print the file path to the console
        System.out.println(file.getAbsolutePath());
        if (file != null) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                CSVtoDB.fullUploadStudentsFromPath(file.getAbsolutePath(), conn, activeClass);
            } catch (SQLException e) {
                showErrorDialog("Failed to load the file and update the database: " + e.getMessage());
            }
            loadStudentsFromDatabase();
            updateStudnetAnalytics();
        }
    }

    // Load a CSV file formatted as a quiz CSV to the database
    private void loadQuizCSV(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);
        // Print the file path to the console
        System.out.println(file.getAbsolutePath());
        if (file != null) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                CSVtoDB.fullUploadQuizQuestionsFromPath(file.getAbsolutePath(), conn, activeClass);
            } catch (SQLException e) {
                showErrorDialog("Failed to load the file and update the database: " + e.getMessage());
            }
            loadQuizQuestionsFromDatabase();
            updateQuizAnalytics();
        }
    }

    // Load students from the database in the current class
    private void loadStudentsFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
            //  ResultSet rs = stmt.executeQuery("SELECT studentID, studentFName, studentLName FROM student WHERE studentID IN (SELECT studentID FROM student_in_classes WHERE classID = " + activeClass + ")")) {
            // modify the statement to join the student and student_in_classes tables and get
            ResultSet rs = stmt.executeQuery("SELECT student.studentID, student.studentFName, student.studentLName, student_in_classes.isPresent FROM student JOIN student_in_classes ON student.studentID = student_in_classes.studentID WHERE student_in_classes.classID = " + activeClass)) {
            studentList.clear();

            while (rs.next()) {
                String fName, lName, studentID, isPresent;
                fName = rs.getString("studentFName");
                lName = rs.getString("studentLName");
                studentID = rs.getString("studentID");
                isPresent = rs.getInt("isPresent") == 1 ? "Present" : "Absent";
                
                studentList.add(new Student(fName, lName, studentID, isPresent));
            }

            
        } catch (SQLException e) {
            showErrorDialog("Failed to load students from database: " + e.getMessage());
        }
        
    }

    // This fn is called when the CSV Reader detects a Quiz Question CSV
    private void loadQuizQuestionsFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM quizquestions WHERE classID = " + activeClass)) {
            quizQuestionList.clear();
            while (rs.next()) {
                List<String> answers = new ArrayList<>();
                answers.add(rs.getString("answer1"));
                answers.add(rs.getString("answer2"));
                answers.add(rs.getString("answer3"));
                answers.add(rs.getString("answer4"));
                quizQuestionList.add(new Quiz(rs.getInt("questionID"), rs.getString("questionContent"), answers, rs.getInt("correct_answer")));
            }
            // print the quiz questions to the console
            for (Quiz q : quizQuestionList) {
                System.out.println(q);
            }
        } catch (SQLException e) {
            showErrorDialog("Failed to load quiz questions from database: " + e.getMessage());
        }
    }

    // Function to load the classes from the database
    private void loadClassesFromDatabase() {
        // Get the list of courses
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM class")) {
            while (rs.next()) {
                System.out.println("Class ID: " + rs.getInt("class-id"));
                // if (!classList.contains(rs.getInt("class-id"))) {
                    // classList.add(rs.getInt("class-id"));
                    // classNames.add(rs.getString("course-sec"));
                if (!classList.contains(new courseSec(rs.getString("course-sec"), rs.getInt("class-id")))) {
                    classList.add(new courseSec(rs.getString("course-sec"), rs.getInt("class-id")));
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Failed to load courses from database: " + e.getMessage());
        }
    }

    // Function to load the attendance history of a student
    public static List<courseSec> getClassList() {
        List<courseSec> localClassList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM class")) {
            while (rs.next()) {
                courseSec course = new courseSec(rs.getString("course-sec"), rs.getInt("class-id"));
                localClassList.add(course);
            }
            return localClassList;
        } catch (SQLException e) {
            System.out.println("Failed to load courses from database: " + e.getMessage());
            return null;
        }
    }

    // Function to refresh the table information after the user selects the refresh button
    private void refreshTables() {
        loadStudentsFromDatabase();
        updateStudnetAnalytics();
        setupStudentTableClickEvent();

        loadQuizQuestionsFromDatabase();
        updateQuizAnalytics();
        setupQuizQuestionTableClickEvent();

        // update class list
        loadClassesFromDatabase();

        System.out.println("Tables refreshed");
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
        TableColumn<Student, String> presentCountCol = new TableColumn<>("Present Count");
        presentCountCol.setCellValueFactory(cellData -> {
            // Get the amount of times present from studentAttendance table in the current class
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM studentAttendance WHERE studentID = ? AND classID = ? AND status = 'Present'")) {
                stmt.setInt(1, Integer.parseInt(cellData.getValue().getStudentId()));
                stmt.setInt(2, activeClass);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new SimpleStringProperty(rs.getString(1));
                }
            } catch (SQLException e) {
                showErrorDialog("Failed to get present count: " + e.getMessage());
            }
            return new SimpleStringProperty("0");
        });
        TableColumn<Student, String> absentCountCol = new TableColumn<>("Absent Count");
        absentCountCol.setCellValueFactory(cellData -> {
            // Get the amount of times absent from studentAttendance table in the current class
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM studentAttendance WHERE studentID = ? AND classID = ? AND status = 'Absent'")) {
                stmt.setInt(1, Integer.parseInt(cellData.getValue().getStudentId()));
                stmt.setInt(2, activeClass);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new SimpleStringProperty(rs.getString(1));
                }
            } catch (SQLException e) {
                showErrorDialog("Failed to get absent count: " + e.getMessage());
            }
            return new SimpleStringProperty("0");
        });
        TableColumn<Student, String> attendanceRateCol = new TableColumn<>("Attendance Rate");
        attendanceRateCol.setCellValueFactory(cellData -> {
            // add the values of presentCountCol and absentCountCol to get the total number of classes
            int presentCount = Integer.parseInt(presentCountCol.getCellData(cellData.getValue()));
            int absentCount = Integer.parseInt(absentCountCol.getCellData(cellData.getValue()));
            int totalClasses = presentCount + absentCount;
            double attendanceRate = totalClasses > 0 ? 100.0 * presentCount / totalClasses : 0;
            return new SimpleStringProperty(String.format("%.2f%%", attendanceRate));
        });
        TableColumn<Student, String> currentConsecutiveAbsentCol = new TableColumn<>("Consecutive Absenses");
        currentConsecutiveAbsentCol.setCellValueFactory(cellData -> {
            // Get the amount of times absent from studentAttendance table in the current class
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM studentAttendance WHERE studentID = ? AND classID = ? ORDER BY dateAttended DESC")) {
                stmt.setInt(1, Integer.parseInt(cellData.getValue().getStudentId()));
                stmt.setInt(2, activeClass);
                ResultSet rs = stmt.executeQuery();
                int consecutiveAbsent = 0;
                while (rs.next()) {
                    if ("Absent".equals(rs.getString("status"))) {
                        consecutiveAbsent++;
                    } else {
                        break;
                    }
                }
                return new SimpleStringProperty(String.valueOf(consecutiveAbsent));
            } catch (SQLException e) {
                showErrorDialog("Failed to get current consecutive absenses: " + e.getMessage());
            }
            return new SimpleStringProperty("0");
        });
        TableColumn<Student, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(String.valueOf(gradeStudent(Integer.parseInt(cellData.getValue().getStudentId()), activeClass)));
        });
        // Get the last used ip address for the student (lastUsedIPAddress) in student
        TableColumn<Student, String> ipCol = new TableColumn<>("Last Used IP");
        ipCol.setCellValueFactory(cellData -> {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement stmt = conn.prepareStatement("SELECT lastUsedIPAddress FROM student WHERE studentID = ?")) {
                stmt.setInt(1, Integer.parseInt(cellData.getValue().getStudentId()));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new SimpleStringProperty(rs.getString("lastUsedIPAddress"));
                }
            } catch (SQLException e) {
                showErrorDialog("Failed to get last used IP address: " + e.getMessage());
            }
            return new SimpleStringProperty("N/A");
        });
        

        studentTableView.getColumns().addAll(firstNameCol, lastNameCol, studentIdCol, statusCol, presentCountCol, absentCountCol, attendanceRateCol, currentConsecutiveAbsentCol, gradeCol, ipCol);
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

    // Function to setup the quiz question table view
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
        // for (Quiz q : quizQuestionList) {
        //     System.out.println(q);
        // }
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

    // Function to get the grade of a student
    private static Double gradeStudent(int studentID, int classID){
        // Get all the questions for the class that the student has answered from studentresponses
        // Get all the questions for the class from quizquestions
        // Compare the two lists and calculate the grade
        int correctAnswers = 0;
        int totalQuestions = 0;

        // System.out.println("Grading student " + studentID + " for class " + classID);

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM studentresponses WHERE studentID = ? AND classID = ?")) {
            stmt.setInt(1, studentID);
            stmt.setInt(2, classID);
            ResultSet rs = stmt.executeQuery();
            List<Integer> studentResponses = new ArrayList<>();
            List<Integer> answeredQuestionIDs = new ArrayList<>();
            while (rs.next()) {
                studentResponses.add(rs.getInt("response"));
                answeredQuestionIDs.add(rs.getInt("questionID"));
                // System.out.println("Student Response: " + rs.getInt("response"));
            }
            
            // Get the correct answers for the questions that the student has answered
            // Loop through answeredQuestionIDs and get the correct answer for each question
            for (int questionID : answeredQuestionIDs) {
                try (PreparedStatement stmt2 = conn.prepareStatement("SELECT correct_answer FROM quizquestions WHERE questionID = ? AND classID = ?")) {
                    stmt2.setInt(1, questionID);
                    stmt2.setInt(2, classID);
                    ResultSet rs2 = stmt2.executeQuery();
                    if (rs2.next()) {
                        totalQuestions++;
                        // System.out.println("Correct Answer: " + rs2.getInt("correct_answer"));
                        if (studentResponses.get(answeredQuestionIDs.indexOf(questionID)) == rs2.getInt("correct_answer")) {
                            correctAnswers++;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to grade student: " + e.getMessage());
        }

        return totalQuestions > 0 ? 100.0 * correctAnswers / totalQuestions : 0;
    }

    // Function to update the footer of the student table
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

    // Function to update the footer of the quiz table
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

    // Toggles between present and absent for a student
    public void clickStudent(MouseEvent event) {
        if (activeTable == 0 && event.getClickCount() == 1 && studentTableView.getSelectionModel().getSelectedItem() != null) {
            Student student = studentTableView.getSelectionModel().getSelectedItem();
            if (student != null) {
                // print student to console
                System.out.println(student);
                student.setStatus("Absent".equals(student.getStatus()) ? "Present" : "Absent");
                studentTableView.refresh();
                updateStudnetAnalytics();
                changeStudentAttendance(Integer.parseInt(student.getStudentId()), student.getStatus());
            }
        // } else if (activeTable == 0 && event.getClickCount() == 2 && studentTableView.getSelectionModel().getSelectedItem() != null) {
        //     Student student = studentTableView.getSelectionModel().getSelectedItem();
        //     openAttendanceHistoryDialog(student);
        }
    }

    // Function to add a context menu to the student table. Allows the user to edit student attendance history
    private void rightClickStudent(ContextMenuEvent event) {
        // Create a new context menu
        ContextMenu contextMenu = new ContextMenu();

        // Add menu to edit the student's attendance history
        MenuItem editItem = new MenuItem("Edit Attendance History");
        editItem.setOnAction(e -> {
            Student selectedStudent = studentTableView.getSelectionModel().getSelectedItem();
            if (selectedStudent != null) {
                openAttendanceHistoryDialog(selectedStudent);
            }
            contextMenu.hide();
        });

        // Add the menu items to the context menu
        contextMenu.getItems().addAll(editItem);

        // Show the context menu
        contextMenu.show(studentTableView, event.getScreenX(), event.getScreenY());

        // Make the context menu disappear when the user clicks outside of it
        studentTableView.setOnMouseClicked(e -> contextMenu.hide());
    }

    // Click on a quiz question to open the quiz question modification dialog
    // public void clickQuizQuestion(MouseEvent event) {
    //     if (activeTable == 1 && event.getClickCount() == 2 && quizQuestionTableView.getSelectionModel().getSelectedItem() != null) {
    //         Quiz quiz = quizQuestionTableView.getSelectionModel().getSelectedItem();
    //         if (quiz != null) {
    //             // print quiz to console
    //             System.out.println(quiz);
    //             openQuizQuestionModification(quiz);
    //         }
    //     } 
    // }

    // Function to add a context menu to the quiz question table. Allows the user to edit, add, or delete quiz questions
    private void rightClickQuizQuestion(ContextMenuEvent event) {
        // Create a new context menu
        ContextMenu contextMenu = new ContextMenu();

        // Add menu to edit the question
        MenuItem editItem = new MenuItem("Edit question");
        editItem.setOnAction(e -> {
            Quiz selectedQuestion = quizQuestionTableView.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
                openQuizQuestionModification(selectedQuestion);
            }
            contextMenu.hide();
        });

        // Create a new menu item for adding a new quiz question
        MenuItem addItem = new MenuItem("Add question");
        addItem.setOnAction(e -> {
            // Create a new quiz question
            System.out.println("Adding new quiz question");
            int addedQuestionID = addFirstEmptyQuizQuestion();
            refreshTables();
            openQuizQuestionModification(new Quiz(addedQuestionID, "New Question", new ArrayList<>(Arrays.asList("Answer 1", "Answer 2", "Answer 3", "Answer 4")), 1));
            contextMenu.hide();
        });

        // Create a new menu item for deleting a quiz question
        MenuItem deleteItem = new MenuItem("Delete question");
        deleteItem.setOnAction(e -> {
            // Get the selected quiz question
            Quiz selectedQuestion = quizQuestionTableView.getSelectionModel().getSelectedItem();

            // Delete the selected question
            if (selectedQuestion != null) {
                try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM quizquestions WHERE questionID = ? AND classID = ?")) {
                    stmt.setInt(1, Integer.parseInt(selectedQuestion.getQuestionNum()));
                    stmt.setInt(2, activeClass);
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    // showErrorDialog("Failed to delete quiz question: " + ex.getMessage());
                    showErrorDialog("Failed to delete question. Someone has most likely already answered.");
                }
                refreshTables();
            }
        });

        // Add the menu items to the context menu
        contextMenu.getItems().addAll(editItem, addItem, deleteItem);

        // Show the context menu
        contextMenu.show(quizQuestionTableView, event.getScreenX(), event.getScreenY());

        // Make the context menu disappear when the user clicks outside of it
        quizQuestionTableView.setOnMouseClicked(e -> contextMenu.hide());
    }

    // Function to change the attendance history of a student
    private void openAttendanceHistoryDialog(Student student) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Attendance History for " + student.getFirstName() + " " + student.getLastName());

        // Create a table to show attendance history
        TableView<AttendanceRecord> attendanceTable = new TableView<>();
        loadAttendanceHistory(attendanceTable, student.getStudentId());

        TableColumn<AttendanceRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<AttendanceRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(ComboBoxTableCell.forTableColumn("Present", "Absent"));
        statusCol.setOnEditCommit(e -> {
            AttendanceRecord record = e.getRowValue();
            record.setStatus(e.getNewValue());
            updateAttendanceRecord(student.getStudentId(), record.getDate(), record.getStatus());
        });

        attendanceTable.getColumns().addAll(dateCol, statusCol);
        attendanceTable.setEditable(true);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> dialogStage.close());

        // VBox vbox = new VBox(10, new Label("Edit " + classNames.get(classList.indexOf(activeClass)) + " attendance records for " + student.getFirstName() + " " + student.getLastName()), attendanceTable, closeButton);
        VBox vbox = new VBox(10, new Label("Edit " + classList.get(getCourseIndex(activeClass)).getCourseSec() + " attendance records for " + student.getFirstName() + " " + student.getLastName()), attendanceTable, closeButton);

        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox);
        dialogStage.setScene(scene);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.showAndWait();

        // Refresh the student table after closing the dialog
        refreshTables();
    }

    // Find the first available quiestionID under the current classID and then add a new question to the database with that questionID
    private int addFirstEmptyQuizQuestion() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                PreparedStatement stmt = conn.prepareStatement("SELECT questionID FROM quizquestions WHERE classID = ?")) {
            stmt.setInt(1, activeClass);
            ResultSet rs = stmt.executeQuery();
            int questionID = 1;
            while (rs.next()) {
                if (rs.getInt("questionID") != questionID) {
                    break;
                }
                questionID++;
            }
            try (PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO quizquestions (questionID, classID, questionContent, answer1, answer2, answer3, answer4, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                stmt2.setInt(1, questionID);
                stmt2.setInt(2, activeClass);
                stmt2.setString(3, "New Question");
                stmt2.setString(4, "Answer 1");
                stmt2.setString(5, "Answer 2");
                stmt2.setString(6, "Answer 3");
                stmt2.setString(7, "Answer 4");
                stmt2.setInt(8, 1);
                stmt2.executeUpdate();
            }

            return questionID;
        } catch (SQLException ex) {
            showErrorDialog("Failed to add new quiz question: " + ex.getMessage());
        }
        return -1;
    }

    // opens the dialog to modify a quiz question
    private void openQuizQuestionModification(Quiz quiz) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Quiz Question");

        // Create a form to edit the quiz question
        // Question Content: (int) quiestionID, (int) classID, questionContent, answer1, answer2, answer3, answer4, correct_answer
        TextField questionContentField = new TextField(quiz.getQuestionContent());
        TextField answer1Field = new TextField(quiz.getAnswers().get(0));
        TextField answer2Field = new TextField(quiz.getAnswers().get(1));
        TextField answer3Field = new TextField(quiz.getAnswers().get(2));
        TextField answer4Field = new TextField(quiz.getAnswers().get(3));

        // Drop down menu for correct answer. List answers but return the index of the correct answer
        ComboBox<String> correctAnswerField = new ComboBox<>();
        correctAnswerField.getItems().addAll(quiz.getAnswers());
        correctAnswerField.setValue(quiz.getAnswers().get(quiz.getCorrectAnswer()-1));

        // Add text change listeners to the answer fields
        answer1Field.textProperty().addListener((observable, oldValue, newValue) -> correctAnswerField.getItems().set(0, newValue));
        answer2Field.textProperty().addListener((observable, oldValue, newValue) -> correctAnswerField.getItems().set(1, newValue));
        answer3Field.textProperty().addListener((observable, oldValue, newValue) -> correctAnswerField.getItems().set(2, newValue));
        answer4Field.textProperty().addListener((observable, oldValue, newValue) -> correctAnswerField.getItems().set(3, newValue));

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            // Update the quiz question in the database
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement stmt = conn.prepareStatement("UPDATE quizquestions SET questionContent = ?, answer1 = ?, answer2 = ?, answer3 = ?, answer4 = ?, correct_answer = ? WHERE questionID = ? AND classID = ?")) {
                stmt.setString(1, questionContentField.getText());
                stmt.setString(2, answer1Field.getText());
                stmt.setString(3, answer2Field.getText());
                stmt.setString(4, answer3Field.getText());
                stmt.setString(5, answer4Field.getText());
                stmt.setInt(6, correctAnswerField.getItems().indexOf(correctAnswerField.getValue())+1);
                stmt.setInt(7, Integer.parseInt(quiz.getQuestionNum()));
                stmt.setInt(8, activeClass);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                showErrorDialog("Failed to update quiz question: " + ex.getMessage());
            }
            dialogStage.close();
            refreshTables();
        });

        Button doNotSaveButton = new Button("Close");
        doNotSaveButton.setOnAction(e -> dialogStage.close());

        Button deleteQuestionButton = new Button("Delete Question");
        deleteQuestionButton.setOnAction(e -> {
            // Show an alert asking is the user is sure they want to delete the question
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Quiz Question");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this quiz question?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Delete the quiz question from the database
                try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM quizquestions WHERE questionID = ? AND classID = ?")) {
                    stmt.setInt(1, Integer.parseInt(quiz.getQuestionNum()));
                    stmt.setInt(2, activeClass);
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    showErrorDialog("Failed to delete quiz question: " + ex.getMessage());
                }
                dialogStage.close();
                refreshTables();
            }
        });

        // Box to hold buttons horizontally
        HBox buttonBox = new HBox(10, saveButton, doNotSaveButton, deleteQuestionButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Box to hold the form vertically
        VBox vbox = new VBox(10, new Label("Edit Quiz Question"), questionContentField, answer1Field, answer2Field, answer3Field, answer4Field, correctAnswerField, buttonBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox);
        dialogStage.setScene(scene);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.showAndWait();
    }

    // Function to load the attendance history of a student
    private void loadAttendanceHistory(TableView<AttendanceRecord> table, String studentId) {
        ObservableList<AttendanceRecord> attendanceRecords = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement stmt = conn.prepareStatement("SELECT dateAttended, status FROM studentAttendance WHERE studentID = ? AND classID = ? ORDER BY dateAttended DESC")) {
            stmt.setString(1, studentId);
            stmt.setInt(2, activeClass);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                attendanceRecords.add(new AttendanceRecord(rs.getDate("dateAttended").toString(), rs.getString("status")));
            }
        } catch (SQLException e) {
            showErrorDialog("Failed to load attendance history: " + e.getMessage());
        }
        table.setItems(attendanceRecords);
    }

    // Function to update the attendance record of a student
    private void updateAttendanceRecord(String studentId, String date, String status) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement stmt = conn.prepareStatement("UPDATE studentAttendance SET status = ? WHERE studentID = ? AND dateAttended = ?")) {
            stmt.setString(1, status);
            stmt.setString(2, studentId);
            stmt.setString(3, date);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showErrorDialog("Failed to update attendance record: " + e.getMessage());
        }
    }

    // Reset the attendance of all students to absent
    private void resetAttendance() {
        // add an alert to confirm the user wants to reset attendance
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Attendance");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to reset attendance? This will set all students to absent.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK) {
            return;
        }

        for (Student s : studentList) {
            s.setStatus("Absent");
        }

        // change all students to absent in the database
        for (Student s : studentList) {
            changeStudentAttendance(Integer.parseInt(s.getStudentId()), s.getStatus());
        }

        studentTableView.refresh();
        updateStudnetAnalytics();
    }

    // Add this method to setup the click event for the student table
    private void setupStudentTableClickEvent() {
        studentTableView.setOnMouseClicked(this::clickStudent);
        studentTableView.setOnContextMenuRequested(this::rightClickStudent);
    }

    // Add this method to setup the click event for the quiz question table
    private void setupQuizQuestionTableClickEvent() {
        // quizQuestionTableView.setOnMouseClicked(this::clickQuizQuestion);
        quizQuestionTableView.setOnContextMenuRequested(this::rightClickQuizQuestion);
    }

    // Function to save the current table to a CSV file
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

    // Function to lock in the attendance and update the database
    private void lockInAttendance() {
        // Prompt user for "Are you sure you want to lock in attendance?"
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Lock In Attendance");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to lock in attendance?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK) {
            return;
        }
        // @TODO: Update the database with the attendance status
        // for now we will just print the attendance status to the console
        for (Student s : studentList) {
            uploadStudentAttendance(Integer.parseInt(s.getStudentId()), s.getStatus(), activeClass);
        }

        // Update the student attendance in the database
        System.out.println("Attendance locked in");
        refreshTables();
    }

    // Function to upsert the student attendance to the database
    private boolean uploadStudentAttendance(int studentID, String status, int classID) {
        // Check if student is in the class in student_in_classes table with the unique keys studentID, classID
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM student_in_classes WHERE studentID = ? AND classID = ?")) {
            stmt.setInt(1, studentID);
            stmt.setInt(2, classID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                showErrorDialog("Student is not in the class");
                return false;
            }
        } catch (SQLException e) {
            showErrorDialog("Failed to check if student is in the class: " + e.getMessage());
            return false;
        }
        // If status is 1 (present) or 0 (absent)
        // Insert the student into the studentAttendance table. Use studentID, classID, and status
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            // PreparedStatement stmt = conn.prepareStatement("INSERT INTO studentAttendance (studentID, classID, status, dateAttended) VALUES (?, ?, ?, CURDATE())")) {
            // change to upsert
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO studentAttendance (studentID, classID, status, dateAttended) VALUES (?, ?, ?, CURDATE()) ON DUPLICATE KEY UPDATE status = VALUES(status)")) {
            // statement with tomorrow date for demo purposes
            // PreparedStatement stmt = conn.prepareStatement("INSERT INTO studentAttendance (studentID, classID, status, dateAttended) VALUES (?, ?, ?, DATE_ADD(CURDATE(), INTERVAL 1 DAY)) ON DUPLICATE KEY UPDATE status = VALUES(status)")) {
            stmt.setInt(1, studentID);
            stmt.setInt(2, classID);
            if ("Present".equals(status)) {
                stmt.setString(3, "Present");
            } else {
                stmt.setString(3, "Absent");
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            showErrorDialog("Failed to upload student attendance: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    // changes the student attendance in "student_in_classes" table ""
    private void changeStudentAttendance(int studentID, String status) {
        // TODO: Update the database with the attendance status
        System.out.println("Student ID: " + studentID + " Status: " + status);
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("UPDATE student_in_classes SET isPresent = ? WHERE studentID = ? AND classID = ?")) {
            if ("Present".equals(status)) {
                stmt.setInt(1, 1);
            } else {
                stmt.setInt(1, 0);
            }
            stmt.setInt(2, studentID);
            stmt.setInt(3, activeClass);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showErrorDialog("Failed to update student attendance: " + e.getMessage());
        }
        System.out.println("Student attendance updated");
    }

    // Function to add a new class
    public static int addNewClass(String courseSec) {
        System.out.println("Adding new class: " + courseSec);
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO class (`course-sec`) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, courseSec);
            stmt.executeUpdate();
            // Return the class ID of the newly added class
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException e) {
                System.out.println("Failed to get the class ID of the newly added class: " + e.getMessage());
                return -1;
            }

            return -1;
        } catch (SQLException e) {
            System.out.println("Failed to add new class: " + e.getMessage());
            return -1;
        }
    }

    // Function to get the index of the classID in the classList
    int getCourseIndex(int courseID) {
        for (int i = 0; i < classList.size(); i++) {
            if (classList.get(i).getCourseSecID() == courseID) {
                return i;
            }
        }
        return -1;
    }

    // Return statement for the requested classID
    public static courseSec getClassInfo(int courseID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM class WHERE `class-id` = ?")) {
            stmt.setInt(1, courseID);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Class not found");
                return null;
            }
            // courseSec(String courseSec, int courseID, String startDate, String endDate, String quizPass, boolean quizOpen)
            courseSec classInfo = new courseSec(rs.getString("course-sec"), rs.getInt("class-id"), rs.getDate("startDate"), rs.getDate("endDate"), rs.getString("quizPassword"), rs.getBoolean("quiz_is_open"));
            return classInfo;
        } catch (SQLException e) {
            System.out.println("Failed to get the course section: " + e.getMessage());
        }
        return null;
    }

    // Update statement for the requested classID
    public static void updateClass(int classID, String startDate, String endDate, String quizPass, boolean quizOpen){
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("UPDATE class SET `startDate` = ?, `endDate` = ?, `quizPassword` = ?, `quiz_is_open` = ? WHERE `class-id` = ?")) {
            // convert from MM/DD/YYYY
            // startDate = startDate.substring(6, 10) + "-" + startDate.substring(0, 2) + "-" + startDate.substring(3, 5);
            stmt.setDate(1, Date.valueOf(startDate));
            // endDate = endDate.substring(6, 10) + "-" + endDate.substring(0, 2) + "-" + endDate.substring(3, 5);
            stmt.setDate(2, Date.valueOf(endDate));
            stmt.setString(3, quizPass);
            stmt.setInt(4, quizOpen ? 1 : 0);
            stmt.setInt(5, classID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update the class: " + e.getMessage());
        }
    }

    // Delete statement for the requested classID
    static void deleteClass(int courseID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM class WHERE `class-id` = ?")) {
            stmt.setInt(1, courseID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete the class: " + e.getMessage());
        }
    }

    // Check if the String is in the correct format for a date in SQL (YYYY-MM-DD)
    static boolean isValidSQLDate(String date) {
        try {
            Date.valueOf(date);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Class to hold the attendanceRecord to make the table easier to load and update
    public class AttendanceRecord {
        private final SimpleStringProperty date;
        private final SimpleStringProperty status;

        public AttendanceRecord(String date, String status) {
            this.date = new SimpleStringProperty(date);
            this.status = new SimpleStringProperty(status);
        }

        public String getDate() {
            return date.get();
        }

        public void setDate(String date) {
            this.date.set(date);
        }

        public String getStatus() {
            return status.get();
        }

        public void setStatus(String status) {
            this.status.set(status);
        }
    }
}