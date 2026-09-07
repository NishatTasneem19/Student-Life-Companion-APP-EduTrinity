package com.example.stu;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class readydua extends AppCompatActivity {

    private TextView counterText;
    private Button alhamdulillah, allahu, astaghfirullah, subhanallah, lailaha, lahawla;
    private Button resetButton, soundButton;

    private int counter = 0;
    private boolean isSoundOn = true;

    private MediaPlayer clickSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readydua);


        counterText = findViewById(R.id.counterText);


        alhamdulillah = findViewById(R.id.alhamdulillah);
        allahu = findViewById(R.id.allahu);
        astaghfirullah = findViewById(R.id.astaghfirullah);
        subhanallah = findViewById(R.id.subhanallah);
        lailaha = findViewById(R.id.lailaha);
        lahawla = findViewById(R.id.lahawla);

        resetButton = findViewById(R.id.newreset);
        soundButton = findViewById(R.id.vol);


        clickSound = MediaPlayer.create(this, R.raw.count);


        setDoaListener(alhamdulillah, "Alhamdulillah");
        setDoaListener(allahu, "Allahu Akbar");
        setDoaListener(astaghfirullah, "Astaghfirullah");
        setDoaListener(subhanallah, "Subhanallah");
        setDoaListener(lailaha, "La Ilaha Illallah");
        setDoaListener(lahawla, "La Hawla wa la quwwata illa billah");

        resetButton.setOnClickListener(v -> {
            counter = 0;
            counterText.setText("0");
        });


        soundButton.setOnClickListener(v -> {
            isSoundOn = !isSoundOn;
            if (isSoundOn) {
                soundButton.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        getResources().getDrawable(R.drawable.newvol),
                        null,
                        null
                );
            } else {
                soundButton.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        getResources().getDrawable(R.drawable.newmute),
                        null,
                        null
                );
                if (clickSound.isPlaying()) clickSound.pause();
            }
        });
    }

    // Helper
    private void setDoaListener(Button button, final String doaName) {
        button.setOnClickListener(v -> {
            counter++;
            counterText.setText(doaName + "\n" + counter);

            if (isSoundOn) {
                if (clickSound.isPlaying()) clickSound.seekTo(0);
                clickSound.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clickSound != null) {
            clickSound.release();
            clickSound = null;
        }
    }
}
