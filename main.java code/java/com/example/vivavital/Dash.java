package com.example.vivavital;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Dash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        ImageButton mealPlanButton = findViewById(R.id.dashweek);
        mealPlanButton.setOnClickListener(v -> openPdfUrl());
    }

    private void openPdfUrl() {
        String url = "https://www.nhlbi.nih.gov/sites/default/files/publications/WeekOnDASH.pdf";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        // Verify that there's an app to handle this intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Handle case where no PDF viewer is installed
            // You could show a toast or open in browser instead
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://docs.google.com/viewer?url=" + url));
            startActivity(browserIntent);
        }
    }

}