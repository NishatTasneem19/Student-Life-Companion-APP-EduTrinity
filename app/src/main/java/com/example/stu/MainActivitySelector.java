package com.example.stu;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivitySelector extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_selector);

        findViewById(R.id.manualBtn).setOnClickListener(v ->
                startActivity(new Intent(this, SleepActivity.class)));

        findViewById(R.id.autoBtn).setOnClickListener(v ->
                startActivity(new Intent(this, AutoSleepActivity.class)));
    }
}