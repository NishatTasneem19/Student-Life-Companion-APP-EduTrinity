package com.example.stu;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 101;

    // UI components
    private DrawerLayout drawerLayout;
    private ImageButton btnMenu;
    private TextView tvName, tvDepartment, tvSemester;
    private TextView btnAbout, btnFeedback;

    private Button btnTopLeft, btnBottomLeft, btnTopRight, btnBottomRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 1️⃣ Initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        btnMenu = findViewById(R.id.btnMenu);
        tvName = findViewById(R.id.tvName);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvSemester = findViewById(R.id.tvSemester);
        btnAbout = findViewById(R.id.btnAbout);
        btnFeedback = findViewById(R.id.btnFeedback);

        btnTopLeft = findViewById(R.id.btnTopLeft);
        btnBottomLeft = findViewById(R.id.btnBottomLeft);
        btnTopRight = findViewById(R.id.btnTopRight);
        btnBottomRight = findViewById(R.id.btnBottomRight);

        // 2️⃣ Set click listeners
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        btnAbout.setOnClickListener(v ->
                Toast.makeText(this, "App made for students 🎓", Toast.LENGTH_SHORT).show());

        btnFeedback.setOnClickListener(v ->
                Toast.makeText(this, "Feedback feature coming soon!", Toast.LENGTH_SHORT).show());

        btnTopLeft.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, AcademicTools.class));
            showMessage("Academic Tools clicked");
        });

        btnBottomLeft.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, PersonalManagement.class));
            showMessage("Personal Management clicked");
        });

        btnTopRight.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProductivityFocus.class));
            showMessage("Productivity and Focus clicked");
        });

        btnBottomRight.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SpiritualCorner.class));
            showMessage("Spiritual Corner clicked");
        });

        // 3️⃣ Load profile info
        loadUserProfile();

        // 4️⃣ Ask notification permission
        askNotificationPermission();

        // 5️⃣ Handle back press with OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    finish(); // behaves like super.onBackPressed()
                }
            }
        });
    }

    // Load user info from SharedPreferences
    private void loadUserProfile() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String name = "  "+prefs.getString("name", "User");
        String department = " "+prefs.getString("department", "N/A");
        String year = prefs.getString("year", "N/A");

        tvName.setText(name);
        tvDepartment.setText("   Department:" +department);
        tvSemester.setText("   Semester: " + year);
    }

    // Notification permission for Android 13+
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION
                );
            }
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications allowed 🎉", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Notifications denied. You can enable them later in settings.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    // Show short toast message
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
