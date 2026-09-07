package com.example.stu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Diary extends AppCompatActivity {

    private static final String PREFS = "DiaryPrefs";
    private static final int MAX_WORDS = 300;

    private EditText etDiary;
    private TextView tvWordCount, tvDate;
    private SharedPreferences prefs;
    private String currentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        etDiary = findViewById(R.id.etDiary);
        tvWordCount = findViewById(R.id.tvWordCount);
        tvDate = findViewById(R.id.tvDate);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnClear = findViewById(R.id.btnClear);
        Button btnHistory = findViewById(R.id.btnHistory);

        prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        // Check if opened from history
        String dateFromIntent = getIntent().getStringExtra("date");
        String textFromIntent = getIntent().getStringExtra("text");

        if (dateFromIntent != null) {
            // Editing old entry
            currentKey = "entry_" + dateFromIntent;
            tvDate.setText(dateFromIntent);
            etDiary.setText(textFromIntent);
        } else {
            // New entry
            String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            currentKey = "entry_" + dateTime;
            tvDate.setText(dateTime);

            etDiary.setText("");
            etDiary.setHint("Where I Reveal My Thoughts Secretly");
        }

        updateWordCount(etDiary.getText().toString());

        // Word limit watcher
        etDiary.addTextChangedListener(new TextWatcher() {
            private String beforeText = "";
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { beforeText = s.toString(); }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (!enforceWordLimit(s)) {
                    etDiary.removeTextChangedListener(this);
                    etDiary.setText(beforeText);
                    etDiary.setSelection(beforeText.length());
                    etDiary.addTextChangedListener(this);
                    Toast.makeText(Diary.this, "Max " + MAX_WORDS + " words.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSave.setOnClickListener(v -> {
            String text = etDiary.getText().toString().trim();
            prefs.edit().putString(currentKey, text).apply();
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        });

        btnClear.setOnClickListener(v -> etDiary.setText(""));

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(Diary.this, DiaryHistory.class);
            startActivity(intent);
        });
    }

    private boolean enforceWordLimit(Editable s) {
        String text = s.toString();
        updateWordCount(text);
        int words = countWords(text);
        return words <= MAX_WORDS;
    }

    private void updateWordCount(String text) {
        int words = countWords(text);
        tvWordCount.setText(words + " / " + MAX_WORDS + " words");
    }

    private int countWords(String text) {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) return 0;
        return trimmed.split("\\s+").length;
    }

    public static void openDiaryForEdit(Context context, String date, String text) {
        Intent intent = new Intent(context, Diary.class);
        intent.putExtra("date", date);
        intent.putExtra("text", text);
        context.startActivity(intent);
    }
}
