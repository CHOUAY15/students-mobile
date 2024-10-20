package com.example.projetws;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;

public class SplashActivity extends AppCompatActivity {
    private ImageView logo;
    private TextView appTitle;
    private LinearProgressIndicator progressBar;
    private TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize views
        logo = findViewById(R.id.logo);
        appTitle = findViewById(R.id.appTitle);
        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);

        // Start animations
        startAnimations();

        // Navigate to MainActivity after delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }

    private void startAnimations() {
        // Fade in and slide down animation for logo
        ObjectAnimator logoFade = ObjectAnimator.ofFloat(logo, View.ALPHA, 0f, 1f);
        ObjectAnimator logoSlide = ObjectAnimator.ofFloat(logo, View.TRANSLATION_Y, -100f, 0f);

        // Fade in and slide up animation for app title
        ObjectAnimator titleFade = ObjectAnimator.ofFloat(appTitle, View.ALPHA, 0f, 1f);
        ObjectAnimator titleSlide = ObjectAnimator.ofFloat(appTitle, View.TRANSLATION_Y, 50f, 0f);

        // Create animator set
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(logoFade, logoSlide, titleFade, titleSlide);
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}