package com.attendance.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
		List<Student> studentList = new ArrayList<>(); // list where student data will be inserted after being read from the databse
		String idString = request.getParameter("student-id");
		int id = Integer.parseInt(idString); // id entered by the user from the login form
		String password = request.getParameter("password-input"); // password entered by the user from login form
		response.setContentType("text/html");
		boolean matchFound = false;
		
		// load the driver (MAKE SURE YOU ADD THE MYSQL-CONNECTOR.JAR FILE TO YOUR CLASSPATH
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection cnSQL;
		String strSql;
		PreparedStatement statement;
		String strdata;

		// establish the connection
		try {
			cnSQL = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendancedatabase",
					"root", "MDouglas2kay");
			statement = cnSQL.prepareStatement("SELECT * FROM student WHERE studentID=?");
			statement.setInt(1, id);
			ResultSet results = statement.executeQuery();
			matchFound = results.next();
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
		}
		
		if (matchFound) {
			response.sendRedirect("attendance.jsp");
		} else {
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
