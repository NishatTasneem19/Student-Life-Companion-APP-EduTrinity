package com.example.stu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AutoSleepActivity extends AppCompatActivity {

    private ToggleButton autoToggle;
    private TextView autoTimeDisplay, bedtimeText, wakeupText;
    private Button saveBtn, viewBtn, historyBtn, resetBtn;

    private String startTimeStr = "", endTimeStr = "";
    private SharedPreferences sharedPreferences;
    private ArrayList<SleepRecord> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_sleep);

        // UI Initialization
        autoToggle = findViewById(R.id.autoSleepToggle);
        autoTimeDisplay = findViewById(R.id.autoTimeDisplay);
        bedtimeText = findViewById(R.id.bedtimeText);
        wakeupText = findViewById(R.id.wakeupText);
        saveBtn = findViewById(R.id.saveBtnAuto);
        viewBtn = findViewById(R.id.viewBtnAuto);
        historyBtn = findViewById(R.id.historyBtnAuto);
        resetBtn = findViewById(R.id.resetBtnAuto);

        sharedPreferences = getSharedPreferences("SleepData", MODE_PRIVATE);

        // --- Restore previous state ---
        restoreTrackingState();

        // --- Setup Toggle Logic ---
        setupToggleListener();

        // --- Save Button Logic ---
        saveBtn.setOnClickListener(v -> saveAutoData());

        // --- Aggressive Reset Logic ---
        resetBtn.setOnClickListener(v -> {
            // 1. Temporary listener remove (jate bedtime wakeup e na jay)
            autoToggle.setOnCheckedChangeListener(null);

            // 2. Clear SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("isTracking");
            editor.remove("tempStartTime");
            editor.remove("tempEndTime");
            editor.apply();

            // 3. Clear Class Variables
            startTimeStr = "";
            endTimeStr = "";

            // 4. UI Reset
            autoToggle.setChecked(false);
            autoToggle.getBackground().setTint(Color.GRAY);
            autoTimeDisplay.setText("Status: Ready");
            bedtimeText.setText("Bedtime: --:--");
            wakeupText.setText("Wakeup: --:--");

            // 5. Listener abar enable kora
            setupToggleListener();

            Toast.makeText(this, "Everything Reset!", Toast.LENGTH_SHORT).show();
        });

        viewBtn.setOnClickListener(v -> startActivity(new Intent(this, SleepStatistics.class)));
        historyBtn.setOnClickListener(v -> startActivity(new Intent(this, SleepHistoryActivity.class)));
    }

    private void setupToggleListener() {
        autoToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String now = new SimpleDateFormat("hh:mm a", Locale.US).format(new Date());
            if (isChecked) {
                startTimeStr = now;
                endTimeStr = "";
                saveTrackingState(true, startTimeStr, "");
                updateUIForTracking(true);
                bedtimeText.setText("Bedtime: " + startTimeStr);
                wakeupText.setText("Wakeup: --:--");
            } else {
                endTimeStr = now;
                saveTrackingState(false, startTimeStr, endTimeStr);
                updateUIForTracking(false);
                wakeupText.setText("Wakeup: " + endTimeStr);
            }
        });
    }

    private void saveTrackingState(boolean isTracking, String start, String end) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isTracking", isTracking);
        editor.putString("tempStartTime", start);
        editor.putString("tempEndTime", end);
        editor.apply();
    }

    private void restoreTrackingState() {
        boolean isTracking = sharedPreferences.getBoolean("isTracking", false);
        startTimeStr = sharedPreferences.getString("tempStartTime", "");
        endTimeStr = sharedPreferences.getString("tempEndTime", "");

        if (isTracking) {
            autoToggle.setChecked(true);
            updateUIForTracking(true);
            bedtimeText.setText("Bedtime: " + startTimeStr);
            wakeupText.setText("Wakeup: --:--");
        } else if (!startTimeStr.isEmpty() && !endTimeStr.isEmpty()) {
            autoToggle.setChecked(false);
            updateUIForTracking(false);
            bedtimeText.setText("Bedtime: " + startTimeStr);
            wakeupText.setText("Wakeup: " + endTimeStr);
        } else {
            updateUIForTracking(false);
        }
    }

    private void updateUIForTracking(boolean isTracking) {
        if (isTracking) {
            autoToggle.getBackground().setTint(Color.parseColor("#10B981"));
            autoTimeDisplay.setText("Status: Sleeping...");
        } else {
            autoToggle.getBackground().setTint(Color.GRAY);
            autoTimeDisplay.setText("Status: Awake!");
        }
    }

    private void saveAutoData() {
        if (startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
            Toast.makeText(this, "Complete tracking first!", Toast.LENGTH_SHORT).show();
            return;
        }

        float duration = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
            Date date1 = sdf.parse(startTimeStr);
            Date date2 = sdf.parse(endTimeStr);
            if (date1 != null && date2 != null) {
                long diff = date2.getTime() - date1.getTime();
                if (diff < 0) diff += 24 * 60 * 60 * 1000;
                duration = diff / (1000f * 60 * 60);
            }
        } catch (Exception e) { Log.e("AUTO_ERROR", e.getMessage()); }

        if (duration <= 0.01) {
            Toast.makeText(this, "Duration too short!", Toast.LENGTH_SHORT).show();
            return;
        }

        loadRecords();
        String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Calendar cal = Calendar.getInstance();
        int weekday = (cal.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY && cal.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY) ? 1 : 0;
        float lastDur = records.isEmpty() ? duration : records.get(records.size() - 1).getDuration();

        records.add(new SleepRecord(today, startTimeStr, endTimeStr, duration, weekday, Math.abs(duration - lastDur)));
        sharedPreferences.edit().putString("records", new Gson().toJson(records)).apply();

        saveTrackingState(false, "", "");
        Toast.makeText(this, "Saved " + String.format(Locale.US, "%.2f", duration) + " hrs", Toast.LENGTH_SHORT).show();

        // Manual Reset UI
        autoToggle.setOnCheckedChangeListener(null);
        resetUI();
        setupToggleListener();
    }

    private void loadRecords() {
        String json = sharedPreferences.getString("records", null);
        Type type = new TypeToken<ArrayList<SleepRecord>>() {}.getType();
        records = (json != null) ? new Gson().fromJson(json, type) : new ArrayList<>();
    }

    private void resetUI() {
        startTimeStr = ""; endTimeStr = "";
        bedtimeText.setText("Bedtime: --:--");
        wakeupText.setText("Wakeup: --:--");
        autoTimeDisplay.setText("Status: Ready");
        autoToggle.setChecked(false);
        autoToggle.getBackground().setTint(Color.GRAY);
    }
}