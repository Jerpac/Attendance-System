package com.app;

import java.sql.*;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        System.out.println("It works here");
        Connection cnSQL;
        String strSql;
        Statement stmtSQL;
        String strdata;

        try {
            cnSQL = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/attendancesystem?user=root",
                    "root", "beckergoat");

            stmtSQL = cnSQL.createStatement();

            // String insertQuery = "INSERT INTO professor (professorID, professorName)
            // VALUES ('12345', 'Anakin Ha')";

            // PreparedStatement preparedStatement = cnSQL.prepareStatement(insertQuery);
            String selectQuery = "SELECT * FROM student";
            PreparedStatement preparedStatement = cnSQL.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int studentID = resultSet.getInt("studentID");
                String studentName = resultSet.getString("studentFName");
                // You can access other columns similarly

                System.out.println("Student ID: " + studentID + ", Student Name: " + studentName);
                // Print other information as needed
            }

            cnSQL.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
