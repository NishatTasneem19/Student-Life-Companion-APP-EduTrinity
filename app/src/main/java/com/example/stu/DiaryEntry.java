package com.example.stu;

import org.json.JSONException;
import org.json.JSONObject;

public class DiaryEntry {
    public String timestamp;
    public String mood;
    public String description;

    public DiaryEntry(String timestamp, String mood, String description) {
        this.timestamp = timestamp;
        this.mood = mood;
        this.description = description;
    }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        try {
            o.put("timestamp", timestamp);
            o.put("mood", mood);
            o.put("description", description);
        } catch (JSONException ignored) {}
        return o;
    }

    public static DiaryEntry fromJson(JSONObject o) throws JSONException {
        return new DiaryEntry(
                o.getString("timestamp"),
                o.getString("mood"),
                o.getString("description")
        );
    }
}
