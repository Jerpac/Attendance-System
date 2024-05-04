public class Student {
    private String firstName;
    private String lastName;
    private String studentId;
    private String status; // "Present" or "Absent"

    public Student(String firstName, String lastName, String studentId, String status) {
        this.firstName = new String(firstName);
        this.lastName = new String(lastName);
        this.studentId = new String(studentId);
        this.status = new String(status);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = new String(status);
    }

    public String toString() {
        return "Student{" +
                "firstName=" + firstName +
                ", lastName=" + lastName +
                ", studentId=" + studentId +
                ", status=" + status +
                '}';
    }
}
