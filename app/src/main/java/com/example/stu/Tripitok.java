package com.example.stu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Tripitok extends AppCompatActivity {

    private LinearLayout vinayaBtn, suttaBtn, abhidhammaBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tripitok);

        // Initialize "buttons" (LinearLayouts)
        vinayaBtn = findViewById(R.id.vinayaBtn);
        suttaBtn = findViewById(R.id.suttaBtn);
        abhidhammaBtn = findViewById(R.id.abhidhammaBtn);

        vinayaBtn.setOnClickListener(v ->
                startActivity(new Intent(Tripitok.this, VinayaActivity.class)));

        suttaBtn.setOnClickListener(v ->
                startActivity(new Intent(Tripitok.this, SuttaActivity.class)));

        abhidhammaBtn.setOnClickListener(v ->
                startActivity(new Intent(Tripitok.this, AbhidhammaActivity.class)));
    }
}
