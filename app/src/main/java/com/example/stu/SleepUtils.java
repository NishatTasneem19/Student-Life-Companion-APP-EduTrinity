package com.example.stu;

public class SleepUtils {

    /**
     * Calculates sleep duration in hours, handling overnight sleep.
     * @param start Start time string (HH:mm)
     * @param end End time string (HH:mm)
     * @return Duration in hours (float)
     */
    public static float calculateDuration(String start, String end) {
        if (start == null || end == null) return 0;
        try {
            String[] sParts = start.trim().split(":");
            String[] eParts = end.trim().split(":");
            if (sParts.length < 2 || eParts.length < 2) return 0;

            int startMin = Integer.parseInt(sParts[0]) * 60 + Integer.parseInt(sParts[1]);
            int endMin = Integer.parseInt(eParts[0]) * 60 + Integer.parseInt(eParts[1]);
            int diff = endMin - startMin;
            if (diff < 0) diff += 24 * 60; // Handle overnight sleep

            return diff / 60f;
        } catch (Exception e) {
            return 0;
        }
    }
}
