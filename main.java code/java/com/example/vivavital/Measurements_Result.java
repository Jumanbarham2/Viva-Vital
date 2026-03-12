package com.example.vivavital;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Measurements_Result extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements_result);

        // Get data from intent
        int systolic = getIntent().getIntExtra("SYSTOLIC", 0);
        int diastolic = getIntent().getIntExtra("DIASTOLIC", 0);
        int glucose = getIntent().getIntExtra("GLUCOSE", 0);
        String measurementTime = getIntent().getStringExtra("MEASUREMENT_TIME");
        String reminderTime = getIntent().getStringExtra("REMINDER_TIME");

        // Initialize views
        TextView bpResultView = findViewById(R.id.bp_result);
        TextView glucoseResultView = findViewById(R.id.glucose_result);
        TextView bpDetailsView = findViewById(R.id.bp_details);
        TextView glucoseDetailsView = findViewById(R.id.glucose_details);
        TextView measurementTimeView = findViewById(R.id.measurement_time);
        TextView reminderTimeView = findViewById(R.id.reminder_time);
        Button doneButton = findViewById(R.id.done_button);

        // Set measurement context
        measurementTimeView.setText("Measured: " + measurementTime);
        reminderTimeView.setText("Next reminder: " + reminderTime);

        // Analyze and display BP results with values
        String bpResult = analyzeBloodPressure(systolic, diastolic);
        bpResultView.setText(bpResult + " (" + systolic + "/" + diastolic + " mmHg)");
        bpDetailsView.setText(getBloodPressureDetails(systolic, diastolic));

        // Analyze and display Glucose results with values
        String glucoseResult = analyzeBloodGlucose(glucose, measurementTime);
        glucoseResultView.setText(glucoseResult + " (" + glucose + " mg/dL)");
        glucoseDetailsView.setText(getGlucoseDetails(glucose, measurementTime));

        // Done button click handler
        doneButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }



    private String analyzeBloodPressure(int systolic, int diastolic) {
        if (systolic < 90 || diastolic < 60) {
            return "⚠️ Low Blood Pressure";
        } else if (systolic <= 120 && diastolic <= 80) {
            return "✅ Normal Blood Pressure";
        } else if (systolic <= 139 || diastolic <= 89) {
            return "⚠️ Elevated Blood Pressure";
        } else {
            return "🚨 High Blood Pressure";
        }
    }

    private String getBloodPressureDetails(int systolic, int diastolic) {
        if (systolic < 90 || diastolic < 60) {
            return "Your BP is too low (Hypotension)\n\n" +
                    "• Sit or lie down if feeling dizzy\n" +
                    "• Drink more water\n" +
                    "• Stand up slowly\n" +
                    "• Seek help if fainting occurs";
        } else if (systolic <= 120 && diastolic <= 80) {
            return "Your BP is in normal range\n\n" +
                    "• Maintain healthy habits\n" +
                    "• Continue regular monitoring\n" +
                    "• Manage stress effectively";
        } else if (systolic <= 139 || diastolic <= 89) {
            return "Your BP is slightly high\n\n" +
                    "• Reduce salt intake\n" +
                    "• Take deep breaths and relax\n" +
                    "• Take a short walk\n" +
                    "• Recheck after 30 minutes";
        } else {
            return "Your BP is dangerously high\n\n" +
                    "• Sit down and remain calm\n" +
                    "• Avoid physical exertion\n" +
                    "• Check again after 15 minutes\n" +
                    "• Call doctor if remains high";
        }
    }

    private String analyzeBloodGlucose(int glucose, String measurementTime) {
        if (glucose < 70) {
            return "⚠️ Low Blood Sugar";
        } else if ("Fasting".equals(measurementTime)) {
            if (glucose <= 99) return "✅ Normal Blood Sugar";
            else if (glucose <= 125) return "⚠️ Elevated Blood Sugar";
            else return "🚨 High Blood Sugar";
        } else { // Non-fasting measurement
            if (glucose <= 140) return "✅ Normal Blood Sugar";
            else if (glucose <= 199) return "⚠️ Elevated Blood Sugar";
            else return "🚨 High Blood Sugar";
        }
    }

    private String getGlucoseDetails(int glucose, String measurementTime) {
        if (glucose < 70) {
            return "Your glucose is too low (Hypoglycemia)\n\n" +
                    "• Consume 15g fast-acting carbs\n" +
                    "• Recheck after 15 minutes\n" +
                    "• Avoid driving until stable\n" +
                    "• Seek help if unconscious";
        } else if ("Fasting".equals(measurementTime)) {
            if (glucose <= 99) {
                return "Your fasting glucose is normal\n\n" +
                        "• Maintain balanced meals\n" +
                        "• Stay hydrated\n" +
                        "• Continue regular monitoring";
            } else if (glucose <= 125) {
                return "Your fasting glucose is elevated\n\n" +
                        "• Drink water to flush excess glucose\n" +
                        "• Take a 10-15 minute walk\n" +
                        "• Choose high-fiber foods\n" +
                        "• Discuss with doctor if consistent";
            } else {
                return "Your fasting glucose is dangerously high\n\n" +
                        "• Check for ketones if type 1 diabetic\n" +
                        "• Drink water (no sugar)\n" +
                        "• Administer insulin as prescribed\n" +
                        "• Seek medical help if over 250";
            }
        } else { // Non-fasting
            if (glucose <= 140) {
                return "Your glucose is normal for this measurement time\n\n" +
                        "• Maintain healthy eating habits\n" +
                        "• Continue regular exercise\n" +
                        "• Monitor after meals";
            } else if (glucose <= 199) {
                return "Your glucose is elevated for this measurement time\n\n" +
                        "• Reduce carb intake in next meal\n" +
                        "• Go for a walk\n" +
                        "• Drink plenty of water\n" +
                        "• Monitor trends over time";
            } else {
                return "Your glucose is critically high\n\n" +
                        "• Check ketones if type 1 diabetic\n" +
                        "• Drink water (no sugary drinks)\n" +
                        "• Administer insulin as prescribed\n" +
                        "• Seek emergency care if over 300";
            }
        }
    }
}