package com.example.stu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SpiritualCorner extends AppCompatActivity {
    CardView cardTb, cardHolyA;
    ImageButton  btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spiritual_corner);
        cardTb = findViewById(R.id.cardTb);
       cardHolyA = findViewById(R.id. cardHolyA);
      btnBack = findViewById(R.id. btnBack);

        cardTb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SpiritualCorner.this, Tasbeehstart.class));


            }
        });

        cardHolyA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SpiritualCorner.this, HolybookMain.class));
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}