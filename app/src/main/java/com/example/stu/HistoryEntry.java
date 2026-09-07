package com.example.stu;

public class HistoryEntry {
    private String date;
    private String description;

    public HistoryEntry(String date, String description) {
        this.date = date;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
