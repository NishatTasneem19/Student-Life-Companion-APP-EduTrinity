package com.example.stu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileSetupActivity extends AppCompatActivity {

    ViewFlipper viewFlipper;
    EditText etName, etDept, etYear;
    Button btnNext1, btnNext2, btnBack1, btnBack2, btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        if (prefs.getBoolean("isProfileSaved", false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }


        viewFlipper = findViewById(R.id.viewFlipper);
        etName = findViewById(R.id.etName);
        etDept = findViewById(R.id.etDept);
        etYear = findViewById(R.id.etYear);

        btnNext1 = findViewById(R.id.btnNext1);
        btnNext2 = findViewById(R.id.btnNext2);
        btnBack1 = findViewById(R.id.btnBack1);
        btnBack2 = findViewById(R.id.btnBack2);
        btnFinish = findViewById(R.id.btnFinish);

        btnNext1.setOnClickListener(v -> {
            if (etName.getText().toString().trim().isEmpty()) {
                etName.setError("Enter your name");
            } else {
                viewFlipper.showNext();
            }
        });

        btnNext2.setOnClickListener(v -> {
            if (etDept.getText().toString().trim().isEmpty()) {
                etDept.setError("Enter your department");
            } else {
                viewFlipper.showNext();
            }
        });

        btnBack1.setOnClickListener(v -> viewFlipper.showPrevious());
        btnBack2.setOnClickListener(v -> viewFlipper.showPrevious());

        btnFinish.setOnClickListener(v -> {
            if (etYear.getText().toString().trim().isEmpty()) {
                etYear.setError("Enter your year/semester");
            } else {
                // Save to SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("UserProfile", MODE_PRIVATE).edit();
                editor.putString("name", etName.getText().toString().trim());
                editor.putString("department", etDept.getText().toString().trim());
                editor.putString("year", etYear.getText().toString().trim());
                editor.putBoolean("isProfileSaved", true);
                editor.apply();

                // Show success dialog
                new AlertDialog.Builder(this)
                        .setTitle("Profile Created")
                        .setMessage("Your profile has been set successfully!")
                        .setPositiveButton("Continue", (dialog, which) -> {
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }
}

