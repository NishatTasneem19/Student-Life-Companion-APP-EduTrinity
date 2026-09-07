package com.example.stu;

public class SleepRecord {
    private String date, start, end;
    private float duration;
    private int weekday;
    private float consistency;

    public SleepRecord(String date, String start, String end, float duration, int weekday, float consistency){
        this.date=date; this.start=start; this.end=end; this.duration=duration; this.weekday=weekday; this.consistency=consistency;
    }
    public String getDate(){return date;}
    public String getStart(){return start;}
    public String getEnd(){return end;}
    public float getDuration(){return duration;}
    public int getWeekday(){return weekday;}
    public float getConsistency(){return consistency;}

    // New Setters for editing records
    public void setDate(String date) {this.date = date;}
    public void setStart(String start) {this.start = start;}
    public void setEnd(String end) {this.end = end;}
    public void setDuration(float duration) {this.duration = duration;}
}