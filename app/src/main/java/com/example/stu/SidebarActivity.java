package com.example.stu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SidebarActivity extends AppCompatActivity {

    TextView tvName, tvDepartment, tvSemester;
    TextView btnToggleTheme, btnAbout, btnFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sidebar);

        tvName = findViewById(R.id.tvName);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvSemester = findViewById(R.id.tvSemester);

        btnToggleTheme = findViewById(R.id.btnToggleTheme);
        btnAbout = findViewById(R.id.btnAbout);
        btnFeedback = findViewById(R.id.btnFeedback);

        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        tvName.setText(prefs.getString("name", "User"));
        tvDepartment.setText(prefs.getString("department", "N/A"));
        tvSemester.setText(prefs.getString("year", "N/A"));

        btnToggleTheme.setOnClickListener(v -> {
            Toast.makeText(this, "Theme toggle clicked", Toast.LENGTH_SHORT).show();
        });

        btnAbout.setOnClickListener(v -> {
            Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show();
        });

        btnFeedback.setOnClickListener(v -> {
            Toast.makeText(this, "Feedback clicked", Toast.LENGTH_SHORT).show();
        });
    }
}
