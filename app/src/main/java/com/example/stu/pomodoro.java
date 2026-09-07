package com.example.stu;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;

public class pomodoro extends AppCompatActivity {

    private TextView timerText, breakText;
    private Button startButton, resetButton, timerButton, stopButton;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 1500000; // 25 min
    private long savedTimeInMillis = 1500000;
    private boolean isBreakTime = false;

    private LottieAnimationView tomatoAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        timerText = findViewById(R.id.timerText);
        breakText = findViewById(R.id.breakText); // ✅ TextView for Keep Going / Break
        startButton = findViewById(R.id.start);
        resetButton = findViewById(R.id.reset);
        timerButton = findViewById(R.id.timer);
        stopButton = findViewById(R.id.stop);
        tomatoAnim = findViewById(R.id.tomatoAnim);

        tomatoAnim.pauseAnimation();
        breakText.setText("Keep Going!!!");
        updateTimerText();

        // START button
        startButton.setOnClickListener(v -> {
            if (isTimerRunning) return;
            animateButton(v);
            MediaPlayer mp = MediaPlayer.create(this, R.raw.start);
            mp.start();
            mp.setOnCompletionListener(mediaPlayer -> {
                startTimer();
                if (!isBreakTime) tomatoAnim.playAnimation();
            });
        });

        // RESET button
        resetButton.setOnClickListener(v -> {
            animateButton(v);
            MediaPlayer mp = MediaPlayer.create(this, R.raw.reseet);
            mp.start();
            resetTimer();
        });

        // STOP button
        stopButton.setOnClickListener(v -> {
            animateButton(v);
            MediaPlayer mp = MediaPlayer.create(this, R.raw.stop);
            mp.start();
            stopTimer();
        });

        // TIMER selection
        timerButton.setOnClickListener(v -> {
            animateButton(v);
            showTimeSelectionDialog();
        });
    }

    private void startTimer() {
        if (isTimerRunning) return;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                tomatoAnim.pauseAnimation();

                // STOP sound bajbe break shuru howar age
                MediaPlayer stopSound = MediaPlayer.create(pomodoro.this, R.raw.stop);
                stopSound.start();

                if (!isBreakTime) {
                    if (savedTimeInMillis == 1500000) startBreak(5);
                    else if (savedTimeInMillis == 3600000) startBreak(10);
                } else {
                    showRestartDialog();
                }
            }
        }.start();

        isTimerRunning = true;
    }

    private void startBreak(int minutes) {
        isBreakTime = true;
        timeLeftInMillis = minutes * 60 * 1000L;
        tomatoAnim.pauseAnimation(); // break er jonno animation off
        breakText.setText("Take rest for " + minutes + " min"); // ✅ Update text
        startTimer();
    }

    private void stopTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        isTimerRunning = false;
        tomatoAnim.pauseAnimation();
    }

    private void resetTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        isTimerRunning = false;
        isBreakTime = false;
        timeLeftInMillis = savedTimeInMillis;
        updateTimerText();
        tomatoAnim.pauseAnimation();
        breakText.setText("Keep Going!!!"); // ✅ reset text
    }

    private void showTimeSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a timer");

        String[] options = {"25 minutes", "60 minutes"};
        builder.setItems(options, (dialog, which) -> {
            if (countDownTimer != null) countDownTimer.cancel();
            isBreakTime = false;
            tomatoAnim.pauseAnimation();
            breakText.setText("Keep Going!!!");

            if (which == 0) timeLeftInMillis = savedTimeInMillis = 1500000;
            else timeLeftInMillis = savedTimeInMillis = 3600000;

            updateTimerText();
        });

        builder.show();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        timerText.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void animateButton(View v) {
        RotateAnimation rotate = new RotateAnimation(
                0, 20,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(100);
        rotate.setRepeatCount(1);
        rotate.setRepeatMode(Animation.REVERSE);
        v.startAnimation(rotate);
    }

    private void showRestartDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Break Over!");
        builder.setMessage("Start Pomodoro again?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            isBreakTime = false;
            timeLeftInMillis = savedTimeInMillis;
            tomatoAnim.pauseAnimation();
            breakText.setText("Keep Going!!!");
            startTimer();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            isBreakTime = false;
            timerText.setText("Take a rest :)");
        });

        builder.show();
    }
}

