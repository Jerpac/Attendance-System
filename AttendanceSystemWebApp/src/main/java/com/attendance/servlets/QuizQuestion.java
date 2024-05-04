package com.attendance.servlets;

public class QuizQuestion {
	private int questionId;
	private String question;
	private String answer1;
	private String answer2;
	private String answer3;
	private String answer4;
	
	public QuizQuestion(int questionId, String question, String answer1, String answer2, String answer3, String answer4) {
		this.questionId = questionId;
		this.question = question;
		this.answer1 = answer1;
		this.answer2 = answer2;
		this.answer3 = answer3;
		this.answer4 = answer4;
	}
	
	public int getQuestionId() {
		return questionId;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public String getAnswer1() {
		return answer1;
	}
	
	public String getAnswer2() {
		return answer2;
	}
	
	public String getAnswer3() {
		return answer3;
	}
	
	public String getAnswer4() {
		return answer4;
	}

}
