package com.attendance.servlets;

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
        // list of students
        List<Student> studentList = new ArrayList<>();
        
        // Change to allow user to input CSV file path (filechooser)
        String csvPath = "";
        List<List<String>> records = csvToList(csvPath);
        String csvType = identifyCSV(records.get(0));
        printListofLists(records);

        // Path to CSV files for testing (hardcoded)
        // String csvFilePatheLearning = "src/main/Sample-CS1000-eLearning.csv";
        // String csvFilePathCooursebook = "src/main/Sample-CS1000-Coursebook.csv";

        // Read CSV file into a list of arrays (for testing hardcoded paths)
        // List<List<String>> eLearningRecords = csvToList(csvFilePatheLearning);
        // List<List<String>> coursebookRecords = csvToList(csvFilePathCooursebook);

        // Identify the type of CSV file (eLearning or Coursebook) for hardcoded paths
        // String csvType1 = identifyCSV(eLearningRecords.get(0));
        // String csvType2 = identifyCSV(coursebookRecords.get(0));

        // Print the list of arrays (for testing hardcoded paths)
        // printListofLists(eLearningRecords);
        // printListofLists(coursebookRecords);

        // Parse each record into a student object (from elearning CSV) (for hardcoded paths)
        // for (List<String> record : eLearningRecords.subList(1, eLearningRecords.size())) {
        //     Student student = parseStudent(record, csvType1);
        //     if (student != null) {
        //         studentList.add(student);
        //     }
        // }

        // Parse each record into a student object (from coursebook CSV) (for hardcoded paths)
        // for (List<String> record : coursebookRecords.subList(1, coursebookRecords.size())) {
        //     Student student = parseStudent(record, csvType2);
        //     if (student != null) {
        //         studentList.add(student);
        //     }
        // }

        // Parse each record into a student object
        for (List<String> record : records.subList(1, records.size())) {
            Student student = parseStudent(record, csvType);
            if (student != null) {
                studentList.add(student);
            }
        }

        // Print test students
        for (Student s : studentList) {
            System.out.println(s);
        }

        Connection cnSQL;
        String strSql;
        Statement stmtSQL;
  
        String strdata;


        try
        {        
            // Changed "company" to attendancesystem
            cnSQL=DriverManager.getConnection("jdbc:mysql://localhost:3306/attendancedatabase","root","MDouglas2kay");
    
            stmtSQL = cnSQL.createStatement();

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
        }   
    }

    private static List<List<String>> csvToList(String filepath) {
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
    private static String identifyCSV(List<String> header) {
        int length = header.size();
        int coursebookLength = 12;
        int eLearningLength = 6;
        if (length == coursebookLength) {
            return "Coursebook";
        } else if (length == eLearningLength) {
            return "eLearning";
        } else {
            return "Unknown";
        }
    }

    private static Student parseStudent(List<String> values, String csvType) {
        if (csvType.equals("Coursebook")) {
            return parseCoursebookStudent(values);
        } else if (csvType.equals("eLearning")) {
            return parseeLearningStudent(values);
        } else {
            return null;
        }
    }

    private static Student parseCoursebookStudent(List<String> values) {
        String studentId = values.get(1);
        String firstName = values.get(2);
        String lastName = values.get(4);
        return new Student(firstName, lastName, studentId, "Present");
    }

    private static Student parseeLearningStudent(List<String> values) {
        String studentId = values.get(3);
        String firstName = values.get(1);
        String lastName = values.get(0);

        return new Student(firstName, lastName, studentId, "Present");
    }

    private static Boolean addStudentToDB(Student s, Connection cnSQL) {
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
                return false;
            }

            preparedStatement.executeUpdate();
            System.out.println("Successfully added " + s + " to database");
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to add student to database: " + e.getMessage());
            return false;
        }
    }

    private static Boolean addMultipleStudentsToDB(List<Student> students, Connection cnSQL) {
        for (Student s : students) {
            if (!addStudentToDB(s, cnSQL)) {
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

