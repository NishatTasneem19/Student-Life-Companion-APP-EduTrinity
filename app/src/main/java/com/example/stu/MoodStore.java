package com.example.stu;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MoodStore {
    private static final String PREFS_NAME = "MoodTrackerPrefs";
    private static final String KEY_ENTRIES = "MoodEntries";

    public static void add(Context context, MoodEntry entry) {
        List<MoodEntry> list = getAll(context);
        list.add(entry);
        saveAll(context, list);
    }

    public static List<MoodEntry> getAll(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_ENTRIES, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<MoodEntry>>(){}.getType();
        return new Gson().fromJson(json, type);
    }

    private static void saveAll(Context context, List<MoodEntry> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_ENTRIES, new Gson().toJson(list)).apply();
    }

    public static List<MoodEntry> getLastNDays(Context context, int nDays) {
        List<MoodEntry> all = getAll(context);
        List<MoodEntry> result = new ArrayList<>();
        long now = System.currentTimeMillis();
        long cutoff = now - (long)nDays * 24*60*60*1000;
        for (int i = all.size()-1; i>=0; i--) {
            try {
                long time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(all.get(i).timestamp).getTime();
                if (time >= cutoff) result.add(0, all.get(i));
            } catch (Exception e) { e.printStackTrace(); }
        }
        return result;
    }
}
