package com.example.stu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HolybookMain extends AppCompatActivity {

    LinearLayout btnBookQuran, btnBookBible, btnBookGeeta, btnBookTripitok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holybook_main);

        // Find buttons
        btnBookQuran = findViewById(R.id.btnBookQuran);
        btnBookBible = findViewById(R.id.btnBookBible);
        btnBookGeeta = findViewById(R.id.btnBookGeeta);
        btnBookTripitok = findViewById(R.id.btnBookTripitok);





        
        // Click listeners
        btnBookQuran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HolybookMain.this, QuranActivity.class));
            }
        });

        btnBookBible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HolybookMain.this, Bible.class));
            }
        });

        btnBookGeeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HolybookMain.this, Geeta.class));
            }
        });

        btnBookTripitok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HolybookMain.this, Tripitok.class));
            }
        });
    }
}
