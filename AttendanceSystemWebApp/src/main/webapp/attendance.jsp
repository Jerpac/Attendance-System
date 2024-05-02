<%@ page import="com.attendance.servlets.*"%>
<%@ page import="java.util.List"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Attendance</title>
<link rel="stylesheet" href="attendance.css">
</head>

<body>
	<header>
		<div class="utd-logo-container">
			<img class="utd-logo-header" src="UT Dallas_White.png">
		</div>
		<div class="cole-title-container">
			<p class="cole-title">Professor John Cole Attendance Form</p>
		</div>
	</header>

	<main>
		<section class="quiz-questions-list">
			<form action="SubmitAnswers">
				<%
				List<QuizQuestion> selectedQuestions = (List<QuizQuestion>) request.getAttribute("questions");
				int questionNum = 1;
				for (QuizQuestion question : selectedQuestions) {
				%>
				<div class="quiz-question">
					<p class="question-text"><%=question.getQuestion()%></p>
					<div class="answer-list">
						<input class="radio__input" type="radio" value="answer1"
							name="radioButton<%=questionNum%>"
							id="radio1_<%=questionNum%>"> <label
							class="radio__label" for="radio1_<%=questionNum%>"><%=question.getAnswer1()%></label>

						<input class="radio__input" type="radio" value="answer2"
							name="radioButton<%=questionNum%>"
							id="radio2_<%=questionNum%>"> <label
							class="radio__label" for="radio2_<%=questionNum%>"><%=question.getAnswer2()%></label>

						<input class="radio__input" type="radio" value="answer3"
							name="radioButton<%=questionNum%>"
							id="radio3_<%=questionNum%>"> <label
							class="radio__label" for="radio3_<%=questionNum%>"><%=question.getAnswer3()%></label>

						<input class="radio__input" type="radio" value="answer4"
							name="radioButton<%=questionNum%>"
							id="radio4_<%=questionNum%>"> <label
							class="radio__label" for="radio4_<%=questionNum%>"><%=question.getAnswer4()%></label>
					</div>
				</div>
				<%
				questionNum++;
				}
				%>
				<div class="submit-button-container">
					<button class="submit-button">Submit</button>
				</div>
			</form>
		</section>
	</main>
</body>
</html>
