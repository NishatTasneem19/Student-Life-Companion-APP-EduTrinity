package com.example.stu;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class pin extends AppCompatActivity {

    private static final String PREFS = "DiaryPrefs";
    private static final String KEY_PIN = "PIN";
    private static final int PIN_LENGTH = 4;

    private StringBuilder inputPin = new StringBuilder();
    private TextView tvDots;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        tvDots = findViewById(R.id.tvDots);

        setupPad();
        findViewById(R.id.btnSetPin).setOnClickListener(v -> showSetPinDialog());
        updateDots();
    }

    private void setupPad() {
        int[] ids = { R.id.btn0,R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,R.id.btn5,R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9 };
        for (int id : ids) {
            Button b = findViewById(id);
            b.setOnClickListener(v -> appendDigit(((Button) v).getText().toString()));
        }
        findViewById(R.id.btnBack).setOnClickListener(v -> backspace());
        findViewById(R.id.btnClear).setOnClickListener(v -> clearAll());
    }

    private void appendDigit(String d) {
        if (inputPin.length() >= PIN_LENGTH) return;
        inputPin.append(d);
        updateDots();
        if (inputPin.length() == PIN_LENGTH) validatePin();
    }

    private void backspace() {
        if (inputPin.length() > 0) {
            inputPin.deleteCharAt(inputPin.length() - 1);
            updateDots();
        }
    }

    private void clearAll() {
        inputPin.setLength(0);
        updateDots();
    }

    private void updateDots() {
        int n = inputPin.length();
        String dots = "";
        for (int i = 0; i < PIN_LENGTH; i++) {
            dots += (i < n ? "● " : "○ ");
        }
        tvDots.setText(dots.trim());
    }

    private void validatePin() {
        String savedPin = prefs.getString(KEY_PIN, "1234"); // default PIN
        if (inputPin.toString().equals(savedPin)) {
            startActivity(new Intent(this, Diary.class));
            clearAll();
        } else {
            Toast.makeText(this, "Wrong PIN!", Toast.LENGTH_SHORT).show();
            clearAll();
        }
    }

    private void showSetPinDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set / Change PIN");
        builder.setMessage("New PIN will be 4 digits.\nDefault is 1234.");
        builder.setPositiveButton("Set", (d, w) -> askNewPin());
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void askNewPin() {

        final String[] step = { "enter" };
        final StringBuilder temp = new StringBuilder();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Enter new 4-digit PIN")
                .setView(getLayoutInflater().inflate(R.layout.activity_dialog_pin_input, null))
                .setCancelable(false)
                .create();
        dialog.show();

        TextView tvInfo = dialog.findViewById(R.id.tvInfo);
        TextView tvPin = dialog.findViewById(R.id.tvPin);
        Button ok = dialog.findViewById(R.id.btnOk);
        Button cancel = dialog.findViewById(R.id.btnCancel);

        int[] ids = { R.id.k0,R.id.k1,R.id.k2,R.id.k3,R.id.k4,R.id.k5,R.id.k6,R.id.k7,R.id.k8,R.id.k9 };
        for (int id : ids) {
            Button b = dialog.findViewById(id);
            b.setOnClickListener(v -> {
                if (temp.length() < 4) temp.append(b.getText().toString());
                tvPin.setText(star(temp.length()));
            });
        }
        dialog.findViewById(R.id.kBack).setOnClickListener(v -> {
            if (temp.length() > 0) temp.deleteCharAt(temp.length()-1);
            tvPin.setText(star(temp.length()));
        });
        dialog.findViewById(R.id.kClear).setOnClickListener(v -> {
            temp.setLength(0); tvPin.setText(star(0));
        });

        ok.setOnClickListener(v -> {
            if (temp.length() != 4) {
                tvInfo.setText("Please enter 4 digits.");
                return;
            }
            prefs.edit().putString(KEY_PIN, temp.toString()).apply();
            Toast.makeText(this, "PIN set successfully.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
    }

    private String star(int n){
        String s=""; for(int i=0;i<n;i++) s+="● ";
        return s.trim();
    }
}
