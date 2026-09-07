package com.example.stu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class ProductivityFocus extends AppCompatActivity {


    CardView cardMT;
    CardView cardPD;
    CardView cardST;
    ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_productivity_focus);

        cardMT = findViewById(R.id.cardMT);
        cardPD = findViewById(R.id.cardPD);
        cardST = findViewById(R.id.cardST);
        btnBack = findViewById(R.id.btnBack);

        cardMT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductivityFocus.this, moodfrontpage.class));
            }
        });

        cardPD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductivityFocus.this, pomodoro.class));
            }
        });
        cardST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductivityFocus.this, MainActivitySelector.class));
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductivityFocus.this, HomeActivity.class));
            }
        });

    }
}