package com.example.stu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class SemesterSummaryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SemesterAdapter adapter;
    List<Semester> semesterList = new ArrayList<>();


    private Button btnBack;
    private Button btnHome;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester_summary);


//BACK Button
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(SemesterSummaryActivity.this, CGPACalculatorActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

//



        recyclerView = findViewById(R.id.recyclerViewSemesters);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadSemesterData();

        adapter = new SemesterAdapter(semesterList);
        recyclerView.setAdapter(adapter);
    }

    private void loadSemesterData() {
        SharedPreferences sharedPreferences = getSharedPreferences("CGPA_PREFS", Context.MODE_PRIVATE);
        String savedData = sharedPreferences.getString("courses", "");
        if (savedData.isEmpty()) return;

        Map<Integer, List<Course>> semesterCourses = new HashMap<>();
        String[] entries = savedData.split(";");
        for (String entry : entries) {
            String[] parts = entry.split(",");
            if (parts.length < 4) continue;

            String name = parts[0];
            double credits = Double.parseDouble(parts[1]);
            String grade = parts[2];
            int semester = Integer.parseInt(parts[3]);

            Course course = new Course(name, credits, grade);
            semesterCourses.computeIfAbsent(semester, k -> new ArrayList<>()).add(course);
        }

        // Calculate GPAs
        double totalPointsSoFar = 0;
        double totalCreditsSoFar = 0;

        for (int sem : semesterCourses.keySet()) {
            List<Course> courses = semesterCourses.get(sem);
            double semesterPoints = 0, semesterCredits = 0;

            for (Course c : courses) {
                double gradePoint = getGradePoint(c.grade);
                semesterPoints += gradePoint * c.credits;
                semesterCredits += c.credits;
            }

            double semesterGPA = semesterCredits > 0 ? semesterPoints / semesterCredits : 0;
            totalPointsSoFar += semesterPoints;
            totalCreditsSoFar += semesterCredits;
            double cumulativeCGPA = totalCreditsSoFar > 0 ? totalPointsSoFar / totalCreditsSoFar : 0;

            semesterList.add(new Semester(
                    sem, courses, semesterGPA, cumulativeCGPA, semesterCredits, totalCreditsSoFar
            ));
        }

        // Sort by semester number
        Collections.sort(semesterList, Comparator.comparingInt(s -> s.semesterNumber));
    }

    private double getGradePoint(String grade) {
        switch (grade) {
            case "A+": case "A": return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B": return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C": return 2.0;
            case "C-": return 1.7;
            case "D+": return 1.3;
            case "D": return 1.0;
            case "F": return 0.0;
            default: return 0.0;
        }
    }
}
