package com.example.stu;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Tasbeeh extends AppCompatActivity {

    TextView counterText;
    Button countButton, resetButton, soundButton;
    int count = 0;
    boolean isSoundOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tasbeeh);

        counterText = findViewById(R.id.counterText);
        countButton = findViewById(R.id.countButton);
        resetButton = findViewById(R.id.resetButton);
        soundButton = findViewById(R.id.soundButton);

        countButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSoundOn) {
                    MediaPlayer m1 = MediaPlayer.create(Tasbeeh.this, R.raw.count);
                    m1.start();
                }

                RotateAnimation rotate = new RotateAnimation(
                        0, 20,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(100);
                rotate.setRepeatCount(1);
                rotate.setRepeatMode(Animation.REVERSE);
                v.startAnimation(rotate);
                countButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tasbeeh_pressed, 0, 0);

                new Handler().postDelayed(() -> countButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tc, 0, 0), 500);

                count++;
                counterText.setText(String.valueOf(count));
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSoundOn) {
                    MediaPlayer m2 = MediaPlayer.create(Tasbeeh.this, R.raw.reset);
                    m2.start();
                }

                RotateAnimation rotate = new RotateAnimation(
                        0, 20,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(100);
                rotate.setRepeatCount(1);
                rotate.setRepeatMode(Animation.REVERSE);
                v.startAnimation(rotate);

                count = 0;
                counterText.setText("0");

                resetButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.alert, 0, 0);
                new Handler().postDelayed(() -> resetButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.rotate_arrow, 0, 0), 500);

                countButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tc, 0, 0);
            }
        });

        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSoundOn = !isSoundOn;

                RotateAnimation rotate = new RotateAnimation(
                        0, 20,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(100);
                rotate.setRepeatCount(1);
                rotate.setRepeatMode(Animation.REVERSE);
                v.startAnimation(rotate);

                if (isSoundOn) {
                    soundButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sound, 0, 0);
                } else {
                    soundButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.no, 0, 0);
                }
            }
        });
    }
}
