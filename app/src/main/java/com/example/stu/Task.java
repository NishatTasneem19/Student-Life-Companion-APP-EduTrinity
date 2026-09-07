// java/.../Task.java
package com.example.stu;

public class Task {
    public int id;                    // stable unique id for alarms & cancel
    public String title, description, category;
    public long reminderTime;         // millis; 0 => no reminder
    public boolean completed;
    public long timestamp;            // created/last-updated time (for sorting)
    public boolean isHeader = false;  // you’re already using this in the adapter

    public Task() { }

    // Constructor for new tasks
    public Task(String title, String description, String category, long reminderTime, boolean completed) {
        this.id = (int) (System.currentTimeMillis() & 0x7fffffff); // simple unique-ish id
        this.title = title;
        this.description = description;
        this.category = category;
        this.reminderTime = reminderTime;
        this.completed = completed;
        this.timestamp = System.currentTimeMillis();
    }
}
