package com.attendance.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.List;

/**
 * Servlet implementation class QuizSubmitter
 */
public class QuizSubmitter extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QuizSubmitter() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Enumeration<String> parameterNames = request.getParameterNames();
		HttpSession session = request.getSession();
		DatabaseConnection conn = new DatabaseConnection();
		Connection connection = conn.getConnection();
		PreparedStatement statement;
		PreparedStatement statement2;
		int studentID = (int) session.getAttribute("studentId");
		int classID = (int) session.getAttribute("classId");
		
		String ipAddress = request.getParameter("ipAddress");
		System.out.println(ipAddress);
		
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		
		// grab all the data from the form for DB insertion
		while (parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			int questionId = Integer.parseInt(paramName); // grab the question ID
			String paramValue = request.getParameter(paramName);
			int studentAnswer = Integer.parseInt(paramValue); // grab the student's answer to the question
			
			try {
				// update the studentresponses table with the relevant information
				statement = connection.prepareStatement("INSERT INTO studentresponses (studentID, questionID, response, timeOfSubmission) VALUES (?, ?, ?, ?)");
				statement.setInt(1, studentID);
				statement.setInt(2, questionId);
				statement.setInt(3, studentAnswer);
				statement.setTimestamp(4, timestamp);
				statement.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
			
			try {
				// update student_in_classes table to mark student as present
				statement2 = connection.prepareStatement("UPDATE student_in_classes SET isPresent = ? WHERE studentID = ? AND classID = ?");
				statement2.setBoolean(1, true);
				statement2.setInt(2,  studentID);
				statement2.setInt(3, classID);
				statement2.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*try {
				statement2 = connection.prepareStatement("UPDATE student SET lastUsedIPAddress = ? WHERE studentID = ?");
				statement2.setString(1, ipAddress);
				statement2.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			// code for up dating ip address, could not get to work
			
		}
		System.out.println("Data sucessfully entered.");
		response.sendRedirect("quiz-complete.html");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
