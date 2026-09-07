package com.example.stu;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class moodfrontpage extends AppCompatActivity {

    Button bMood, bDiary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodfrontpage);


        bMood = findViewById(R.id.b2);
        bDiary = findViewById(R.id.b3);


        bMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(moodfrontpage.this, mood.class);
                startActivity(intent);
            }
        });


        bDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(moodfrontpage.this, pin.class);
                startActivity(intent);
            }
        });
    }
}
