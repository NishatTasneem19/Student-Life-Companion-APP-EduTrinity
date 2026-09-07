package com.example.stu;

import org.json.JSONException;
import org.json.JSONObject;

public class MoodEntry {
    public String timestamp;
    public String mood;
    public String description;

    public MoodEntry(String timestamp, String mood, String description) {
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

    public static MoodEntry fromJson(JSONObject o) throws JSONException {
        return new MoodEntry(
                o.getString("timestamp"),
                o.getString("mood"),
                o.getString("description")
        );
    }
}
