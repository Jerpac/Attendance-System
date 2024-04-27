import java.sql.*;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.opencsv.CSVReader;
import java.util.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CSVtoDB 
{
    public static void main(String[] args) 
    {
        // Change to allow user to input CSV file path (filechooser)
        String csvPath = "src/main/quiz question csv.csv";
        // String csvPath = "src/main/Sample-CS1000-Coursebook.csv";
        

        Connection cnSQL;

        try
        {        
            // Changed "company" to attendancesystem
            cnSQL=DriverManager.getConnection("jdbc:mysql://localhost:3306/attendancesystem?characterEncoding=utf8","root","4445");

            // Insert test students into database
            if (fullUploadFromPath(csvPath, cnSQL)) {
                System.out.println("Successfully added students to database");
            } else {
                System.out.println("Failed to add students to database");
            }
        
            // Close the connection
            cnSQL.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public static boolean fullUploadFromPath(String filepath, Connection cnSQL) {
        List<List<String>> records = csvToList(filepath);
        String csvType = identifyCSV(records.get(0)).toLowerCase();
        if (csvType.equals("coursebook") || csvType.equals("eLearning")) {
            List<Student> studentList = new ArrayList<>();
            for (List<String> record : records.subList(1, records.size())) {
                Student student = parseStudent(record, csvType);
                if (student != null) {
                    studentList.add(student);
                }
            }

            if (uploadStudentsFromList(studentList, cnSQL)) {
                return true;
            } else {
                return false;
            }
        } else if (csvType.equals("quizquestions")) {
            List<Quiz> quizList = new ArrayList<>();
            for (List<String> record : records.subList(1, records.size())) {
                Quiz quiz = parseQuizQuestions(record);
                if (quiz != null) {
                    quizList.add(quiz);
                }
            }

            if (uploadQuizQuestionsFromList(quizList, cnSQL)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    static boolean uploadStudentsFromList(List<Student> studentList, Connection cnSQL) {
        try
        {        
            // Changed to attendancesystem
            cnSQL=DriverManager.getConnection("jdbc:mysql://localhost:3306/attendancesystem?characterEncoding=utf8","root","4445");

            // Insert test students into database
            if (addMultipleStudentsToDB(studentList, cnSQL)) {
                System.out.println("Successfully added students to database");
            } else {
                System.out.println("Failed to add students to database");
            }
        
            // Close the connection
            cnSQL.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex.getMessage());
            return false;
        }
        return true;
    }

    static boolean uploadQuizQuestionsFromList(List<Quiz> quizQuestionList, Connection cnSQL) {
        try
        {        
            // Changed to attendancesystem
            cnSQL=DriverManager.getConnection("jdbc:mysql://localhost:3306/attendancesystem?characterEncoding=utf8","root","4445");

            // Insert test students into database
            if (addMultipleQuizQuestionsToDB(quizQuestionList, cnSQL)) {
                System.out.println("Successfully added quizzes to database");
            } else {
                System.out.println("Failed to add quizzes to database");
            }
        
            // Close the connection
            cnSQL.close();
        }
        catch (Exception ex)
        {
            System.out.println("Error: " + ex.getMessage());
            return false;
        }
        return true;
    }

    public static List<List<String>> csvToList(String filepath) {
        // Create an object of file reader
        FileReader filereader;
        try {
            filereader = new FileReader(new File(filepath));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        // Create an object of CSVReader that splits by \t
        CSVReader csvReader = new CSVReader(filereader, '\t');

        List<List<String>> records = new ArrayList<>();

        // Print each record and parse each line as a student
        String[] nextRecord;
        try {
            // Loop through each row
            while ((nextRecord = csvReader.readNext()) != null) {
                List<String> record = new ArrayList<>();
                // Loop through each cell in the row
                for (String cell : nextRecord) {
                    // if cell contains \t, is a coursebook record
                    if(cell.contains("\t")) {
                        // split cell by \t
                        String[] values = cell.split("\t");
                        for (String value : values) {
                            value = value.replaceAll("^\"|\"$", "");
                            record.add(value);
                        }
                        break;
                    // if cell doesn't contain \t, is an eLearning record
                    } else {
                        // replace quotes with empty string
                        record.add(cell.replaceAll("\"", ""));
                    }
                }
                // Check if the record is only 1 cell, is a non-value header
                if (record.size() == 1) {
                    continue;
                }
                records.add(record);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return records;
    }

    // Identify the type of CSV file by how many columns it has in the header
    // Coursebook has 12 columns, eLearning has 6 columns
    public static String identifyCSV(List<String> header) {
        int length = header.size();
        int coursebookLength = 12;
        int eLearningLength = 6;
        if (length == coursebookLength) {
            return "coursebook";
        } else if (length == eLearningLength) {
            return "elearning";
        } else if (header.get(0).toLowerCase().contains("question")) {
                return "quizquestions";
        } else {
            return "Unknown";
        }
    }

    public static Student parseStudent(List<String> values, String csvType) {
        if (csvType.toLowerCase().equals("coursebook")) {
            return parseCoursebookStudent(values);
        } else if (csvType.toLowerCase().equals("elearning")) {
            return parseeLearningStudent(values);
        } else {
            return null;
        }
    }

    public static Quiz parseQuizQuestions(List<String> values) {
        String questionNumber = values.get(0);
        String question = values.get(1);
        // loop through next 4 rows and get the 4 answers
        List<String> answers = new ArrayList<>();
        for (int i = 2; i < 6; i++) {
            answers.add(values.get(i));
        }
        String correctAnswer = values.get(6);

        return new Quiz(questionNumber, question, answers, correctAnswer);
    }

    private static Student parseCoursebookStudent(List<String> values) {
        String studentId = values.get(1);
        String firstName = values.get(2);
        String lastName = values.get(4);
        return new Student(firstName, lastName, studentId, "Absent");
    }

    private static Student parseeLearningStudent(List<String> values) {
        String studentId = values.get(3);
        String firstName = values.get(1);
        String lastName = values.get(0);

        return new Student(firstName, lastName, studentId, "Absent");
    }

    public static Boolean addStudentToDB(Student s, Connection cnSQL) {
        System.out.println("Adding student " + s + " to database");
        int studentId = Integer.parseInt(s.getStudentId());
        String studentFirstName = s.getFirstName();
        String studentLastName = s.getLastName();

        try {
            String insertQuery = "INSERT INTO student (studentID, studentFName, studentLName) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = cnSQL.prepareStatement(insertQuery);
            preparedStatement.setInt(1, studentId);
            preparedStatement.setString(2, studentFirstName);
            preparedStatement.setString(3, studentLastName);
            // Check if student is already in database through studentID
            String checkQuery = "SELECT * FROM student WHERE studentID = ?";
            PreparedStatement checkStatement = cnSQL.prepareStatement(checkQuery);
            checkStatement.setInt(1, studentId);
            ResultSet rs = checkStatement.executeQuery();
            if (rs.next()) {
                System.out.println("Student " + s + " already exists in database");
                // Update student in database
                // return updateStudentInDB(s, cnSQL);
                return true;
            }

            preparedStatement.executeUpdate();
            System.out.println("Successfully added " + s + " to database");
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to add student to database: " + e.getMessage());
            return false;
        }
    }

    public static Boolean updateStudentInDB(Student s, Connection cnSQL) {
        try {
            String updateQuery = "UPDATE student SET studentFName = ?, studentLName = ? WHERE studentID = ?";
            PreparedStatement updateStatement = cnSQL.prepareStatement(updateQuery);
            updateStatement.setString(1, s.getFirstName());
            updateStatement.setString(2, s.getLastName());
            updateStatement.setInt(3, Integer.parseInt(s.getStudentId()));
            updateStatement.executeUpdate();
            System.out.println("Successfully updated " + s + " in database");
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to update student in database: " + e.getMessage());
            return false;
        }
    }

    public static Boolean addQuizQuestionToDB(Quiz q, Connection cnSQL) {
        String questionNumber = q.getQuestionNum();
        String question = q.getQuestionContent();
        List<String> answers = q.getAnswers();
        String correctAnswer = q.getCorrectAnswer();

        try {
            String insertQuery = "INSERT INTO quizquestions (questionID, questionContent, answer1, answer2, answer3, answer4, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = cnSQL.prepareStatement(insertQuery);
            preparedStatement.setInt(1, Integer.parseInt(questionNumber));
            preparedStatement.setString(2, question);
            preparedStatement.setString(3, answers.get(0));
            preparedStatement.setString(4, answers.get(1));
            preparedStatement.setString(5, answers.get(2));
            preparedStatement.setString(6, answers.get(3));
            preparedStatement.setString(7, correctAnswer);
            // Check if question is already in database through questionID
            String checkQuery = "SELECT * FROM quizquestions WHERE questionID = ?";
            PreparedStatement checkStatement = cnSQL.prepareStatement(checkQuery);
            checkStatement.setInt(1, Integer.parseInt(questionNumber));
            ResultSet rs = checkStatement.executeQuery();
            if (rs.next()) {
                System.out.println("Question " + q + " already exists in database");
                // updateQuizQuestionInDB(q, cnSQL);
            }
            preparedStatement.executeUpdate();
            System.out.println("Successfully added " + q + " to database");
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to add quiz to database: " + e.getMessage());
            return false;
        }
    }

    public static Boolean updateQuizQuestionInDB(Quiz q, Connection cnSQL) {
        try {
            String updateQuery = "UPDATE quizquestions SET questionContent = ?, answer1 = ?, answer2 = ?, answer3 = ?, answer4 = ?, correct_answer = ? WHERE questionID = ?";
            PreparedStatement updateStatement = cnSQL.prepareStatement(updateQuery);
            updateStatement.setString(1, q.getQuestionContent());
            updateStatement.setString(2, q.getAnswers().get(0));
            updateStatement.setString(3, q.getAnswers().get(1));
            updateStatement.setString(4, q.getAnswers().get(2));
            updateStatement.setString(5, q.getAnswers().get(3));
            updateStatement.setString(6, q.getCorrectAnswer());
            updateStatement.setInt(7, Integer.parseInt(q.getQuestionNum()));
            updateStatement.executeUpdate();
            System.out.println("Successfully updated " + q + " in database");
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to update quiz in database: " + e.getMessage());
            return false;
        }
    }

    public static Boolean addMultipleStudentsToDB(List<Student> students, Connection cnSQL) {
        for (Student s : students) {
            if (!addStudentToDB(s, cnSQL)) {
                return false;
            }
        }
        return true;
    }

    public static Boolean addMultipleQuizQuestionsToDB(List<Quiz> quizQuestions, Connection cnSQL) {
        for (Quiz q : quizQuestions) {
            if (!addQuizQuestionToDB(q, cnSQL)) {
                return false;
            }
        }
        return true;
    }

    private static void printListofLists(List<List<String>> records) {
        for (List<String> record : records) {
            for (String cell : record) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }
}
