package com.attendance.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Servlet implementation class QuizController
 */
public class QuizController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QuizController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int classId = Integer.parseInt(request.getParameter("classId"));
		List<QuizQuestion> quizQuestions = getQuestions(classId);
		List<QuizQuestion> selectedQuestions = selectQuestions(quizQuestions);
		
		// send the required info back to the attendance.jsp for viewing
		request.setAttribute("questions", selectedQuestions);
		
		RequestDispatcher dispatchQuestions = request.getRequestDispatcher("attendance.jsp");
		dispatchQuestions.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private List<QuizQuestion> getQuestions(int classId) {
		ArrayList<QuizQuestion> questions = new ArrayList<>();
		ResultSet classQuestions = null;
		
		// connect to the database
		DatabaseConnection conn = new DatabaseConnection();
		Connection connection = conn.getConnection();
		
		try {
			// select all quiz questions that are specific to the student's class (using classId)
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM quizquestions WHERE classID=?");
			statement.setInt(1, classId);
			classQuestions = statement.executeQuery();
			
			// take the selected rows and create QuizQuestion objects and put them in a list
			while (classQuestions.next()) {
				int questionId = classQuestions.getInt("questionID");
				String question = classQuestions.getString("questionContent");
				String answer1 = classQuestions.getString("answer1");
				String answer2 = classQuestions.getString("answer2");
				String answer3 = classQuestions.getString("answer3");
				String answer4 = classQuestions.getString("answer4");
				QuizQuestion quizQuestion = new QuizQuestion(questionId, question, answer1, answer2, answer3, answer4);
				questions.add(quizQuestion);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		return questions;
	}
	
	// shuffles the quiz bank and then gets a sublist of three questions for randomness
	private List<QuizQuestion> selectQuestions(List<QuizQuestion> quizQuestions) {
		Collections.shuffle(quizQuestions);
		List<QuizQuestion> selectedQuestions = quizQuestions.subList(0,  3);
		return selectedQuestions;
	}

}
