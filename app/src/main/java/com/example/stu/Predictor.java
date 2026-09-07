package com.example.stu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Predictor extends AppCompatActivity {

    private EditText etSemester;
    private Button btnPredict;
    private TextView tvPrediction;
    private Button btnBack;
    private Button btnHome;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictor);



        // Back button: goes to the previous activity















        etSemester = findViewById(R.id.etSemester);
        btnPredict = findViewById(R.id.btnPredict);
        tvPrediction = findViewById(R.id.tvPrediction);

        // Receive data from CGPA Calculator
        String gpaStr = getIntent().getStringExtra("SEM_GPA");
        String creditStr = getIntent().getStringExtra("SEM_CREDITS");
        int totalSemesters = getIntent().getIntExtra("TOTAL_SEMESTERS", 0);

        if (gpaStr == null || creditStr == null || totalSemesters == 0) {
            tvPrediction.setText("No GPA data found!");
            return;
        }

        String[] gpaArr = gpaStr.split(",");
        String[] creditArr = creditStr.split(",");

        double[] pastGPA = new double[totalSemesters];
        double[] pastCredits = new double[totalSemesters];

        for (int i = 0; i < totalSemesters; i++) {
            try {
                pastGPA[i] = Double.parseDouble(gpaArr[i]);
                pastCredits[i] = Double.parseDouble(creditArr[i]);
            } catch (Exception e) {
                pastGPA[i] = 0.0;
                pastCredits[i] = 0.0;
            }
        }

        btnPredict.setOnClickListener(v -> {
            String semInput = etSemester.getText().toString().trim();
            if (semInput.isEmpty()) {
                tvPrediction.setText("Please enter a semester number!");
                return;
            }

            int chosenSemester = Integer.parseInt(semInput);

            // Enforce min 10 and max 17 semester input
            if (chosenSemester < 10 || chosenSemester > 17) {
                tvPrediction.setText("Semester must be between 10 and 17.");
                return;
            }

            if (chosenSemester <= totalSemesters) {
                tvPrediction.setText("Enter a semester number greater than your current semester.");
                return;
            }

            // Only predict next 2 semesters or until chosenSemester
            int futureSemesters = Math.min(2, chosenSemester - totalSemesters);

            double totalPoints = 0;
            double totalCredits = 0;
            for (int i = 0; i < totalSemesters; i++) {
                totalPoints += pastGPA[i] * pastCredits[i];
                totalCredits += pastCredits[i];
            }

            if (totalCredits == 0) {
                tvPrediction.setText("Credits data missing. Cannot predict.");
                return;
            }

            double avgCreditsPerSem = totalCredits / totalSemesters;
            double trend = (pastGPA[totalSemesters - 1] - pastGPA[0]) / totalSemesters;

            StringBuilder resultBuilder = new StringBuilder();
            double lastGPA = pastGPA[totalSemesters - 1];
            double predictedTotalPoints = totalPoints;
            double predictedTotalCredits = totalCredits;

            for (int i = 1; i <= futureSemesters; i++) {
                double predictedGPA = lastGPA + trend * 0.7;
                if (predictedGPA > 4.0) predictedGPA = 4.0;
                if (predictedGPA < 0.0) predictedGPA = 0.0;

                int semNumber = totalSemesters + i;
                resultBuilder.append("Predicted GPA for Semester ")
                        .append(semNumber)
                        .append(" ≈ ")
                        .append(String.format("%.2f", predictedGPA))
                        .append("\n");

                predictedTotalPoints += predictedGPA * avgCreditsPerSem;
                predictedTotalCredits += avgCreditsPerSem;

                lastGPA = predictedGPA;
            }

            double predictedFinalCGPA = predictedTotalPoints / predictedTotalCredits;
            resultBuilder.append("\nPredicted Final CGPA ")
                    .append(" ≈ ")
                    .append(String.format("%.2f", predictedFinalCGPA));

            tvPrediction.setText(resultBuilder.toString());
        });
    }
}
