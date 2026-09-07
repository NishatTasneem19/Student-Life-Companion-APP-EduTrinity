package com.example.stu;

public class ExpenseRecord {
    private String categoryName;
    private int iconResId;
    private double amount;
    private String type; // "income" or "expense"
    private String dateAdded;



    public ExpenseRecord(String categoryName, int iconResId, double amount, String type, String dateAdded ) {
        this.categoryName = categoryName;
        this.iconResId = iconResId;
        this.amount = amount;
        this.type = type;
        this.dateAdded = dateAdded;

    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getIconResId() {
        return iconResId;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getDateAdded() {
        return dateAdded;
    }


}