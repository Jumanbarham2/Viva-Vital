package com.example.vivavital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.AppTheme);

        new Handler().postDelayed(() -> {
        startActivity(new Intent(MainActivity.this, LoginPage.class));
        finish();
    }, 3000);
    }
}