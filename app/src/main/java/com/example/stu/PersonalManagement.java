package com.example.stu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class PersonalManagement extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    EditText etMonthlyBudget;
    Button btnSaveBudget;
    TextView tvSelectedDate;
    LinearLayout tvAddExpenseBox,btnPickDateBox;

    KeyListener originalKeyListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_management);

        LinearLayout navRecords = findViewById(R.id.navRecords);


        navRecords.setOnClickListener(v -> {
            Intent intent = new Intent(PersonalManagement.this, RecordsActivity.class);
            startActivity(intent);
        });

        sharedPreferences = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        etMonthlyBudget = findViewById(R.id.etMonthlyBudget);
        btnSaveBudget = findViewById(R.id.btnSaveBudget);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        ImageButton btnPickDate = findViewById(R.id.btnPickDate);
        ImageButton btnAddExpense = findViewById(R.id.btnAddExpense);
        ImageView btnBack = findViewById(R.id.btnBack);
        tvAddExpenseBox = findViewById(R.id.tvAddExpenseBox);
        btnPickDateBox = findViewById(R.id.btnPickDateBox);

        originalKeyListener = etMonthlyBudget.getKeyListener();

        // Load saved budget
        String savedBudget = sharedPreferences.getString("monthly_budget", "");
        if (!savedBudget.isEmpty()) {
            etMonthlyBudget.setText(savedBudget);
            disableBudgetEditing();
        }

        // Always set the date to today's date on app start (ignore previously saved date)
        String today = getTodayDate();
        tvSelectedDate.setText(today);
        editor.putString("selected_date", today);
        editor.apply();

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(PersonalManagement.this, HomeActivity.class);
            startActivity(intent);
        });

        btnSaveBudget.setOnClickListener(v -> {
            String budget = etMonthlyBudget.getText().toString().trim();
            if (budget.isEmpty()) {
                etMonthlyBudget.setError("Enter a budget");
                return;
            }
            editor.putString("monthly_budget", budget);
            editor.apply();
            disableBudgetEditing();
        });

        etMonthlyBudget.setOnClickListener(v -> {
            if (etMonthlyBudget.getKeyListener() == null) {
                enableBudgetEditing();
            }
        });

        // Show date picker on clicking either date text or calendar icon
        View.OnClickListener pickDateListener = v -> showDatePickerDialog();

        tvSelectedDate.setOnClickListener(pickDateListener);
        btnPickDateBox.setOnClickListener(pickDateListener);

        tvAddExpenseBox.setOnClickListener(v -> {
            String currentBudget = sharedPreferences.getString("monthly_budget", "");
            String selectedDateCheck = sharedPreferences.getString("selected_date", "");

            if (currentBudget.isEmpty()) {
                etMonthlyBudget.setError("Please enter and save your monthly budget first");
                return;
            }

            if (selectedDateCheck.isEmpty()) {
                tvSelectedDate.setError("Please select a date first");
                return;
            }

            Intent intent = new Intent(PersonalManagement.this, AddExpenses.class);
            startActivity(intent);
        });
    }

    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        return String.format(Locale.getDefault(), "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();

        // Parse current selected date or fallback to today
        String currentDateStr = tvSelectedDate.getText().toString();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        try {
            String[] parts = currentDateStr.split("-");
            if (parts.length == 3) {
                year = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]) - 1;
                day = Integer.parseInt(parts[2]);
            }
        } catch (Exception ignored) {}

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    tvSelectedDate.setText(formattedDate);
                    editor.putString("selected_date", formattedDate);
                    editor.apply();
                },
                year, month, day
        );

        // No restriction: user can pick past and future dates freely
        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()); // DO NOT USE

        datePickerDialog.show();
    }

    private void disableBudgetEditing() {
        etMonthlyBudget.setFocusable(false);
        etMonthlyBudget.setFocusableInTouchMode(false);
        etMonthlyBudget.setCursorVisible(false);
        etMonthlyBudget.setKeyListener(null);
        etMonthlyBudget.setTextSize(20);
        etMonthlyBudget.setBackgroundResource(R.drawable.transparent_edittext);
        etMonthlyBudget.setTextColor(Color.BLACK);
        btnSaveBudget.setVisibility(View.GONE);
    }

    private void enableBudgetEditing() {
        etMonthlyBudget.setFocusable(true);
        etMonthlyBudget.setFocusableInTouchMode(true);
        etMonthlyBudget.setCursorVisible(true);
        etMonthlyBudget.setKeyListener(originalKeyListener);
        etMonthlyBudget.setTextSize(18);
        etMonthlyBudget.setBackgroundResource(R.drawable.rounded_edittext_orange);
        etMonthlyBudget.setTextColor(Color.BLACK);
        etMonthlyBudget.requestFocus();
        btnSaveBudget.setVisibility(View.VISIBLE);
    }
}


