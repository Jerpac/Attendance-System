package com.attendance.servlets;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Student {
    private String firstName;
    private String lastName;
    private String studentId;
    private String status; // "Present" or "Absent"

    public Student(String firstName, String lastName, String studentId, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.status = status;
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
        this.status = status;
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
