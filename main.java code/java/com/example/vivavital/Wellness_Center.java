package com.example.vivavital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class Wellness_Center extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellness_center);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            setImageClickListeners();
        }}
        public void setImageClickListeners(){
            findViewById(R.id.dash).setOnClickListener(v ->
                    openActivity("Dash Diabetes Guide", Dash.class));

            findViewById(R.id.exercise).setOnClickListener(v ->
                    openActivity("Recommended Exercises", Exercise_Rec.class));

            findViewById(R.id.meal_plan).setOnClickListener(v ->
                    openActivity("Healthy Meal Plans", Meal_Plan.class));

            findViewById(R.id.latest).setOnClickListener(v ->
                    openActivity("Latest Health Updates", Latest_Updates.class));

            findViewById(R.id.smoking).setOnClickListener(v ->
                    openActivity("Deadly Trio Information", Deadly_Trio.class));

            findViewById(R.id.consultant1).setOnClickListener(v ->
                    openActivity("Consultant 1 Profile", Consultant_1.class));

            findViewById(R.id.consultant2).setOnClickListener(v ->
                    openActivity("Consultant 2 Profile", Consultant_2.class));

            findViewById(R.id.consultant3).setOnClickListener(v ->
                    openActivity("Consultant 3 Profile", Consultant_3.class));

            findViewById(R.id.idf).setOnClickListener(v ->
                    openWebLink("https://www.idf.org"));

            findViewById(R.id.hd).setOnClickListener(v ->
                    openWebLink("https://www.heartfoundation.org"));

            findViewById(R.id.royal).setOnClickListener(v ->
                    openWebLink("https://www.royal.org"));
        }

        private void openActivity (String title, Class < ?>activityClass){
            startActivity(new Intent(this, activityClass));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        private void openWebLink (String url){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }


