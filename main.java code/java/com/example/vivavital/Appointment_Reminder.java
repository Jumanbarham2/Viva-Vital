package com.example.vivavital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class Appointment_Reminder extends AppCompatActivity {
private TextView btnSkip;
private Button btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_reminder);
        TextView btnSkip = findViewById(R.id.btnSkip);
        Button btnNext = findViewById(R.id.btnNext);
        btnSkip.setOnClickListener(v -> {
            Intent intent = new Intent(Appointment_Reminder.this, MainPage.class);
            startActivity(intent);
            finish();
        });
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(Appointment_Reminder.this, Appointment_Reminder2.class);
            startActivity(intent);
            finish();
        });
    }
}