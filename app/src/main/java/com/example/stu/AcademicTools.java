package com.example.stu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.cardview.widget.CardView;

public class AcademicTools extends AppCompatActivity {
    CardView cardTodo, cardCgpa; // change to CardView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_tools);



        ImageView btnBack = findViewById(R.id.btnBack);
        cardTodo = findViewById(R.id.cardTodo);
        cardCgpa = findViewById(R.id.cardCgpa);

        cardTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AcademicTools.this, ToDoList.class));
            }
        });

        cardCgpa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AcademicTools.this, CGPACalculatorActivity.class));
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AcademicTools.this, HomeActivity.class);
            startActivity(intent);
        });

        //cardCgpa.setOnClickListener(v -> {

           // startActivity(intent);
        //});



    }
}

