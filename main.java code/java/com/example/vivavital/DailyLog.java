package com.example.vivavital;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class DailyLog extends AppCompatActivity {
    private LinearLayout dotsLayout;
    private int[] images = {R.id.vital, R.id.okay, R.id.not_well};
    private int[] buttonIds = {R.id.btnVital, R.id.btnOkay, R.id.btnNotWell};
    private int currentPosition = 0;
    private int[] buttonColors = {R.color.green, R.color.yellow, R.color.red};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_log);

        Button btnNext = findViewById(R.id.btnNext);
        TextView btnSkip = findViewById(R.id.btnSkip);

        btnSkip.setOnClickListener(v -> startActivity(new Intent(this, MainPage.class)));
        btnNext.setOnClickListener(v -> startActivity(new Intent(this, DailyLog2.class)));

        // Initialize all images
        ((ImageView)findViewById(R.id.vital)).setImageResource(R.drawable.vital);
        ((ImageView)findViewById(R.id.okay)).setImageResource(R.drawable.okay);
        ((ImageView)findViewById(R.id.not_well)).setImageResource(R.drawable.not_well);

        // Initialize dots layout
        dotsLayout = findViewById(R.id.dotsLayout);
        setupDots();  // Call this first to create dots

        // Show initial image
        showImage(0);

        // Set up button click listeners
        for (int i = 0; i < buttonIds.length; i++) {
            int position = i;
            findViewById(buttonIds[i]).setOnClickListener(v -> {
                showImage(position);
                updateButtonColors(position);
            });
        }
    }

    private void showImage(int position) {
        // Hide all images first
        for (int id : images) {
            findViewById(id).setVisibility(View.GONE);
        }
        // Show selected image
        findViewById(images[position]).setVisibility(View.VISIBLE);
        currentPosition = position;
        updateDots();
    }

    private void updateButtonColors(int selectedPosition) {
        for (int i = 0; i < buttonIds.length; i++) {
            Button button = findViewById(buttonIds[i]);
            int colorRes = (i == selectedPosition) ? buttonColors[i] : R.color.blue;
            button.setBackgroundTintList(ContextCompat.getColorStateList(this, colorRes));
        }
    }

    private void setupDots() {
        // Clear any existing dots
        dotsLayout.removeAllViews();

        // Create 3 dots (one for each image)
        for (int i = 0; i < images.length; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageDrawable(ContextCompat.getDrawable(this,
                    i == currentPosition ? R.drawable.active_dot : R.drawable.inactive_dots));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dot, params);
        }
    }

    private void updateDots() {
        // Update all dots to reflect current position
        for (int i = 0; i < dotsLayout.getChildCount(); i++) {
            ImageView dot = (ImageView) dotsLayout.getChildAt(i);
            dot.setImageDrawable(ContextCompat.getDrawable(this,
                    i == currentPosition ? R.drawable.active_dot : R.drawable.inactive_dots));
        }
    }
}