package com.example.stu;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class MoodStorage {

    private static final String PREF_NAME = "MoodData";
    private SharedPreferences preferences;

    public MoodStorage(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveMood(String date, float moodValue) {
        preferences.edit().putFloat(date, moodValue).apply();
    }

    public float getMoodForDate(String date) {
        return preferences.getFloat(date, 0f); // 0 = no data
    }

    public Map<String, ?> getAllMoods() {
        return preferences.getAll();
    }
}
