package com.example.stu;

public class ReminderItem {
    public long timeMillis;
    public int id;
    public String title;
    public String category;

    public ReminderItem(long timeMillis, int id, String title, String category) {
        this.timeMillis = timeMillis;
        this.id = id;
        this.title = title;
        this.category = category;
    }
}
