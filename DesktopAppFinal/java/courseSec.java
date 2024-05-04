import java.sql.Date;

import javafx.beans.property.SimpleStringProperty;
    
public class courseSec {
    private final SimpleStringProperty courseSec;
    private final int courseID;
    private String startDate;
    private String endDate;
    private String quizPass;
    private boolean quizOpen;

    public courseSec(String courseSec, int courseID) {
        this.courseSec = new SimpleStringProperty(courseSec);
        this.courseID = courseID;
        this.startDate = "";
        this.endDate = "";
        this.quizPass = "";
        this.quizOpen = false;
    }

    public courseSec(String courseSec, int courseID, Date date, Date date2, String quizPass, boolean quizOpen) {
        this.courseSec = new SimpleStringProperty(courseSec);
        this.courseID = courseID;
        if (date == null) {
            this.startDate = "";
        } else {
            this.startDate = date.toString();
        }
        if (date2 == null) {
            this.endDate = "";
        } else {
            this.endDate = date2.toString();
        }
        if (quizPass == null) {
            this.quizPass = "";
        } else {
            this.quizPass = quizPass;
        }
        if (quizOpen == true) {
            this.quizOpen = true;
        } else {
            this.quizOpen = false;
        }
    }

    public String getCourseSec() {
        return courseSec.get();
    }

    public int getCourseSecID() {
        return courseID;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getQuizPass() {
        return quizPass;
    }

    public boolean isQuizOpen() {
        return quizOpen;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setQuizPass(String quizPass) {
        this.quizPass = quizPass;
    }

    public void setQuizOpen(boolean quizOpen) {
        this.quizOpen = quizOpen;
    }
}