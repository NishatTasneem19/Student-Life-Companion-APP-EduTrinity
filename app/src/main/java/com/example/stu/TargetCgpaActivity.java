package com.example.stu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class TargetCgpaActivity extends AppCompatActivity {

    private EditText etTargetCGPA;
    private Button btnCalculate;
    private TextView tvResult;

    private double[] pastGPA;
    private double[] pastCredits;
    private int totalSemesters;

    private Button btnBack;
    private Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_cgpaactivity);




        etTargetCGPA = findViewById(R.id.etTargetCGPA);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);

        // Receive data from CGPA Calculator
        String gpaStr = getIntent().getStringExtra("SEM_GPA");
        String creditStr = getIntent().getStringExtra("SEM_CREDITS");
        totalSemesters = getIntent().getIntExtra("TOTAL_SEMESTERS", 0);

        if (gpaStr == null || creditStr == null || totalSemesters == 0) {
            tvResult.setText("No GPA data found!");
            btnCalculate.setEnabled(false);
            return;
        }

        String[] gpaArr = gpaStr.split(",");
        String[] creditArr = creditStr.split(",");

        pastGPA = new double[totalSemesters];
        pastCredits = new double[totalSemesters];

        for (int i = 0; i < totalSemesters; i++) {
            try {
                pastGPA[i] = Double.parseDouble(gpaArr[i]);
                pastCredits[i] = Double.parseDouble(creditArr[i]);
            } catch (Exception e) {
                pastGPA[i] = 0.0;
                pastCredits[i] = 0.0;
            }
        }

        btnCalculate.setOnClickListener(v -> handleCalculate());
    }

    private void handleCalculate() {
        String targetCgpaStr = etTargetCGPA.getText().toString().trim();

        if (targetCgpaStr.isEmpty()) {
            tvResult.setText("Please enter target CGPA");
            return;
        }

        double targetCGPA;

        try {
            targetCGPA = Double.parseDouble(targetCgpaStr);
        } catch (NumberFormatException e) {
            tvResult.setText("Invalid number input");
            return;
        }

        if (targetCGPA < 0.0 || targetCGPA > 4.0) {
            tvResult.setText("Target GPA must be between 0.0 and 4.0");
            return;
        }

        calculateTargetGPA(targetCGPA);
    }

    private void calculateTargetGPA(double targetCGPA) {
        // Sum up total completed points and credits
        double totalPoints = 0;
        double totalCredits = 0;
        for (int i = 0; i < totalSemesters; i++) {
            totalPoints += pastGPA[i] * pastCredits[i];
            totalCredits += pastCredits[i];
        }

        // Assume total program credits (fixed, e.g., 140) and calculate remaining credits
        double totalProgramCredits = 140; // You can also pass this from previous activity
        double remainingCredits = totalProgramCredits - totalCredits;

        if (remainingCredits <= 0) {
            tvResult.setText("All credits completed. Target CGPA already achieved.");
            return;
        }

        double requiredGPA = (targetCGPA * totalProgramCredits - totalPoints) / remainingCredits;
        if (requiredGPA > 4.0) requiredGPA = 4.0;
        if (requiredGPA < 0.0) requiredGPA = 0.0;

        tvResult.setText(String.format("Required GPA for remaining %.2f credits: %.2f", remainingCredits, requiredGPA));
    }
}
