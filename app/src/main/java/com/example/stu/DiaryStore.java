package com.example.stu;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiaryStore {

    private static final String PREF = "MoodPrefs";
    private static final String KEY = "history_json";

    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static List<DiaryEntry> load(Context c) {
        String json = sp(c).getString(KEY, "[]");
        List<DiaryEntry> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(DiaryEntry.fromJson(o));
            }
        } catch (JSONException ignored) {}
        // newest first
        Collections.reverse(list);
        return list;
    }

    private static void save(Context c, List<DiaryEntry> list) {

        List<DiaryEntry> oldestFirst = new ArrayList<>(list);
        Collections.reverse(oldestFirst);

        JSONArray arr = new JSONArray();
        for (DiaryEntry e : oldestFirst) arr.put(e.toJson());
        sp(c).edit().putString(KEY, arr.toString()).apply();
    }

    public static void add(Context c, DiaryEntry e) {
        List<DiaryEntry> list = load(c);
        list.add(0, e);
        save(c, list);
    }

    public static void updateAt(Context c, int index, DiaryEntry e) {
        List<DiaryEntry> list = load(c);
        if (index >= 0 && index < list.size()) {
            list.set(index, e);
            save(c, list);
        }
    }

    public static void deleteAt(Context c, int index) {
        List<DiaryEntry> list = load(c);
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            save(c, list);
        }
    }
}
