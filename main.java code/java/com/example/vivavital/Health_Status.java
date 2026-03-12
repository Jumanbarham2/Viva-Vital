package com.example.vivavital;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
public class Health_Status extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_status);

        findViewById(R.id.btn_daily_measurements).setOnClickListener(v ->
                startActivity(new Intent(this, Daily_Measurements.class)));

        findViewById(R.id.btn_vital_measurements).setOnClickListener(v ->
                startActivity(new Intent(this, Vital_Measurements.class)));

        findViewById(R.id.btn_hba1c).setOnClickListener(v ->
                startActivity(new Intent(this, Hba1c.class)));

        findViewById(R.id.btn_appointment_reminder).setOnClickListener(v ->
                startActivity(new Intent(this, Appointment_Reminder2.class)));
    }
}