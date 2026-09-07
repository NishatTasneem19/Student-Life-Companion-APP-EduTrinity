package com.example.stu;

import java.util.List;

public class Semester {
    int semesterNumber;
    List<Course> courses;
    double semesterGPA;
    double cumulativeCGPA;
    double semesterCredits;
    double totalCreditsSoFar;

    public Semester(int semesterNumber, List<Course> courses, double semesterGPA,
                    double cumulativeCGPA, double semesterCredits, double totalCreditsSoFar) {
        this.semesterNumber = semesterNumber;
        this.courses = courses;
        this.semesterGPA = semesterGPA;
        this.cumulativeCGPA = cumulativeCGPA;
        this.semesterCredits = semesterCredits;
        this.totalCreditsSoFar = totalCreditsSoFar;
    }
}
