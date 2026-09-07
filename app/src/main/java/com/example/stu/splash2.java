package com.example.stu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class splash2 extends AppCompatActivity {

    private LottieAnimationView lottieView, load;
    private ImageView imageView;
    private TextView textView;
    Animation imanim, teanim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        lottieView = findViewById(R.id.lottieView);
        load = findViewById(R.id.load);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textview);


        imanim = AnimationUtils.loadAnimation(this, R.anim.imageanim);
        teanim = AnimationUtils.loadAnimation(this, R.anim.textanim);


        imageView.setAnimation(imanim);
        textView.setAnimation(teanim);


        lottieView.playAnimation();
        load.playAnimation();


        new Handler().postDelayed(() -> {
            Intent intent = new Intent(splash2.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}
