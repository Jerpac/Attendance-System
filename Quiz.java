import java.util.List;

public class Quiz {
    private String QuestionNum;
    private String QuestionContent;
    private List<String> Answers;
    private String CorrectAnswer;

    public Quiz(String QuestionNum, String QuestionContent, List<String> Answers, String CorrectAnswer) {
        this.QuestionNum = QuestionNum;
        this.QuestionContent = QuestionContent;
        this.Answers = Answers;
        this.CorrectAnswer = CorrectAnswer;
    }
    public Quiz(int QuestionNum, String QuestionContent, List<String> Answers, String CorrectAnswer) {
        this.QuestionNum = Integer.toString(QuestionNum);
        this.QuestionContent = QuestionContent;
        this.Answers = Answers;
        this.CorrectAnswer = CorrectAnswer;
    }

    public String getQuestionNum() {
        return QuestionNum;
    }

    public String getQuestionContent() {
        return QuestionContent;
    }

    public List<String> getAnswers() {
        return Answers;
    }

    public void addAnswer(String answer) {
        Answers.add(answer);
    }

    public void removeAnswer(String answer) {
        Answers.remove(answer);
    }

    public int getNumOfAnswers() {
        return Answers.size();
    }

    public String getCorrectAnswer() {
        return CorrectAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        CorrectAnswer = correctAnswer;
    }
    
    public String toString() {
        return "Question Number: " + QuestionNum + "\nQuestion Content: " + QuestionContent + "\nAnswers: " + Answers + "\nCorrect Answer: " + CorrectAnswer;
    }
}
