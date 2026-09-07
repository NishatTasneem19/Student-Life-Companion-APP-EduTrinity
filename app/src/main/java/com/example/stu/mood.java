package com.example.stu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class mood extends AppCompatActivity {

    private EditText etd;
    private Button saveBtn, viewHistoryBtn,viewInsightsBtn;;

    private String selectedMood = "";
    private boolean isEditing = false;
    private int editIndex = -1;

    private final Random random = new Random();
    private final Map<String, List<String>> moodQuotes = new LinkedHashMap<String, List<String>>() {{
        put("Happy", Arrays.asList("Keep smiling, happiness suits you! 😊", "Your positive vibes can light up the world 🌟"));
        put("Sad", Arrays.asList("It's okay to feel sad, better days are coming 💙", "Every storm runs out of rain ☔"));
        put("Angry", Arrays.asList("Breathe in calm, breathe out anger 🧘", "A moment of patience saves days of regret 🙏"));
        put("Anxious", Arrays.asList("You are stronger than your worries 🌸", "Slow down, breathe deep, you got this 💪"));
        put("Calm", Arrays.asList("Peace begins with you 🌿", "Stay calm, stay focused, stay winning ✨"));
        put("Loved", Arrays.asList("You are deeply valued ❤️", "Love surrounds you more than you know 💕"));
        put("Depressed", Arrays.asList("Even the darkest night will end and the sun will rise 🌅", "You matter, and your story isn’t over yet 💜"));
        put("Sick", Arrays.asList("Take rest, healing is on the way 🌸", "Your body is fighting for you, be gentle with yourself 💊"));
        put("Crying", Arrays.asList("Crying doesn’t mean weakness, it means you’re human 💧", "Every tear waters the seed of strength 🌱"));
        put("Excited", Arrays.asList("Your energy is contagious ⚡", "Great things are coming your way 🚀"));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        etd = findViewById(R.id.etd);
        saveBtn = findViewById(R.id.saveBtn);
        viewHistoryBtn = findViewById(R.id.viewHistoryBtn);
        viewInsightsBtn = findViewById(R.id.viewInsightsBtn);


        Intent i = getIntent();
        if (i != null && i.hasExtra("is_edit")) {
            isEditing = i.getBooleanExtra("is_edit", false);
            editIndex = i.getIntExtra("edit_index", -1);
            String editMood = i.getStringExtra("edit_mood");
            String editDesc = i.getStringExtra("edit_desc");

            if (isEditing && editIndex >= 0 && editDesc != null && editMood != null) {
                selectedMood = editMood;
                etd.setText(editDesc);
                Toast.makeText(this, "Editing entry (" + editMood + ")", Toast.LENGTH_SHORT).show();
            }
        }


        findViewById(R.id.btn1).setOnClickListener(v -> onMoodSelected("Happy"));
        findViewById(R.id.btn2).setOnClickListener(v -> onMoodSelected("Sad"));
        findViewById(R.id.btn3).setOnClickListener(v -> onMoodSelected("Angry"));
        findViewById(R.id.btn4).setOnClickListener(v -> onMoodSelected("Anxious"));
        findViewById(R.id.btn5).setOnClickListener(v -> onMoodSelected("Calm"));
        findViewById(R.id.btn6).setOnClickListener(v -> onMoodSelected("Loved"));
        findViewById(R.id.btn7).setOnClickListener(v -> onMoodSelected("Depressed"));
        findViewById(R.id.btn8).setOnClickListener(v -> onMoodSelected("Sick"));
        findViewById(R.id.btn9).setOnClickListener(v -> onMoodSelected("Crying"));
        findViewById(R.id.btn10).setOnClickListener(v -> onMoodSelected("Excited"));

        saveBtn.setOnClickListener(v -> saveEntry());
        viewHistoryBtn.setOnClickListener(v -> startActivity(new Intent(this, MoodHistory.class)));
        viewInsightsBtn.setOnClickListener(v -> startActivity(new Intent(this, MoodInsightsActivity.class)));
    }

    private void onMoodSelected(String mood) {
        selectedMood = mood;


        List<String> quotes = moodQuotes.get(mood);
        if (quotes != null && !quotes.isEmpty()) {
            String quote = quotes.get(random.nextInt(quotes.size()));
            new AlertDialog.Builder(this)
                    .setTitle(mood + " Mood")
                    .setMessage(quote)
                    .setPositiveButton("OK", null)
                    .show();
        }


        Toast.makeText(this, "Selected: " + mood, Toast.LENGTH_SHORT).show();
    }

    private void saveEntry() {
        String desc = etd.getText().toString().trim();

        if (TextUtils.isEmpty(selectedMood)) {
            Toast.makeText(this, "Please select a mood first!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(desc)) {
            Toast.makeText(this, "Please write something!", Toast.LENGTH_SHORT).show();
            return;
        }

// Truncate to 50 words if needed
        String[] words = desc.split("\\s+");
        if (words.length > 50) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 50; j++) {
                if (j > 0) sb.append(" ");
                sb.append(words[j]);
            }
            desc = sb.toString();
            Toast.makeText(this, "Description truncated to 50 words.", Toast.LENGTH_SHORT).show();
        }

// Create timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

// Create both MoodEntry and DiaryEntry
        MoodEntry moodEntry = new MoodEntry(timestamp, selectedMood, desc);
        DiaryEntry diaryEntry = new DiaryEntry(timestamp, selectedMood, desc);

// Save Mood
        MoodStore.add(this, moodEntry);

// Save or update Diary
        if (isEditing && editIndex >= 0) {
            DiaryStore.updateAt(this, editIndex, diaryEntry);
            Toast.makeText(this, "Diary entry updated", Toast.LENGTH_SHORT).show();
            isEditing = false;
            editIndex = -1;
        } else {
            DiaryStore.add(this, diaryEntry);
            Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();
        }

// Reset input
        etd.setText("");
        selectedMood = "";


    }
}
