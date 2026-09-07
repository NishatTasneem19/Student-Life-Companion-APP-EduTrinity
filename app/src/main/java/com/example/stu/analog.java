package com.example.stu;



import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class analog extends AppCompatActivity {

    int count = 0;
    final int totalBeads = 100;

    TextView countText;
    boolean isSoundOn = true;
    LinearLayout beadContainer;
    boolean[] moved = new boolean[totalBeads];
    ImageView[] beadViews = new ImageView[totalBeads];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analog);

        countText = findViewById(R.id.countText);
        beadContainer = findViewById(R.id.beadContainer);
        Button resetButton = findViewById(R.id.resetButton);
        ImageView thread = findViewById(R.id.threadLine);

        for (int i = 0; i < totalBeads; i++) {
            ImageView bead = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 180);
            params.setMargins(0, 40, 0, 40);
            bead.setLayoutParams(params);
            bead.setImageResource(R.drawable.bead_shape2);
            int finalI = i;

            bead.setOnClickListener(view -> {
                if (isSoundOn) {
                    MediaPlayer m3 = MediaPlayer.create(analog.this, R.raw.glass);
                    m3.start();
                }
                if (!moved[finalI]) {
                    animateBead(bead, -60); // move up
                    highlightBead(bead);
                    moved[finalI] = true;
                    count++;
                } else {
                    animateBead(bead, 60); // move down
                    resetBead(bead);
                    moved[finalI] = false;
                    count--;
                }
                countText.setText("Count: " + count);
            });

            beadContainer.addView(bead);
            beadViews[i] = bead;
        }

        // Rope height = total beads height + spacing
        int totalHeight = (180 + 80) * totalBeads;
        thread.getLayoutParams().height = totalHeight;
        thread.requestLayout();

        // Reset functionality
        resetButton.setOnClickListener(v -> {
            if (isSoundOn) {
                MediaPlayer m2 = MediaPlayer.create(analog.this, R.raw.reset);
                m2.start();
            }
            for (int i = 0; i < totalBeads; i++) {
                if (moved[i]) {
                    animateBead(beadViews[i], 60);
                    resetBead(beadViews[i]);
                    moved[i] = false;
                }
            }
            count = 0;
            countText.setText("Count: 0");
        });
    }

    void animateBead(ImageView bead, float deltaY) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(bead, "translationY", bead.getTranslationY(), bead.getTranslationY() + deltaY);
        animator.setDuration(300);
        animator.start();
    }

    void highlightBead(ImageView bead) {
        bead.setColorFilter(Color.parseColor("#fb6f92"), PorterDuff.Mode.SRC_ATOP); // golden highlight
    }

    void resetBead(ImageView bead) {
        bead.clearColorFilter();
    }
}
