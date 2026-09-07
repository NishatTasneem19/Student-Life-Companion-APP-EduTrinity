package com.example.stu;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SleepActivity extends AppCompatActivity {

    EditText startTime, endTime;
    Button saveBtn, viewBtn, historyBtn;
    SharedPreferences sharedPreferences;
    ArrayList<SleepRecord> records;
    LottieAnimationView lottieView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        saveBtn = findViewById(R.id.saveBtn);
        viewBtn = findViewById(R.id.viewBtn);
        historyBtn = findViewById(R.id.historyBtn);
        lottieView = findViewById(R.id.lottieView);

        if (lottieView != null) lottieView.playAnimation();

        sharedPreferences = getSharedPreferences("SleepData", MODE_PRIVATE);

        loadRecords();

        startTime.setOnClickListener(v -> showTimePicker(startTime));
        endTime.setOnClickListener(v -> showTimePicker(endTime));

        saveBtn.setOnClickListener(v -> saveSleepData());
        viewBtn.setOnClickListener(v -> startActivity(new Intent(this, SleepStatistics.class)));
        historyBtn.setOnClickListener(v -> startActivity(new Intent(this, SleepHistoryActivity.class)));
    }

    private void loadRecords() {
        sharedPreferences = getSharedPreferences("SleepData", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("records", null);
        Type type = new TypeToken<ArrayList<SleepRecord>>() {}.getType();

        if (json != null) {
            records = gson.fromJson(json, type);
            if (records == null) records = new ArrayList<>();
        } else {
            records = new ArrayList<>();
        }
    }

    private void showTimePicker(EditText editText) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog tp = new TimePickerDialog(this, (view, h, m) -> {
            String time = String.format(Locale.US, "%02d:%02d %s",
                    (h==0||h==12)?12:(h%12), m, (h>=12)?"PM":"AM");
            editText.setText(time);
        }, hour, minute, false);
        tp.show();
    }

    private void saveSleepData() {
        // Always reload latest data first
        loadRecords();

        String start = startTime.getText().toString();
        String end = endTime.getText().toString();

        if (start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Enter both times!", Toast.LENGTH_SHORT).show();
            return;
        }

        double durationDouble = calculateDuration(start, end);
        float duration = (float) durationDouble;

        if (duration <= 0 || duration > 24) {
            Toast.makeText(this, "ERROR: Invalid time entered.", Toast.LENGTH_LONG).show();
            Log.e("SleepSave", "Invalid duration: " + duration);
            return;
        }

        Calendar cal = Calendar.getInstance();
        String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(cal.getTime());

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int weekday = (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) ? 1 : 0;

        float consistency = 0f;
        if (!records.isEmpty())
            consistency = Math.abs(duration - records.get(records.size() - 1).getDuration());

        SleepRecord record = new SleepRecord(today, start, end, duration, weekday, consistency);
        records.add(record);

        // Save synchronously to prevent cache issues
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("records");
        editor.putString("records", new Gson().toJson(records));
        editor.commit(); // ✅ critical: synchronous write

        Log.i("SleepSave", String.format("Saved: %.2f hours. Start: %s End: %s", duration, start, end));
        Toast.makeText(this, "Sleep record saved! 😴", Toast.LENGTH_SHORT).show();

        startTime.setText("");
        endTime.setText("");
    }

    public static double calculateDuration(String start, String end) {
        if (start == null || end == null || start.isEmpty() || end.isEmpty()) return 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
            Date d1 = sdf.parse(start.trim());
            Date d2 = sdf.parse(end.trim());
            if (d1 == null || d2 == null) return 0;

            long diff = d2.getTime() - d1.getTime();
            if(diff < 0) diff += 24 * 60 * 60 * 1000;

            double duration = diff / (1000.0 * 60 * 60);
            if (duration <= 0 || duration > 24) return 0;
            return duration;
        } catch (Exception e){
            Log.e("DurationCalc", "Error: " + e.getMessage());
            return 0;
        }
    }
}
