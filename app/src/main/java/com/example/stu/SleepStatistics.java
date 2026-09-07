package com.example.stu;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SleepStatistics extends AppCompatActivity {

    TextView dateText, startText, endText, durationText, feedbackText, mlStatusText;
    LineChart sleepChart;
    LottieAnimationView lottieView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleepstatistics);

        // Initialize UI components
        dateText = findViewById(R.id.dateText);
        startText = findViewById(R.id.startText);
        endText = findViewById(R.id.endText);
        durationText = findViewById(R.id.durationText);
        feedbackText = findViewById(R.id.feedbackText);
        mlStatusText = findViewById(R.id.mlStatusText);
        sleepChart = findViewById(R.id.sleepChart);
        lottieView = findViewById(R.id.lottieView);

        if (lottieView != null) lottieView.playAnimation();
        else Log.e("stats", "LottieAnimationView is null!");

        sharedPreferences = getSharedPreferences("SleepData", MODE_PRIVATE);

        loadLastSleep();
        showSleepChart();
        showSleepFeedback();
    }

    /**
     * Loads and displays the details of the most recent sleep record.
     */
    private void loadLastSleep() {
        ArrayList<SleepRecord> records = loadRecordsFromStorage();
        if (records.isEmpty()) {
            dateText.setText("📅 Date: --");
            startText.setText("🛌 Bedtime: --");
            endText.setText("☀️ Wake Time: --");
            durationText.setText("⏱️ Duration: 0.00 hours");
            return;
        }

        SleepRecord last = records.get(records.size() - 1);

        float duration = last.getDuration();
        // Recalculate duration robustly if the saved value is 0 or invalid (> 24 hours).
        if (duration <= 0 || duration > 24) {
            // Use the calculation method from SleepActivity which handles the hh:mm a format
            duration = (float) SleepActivity.calculateDuration(last.getStart(), last.getEnd());
        }

        dateText.setText("📅 Date: " + safeString(last.getDate()));
        startText.setText("🛌 Bedtime: " + safeString(last.getStart()));
        endText.setText("☀️ Wake Time: " + safeString(last.getEnd()));
        durationText.setText(String.format("⏱️ Duration: %.2f hours", duration));
    }

    private String safeString(String str) {
        return (str == null) ? "--" : str;
    }

    /**
     * Helper function to load all records from SharedPreferences.
     */
    private ArrayList<SleepRecord> loadRecordsFromStorage() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("records", null);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<ArrayList<SleepRecord>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Displays a chart of the last 7 days of sleep duration.
     */
    private void showSleepChart() {
        ArrayList<SleepRecord> records = loadRecordsFromStorage();
        if (records.isEmpty()) {
            sleepChart.clear();
            sleepChart.setNoDataText("No sleep data for chart.");
            sleepChart.invalidate();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        int start = Math.max(0, records.size() - 7);
        for (int i = start; i < records.size(); i++) {
            SleepRecord record = records.get(i);
            float duration = record.getDuration();
            // Recalculate duration for chart if needed
            if (duration <= 0 || duration > 24) duration = (float) SleepActivity.calculateDuration(record.getStart(), record.getEnd());
            entries.add(new Entry(i - start, duration));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Sleep Duration (hours)");

        // Chart Styling
        sleepChart.getLegend().setTextColor(Color.WHITE);
        dataSet.setColor(Color.CYAN);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        LineData lineData = new LineData(dataSet);
        sleepChart.setData(lineData);
        sleepChart.getDescription().setEnabled(false);
        sleepChart.getXAxis().setTextColor(Color.WHITE);
        sleepChart.getAxisLeft().setTextColor(Color.WHITE);
        sleepChart.getAxisRight().setTextColor(Color.WHITE);
        sleepChart.getXAxis().setGridColor(Color.LTGRAY);
        sleepChart.getAxisLeft().setGridColor(Color.LTGRAY);
        sleepChart.getAxisRight().setDrawLabels(false); // Hide right axis labels

        sleepChart.invalidate();
    }

    /**
     * Shows daily feedback (rule-based) and 7-day ML-based risk assessment.
     */
    private void showSleepFeedback() {
        ArrayList<SleepRecord> records = loadRecordsFromStorage();
        if (records.isEmpty()) {
            feedbackText.setText("⚠️ No sleep data available. Please save a record first.");
            mlStatusText.setText("ML Status: Insufficient data for 7-day risk assessment.");
            mlStatusText.setTextColor(Color.WHITE);
            return;
        }

        // --- 1. DAILY FEEDBACK (Simple Rule-Based) ---
        SleepRecord lastRecord = records.get(records.size() - 1);
        float lastDuration = lastRecord.getDuration();
        if (lastDuration <= 0 || lastDuration > 24) lastDuration = (float) SleepActivity.calculateDuration(lastRecord.getStart(), lastRecord.getEnd());

        String dailyFeedback;
        if (lastDuration < 4.0f) {
            dailyFeedback = "🚨 CRITICAL: Very low sleep duration. Seek immediate rest.";
        } else if (lastDuration >= 4.0f && lastDuration < 7.0f) {
            dailyFeedback = "😴 Sleep could be better. Aim for 7–9 hours.";
        } else if (lastDuration >= 7.0f && lastDuration <= 10.0f) {
            dailyFeedback = "🌙 GREAT SLEEP! Keep it up.";
        } else { // > 10.0f
            dailyFeedback = "💤 Oversleeping detected. Maintain a consistent routine.";
        }
        feedbackText.setText(dailyFeedback);

        // --- 2. ML RISK ASSESSMENT (TFLite 7-Day Logic) ---
        if (records.size() < 7) {
            int needed = 7 - records.size();
            mlStatusText.setText("ML Status: Need " + needed + " more day" + (needed > 1 ? "s" : "") + " for 7-day risk assessment.");
            mlStatusText.setTextColor(Color.YELLOW);
            return;
        }

        try {
            // Get the last 7 durations for the ML model
            List<Float> lastSevenDurations = new ArrayList<>();
            for (int i = records.size() - 7; i < records.size(); i++) {
                SleepRecord r = records.get(i);
                float d = r.getDuration();
                // Ensure duration is calculated if it was 0 when saved
                if (d <= 0 || d > 24) d = (float) SleepActivity.calculateDuration(r.getStart(), r.getEnd());
                lastSevenDurations.add(d);
            }

            // The TFLiteRiskPredictor class is assumed to be implemented correctly
            TFLiteRiskPredictor predictor = new TFLiteRiskPredictor(this);
            int riskClass = predictor.predictRisk(lastSevenDurations);
            predictor.close();

            // Display ML result
            if (riskClass == 1) {
                mlStatusText.setText("!!! ML RISK ALERT !!! 7-day average indicates Insomnia Risk.");
                mlStatusText.setTextColor(Color.RED);
            } else {
                mlStatusText.setText("7-day Status: Healthy sleep patterns maintained (No Insomnia Risk).");
                mlStatusText.setTextColor(Color.GREEN);
            }

        } catch (Exception e) {
            Log.e("ML_ERROR", "Error during TFLite prediction: " + e.getMessage());
            mlStatusText.setText("ML Status: Error loading or predicting with the model.");
            mlStatusText.setTextColor(Color.YELLOW);
        }
    }
}