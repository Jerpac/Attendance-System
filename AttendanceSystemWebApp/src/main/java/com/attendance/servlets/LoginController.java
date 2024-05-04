package com.attendance.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class LoginController
 */
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String idString = request.getParameter("student-id");
		int id = Integer.parseInt(idString); // id entered by the user from the login form
		String password = request.getParameter("password-input"); // password entered by the user from login form
		response.setContentType("text/html");
		boolean matchFound = false;
		boolean classFound = false;
		boolean isAvailable = true;
		ResultSet studentResults = null;
		ResultSet classResults = null;
		
		// load the driver (MAKE SURE YOU ADD THE MYSQL-CONNECTOR.JAR FILE TO YOUR CLASSPATH
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection connection;
		PreparedStatement statement;

		// establish the connection
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendancesystem",
					"root", "MDouglas2kay");
			
			// query the student table to see if the student is registered in the database
			statement = connection.prepareStatement("SELECT * FROM student WHERE studentID=?");
			statement.setInt(1, id);
			studentResults = statement.executeQuery();
			matchFound = studentResults.next();
			
			// query the class table to find the correct password for the class
			statement = connection.prepareStatement("SELECT * FROM class WHERE quizPassword=?");
			statement.setString(1, password);
			classResults = statement.executeQuery();
			classFound = classResults.next();
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
		}
		
		// student has been identified and password has been authenticated
		if (matchFound && classFound) {
			HttpSession session = request.getSession();
			session.setAttribute("studentId", id);
			try {
				isAvailable = classResults.getBoolean("quiz_is_open");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// check if the quiz is available
			if (isAvailable) {
				try {
					int classId = classResults.getInt("class-id");
					session.setAttribute("classId", classId);
					response.sendRedirect("QuizController?classId=" + classId);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			// if unavailable, display error message to the user
			} else {
				request.setAttribute("errorMessage", "Quiz is unavailable.");
				RequestDispatcher dispatcher = request.getRequestDispatcher("Login.jsp");
				dispatcher.forward(request, response);
			}
			
		} else { // send back an error message to display to the user
			request.setAttribute("errorMessage", "Invalid ID or password.");
			RequestDispatcher dispatcher = request.getRequestDispatcher("Login.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
