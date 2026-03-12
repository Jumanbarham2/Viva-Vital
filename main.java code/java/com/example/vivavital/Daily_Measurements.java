package com.example.vivavital;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Daily_Measurements extends AppCompatActivity {
    private Button pickTimeButton;
    private Button updateButton;
    private TextView timeReminderText;
    private EditText systolicEditText;
    private EditText diastolicEditText;
    private EditText glucoseLevelsEditText;
    private RadioGroup glucoseMeasuredOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_measurements);

        // Initialize views
        pickTimeButton = findViewById(R.id.pick_time);
        updateButton = findViewById(R.id.update);
        timeReminderText = findViewById(R.id.text3);
        systolicEditText = findViewById(R.id.button3);
        diastolicEditText = findViewById(R.id.dia);
        glucoseLevelsEditText = findViewById(R.id.glucose_levels);
        glucoseMeasuredOptions = findViewById(R.id.radio_group);

        setupInputs();
        pickTimeButton.setOnClickListener(v -> showTimePickerDialog());
        updateButton.setOnClickListener(v -> {
            if (validateAndUpdate()) {
                navigateToResults();
            }
        });
    }

    private void setupInputs() {
        // BP fields: 3-digit limit
        InputFilter[] maxThreeDigits = new InputFilter[]{
                new InputFilter.LengthFilter(3),
                (source, start, end, dest, dstart, dend) -> {
                    for (int i = start; i < end; i++) {
                        if (!Character.isDigit(source.charAt(i))) return "";
                    }
                    return null;
                }
        };

        systolicEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        systolicEditText.setFilters(maxThreeDigits);
        systolicEditText.setHint("Systolic");

        diastolicEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        diastolicEditText.setFilters(maxThreeDigits);
        diastolicEditText.setHint("Diastolic");

        // Glucose field (same as BP)
        glucoseLevelsEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        glucoseLevelsEditText.setFilters(maxThreeDigits);
        glucoseLevelsEditText.setHint("(mg/dL)");
    }

    private boolean validateAndUpdate() {
        // Validate BP
        if (!validateBP()) return false;

        // Validate Glucose
        if (!validateGlucose()) return false;

        // Validate Radio Group
        if (glucoseMeasuredOptions.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Select when glucose was measured", Toast.LENGTH_SHORT).show();
            return false;
        }

        Toast.makeText(this, "Measurements updated!", Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean validateBP() {
        String systolicStr = systolicEditText.getText().toString();
        if (systolicStr.isEmpty()) {
            systolicEditText.setError("Enter systolic value");
            return false;
        }
        int systolic = Integer.parseInt(systolicStr);
        if (systolic < 70 || systolic > 250) {
            systolicEditText.setError("Must be 70-250");
            return false;
        }

        String diastolicStr = diastolicEditText.getText().toString();
        if (diastolicStr.isEmpty()) {
            diastolicEditText.setError("Enter diastolic value");
            return false;
        }
        int diastolic = Integer.parseInt(diastolicStr);
        if (diastolic < 40 || diastolic > 150) {
            diastolicEditText.setError("Must be 40-150");
            return false;
        }
        return true;
    }

    private boolean validateGlucose() {
        String glucoseStr = glucoseLevelsEditText.getText().toString();
        if (glucoseStr.isEmpty()) {
            glucoseLevelsEditText.setError("Enter glucose level");
            return false;
        }
        int glucose = Integer.parseInt(glucoseStr);
        if (glucose < 20 || glucose > 600) {
            glucoseLevelsEditText.setError("Must be 20-600 mg/dL");
            return false;
        }
        return true;
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> {
                    String amPm = hourOfDay < 12 ? "am" : "pm";
                    int displayHour = hourOfDay % 12;
                    displayHour = (displayHour == 0) ? 12 : displayHour;
                    String time = String.format("%d:%02d%s", displayHour, minute1, amPm);
                    timeReminderText.setText("Time for reminder: " + time);
                },
                hour,
                minute,
                false
        );
        timePickerDialog.show();
    }

    private void navigateToResults() {
        // Get all values
        int systolic = Integer.parseInt(systolicEditText.getText().toString());
        int diastolic = Integer.parseInt(diastolicEditText.getText().toString());
        int glucose = Integer.parseInt(glucoseLevelsEditText.getText().toString());
        String measurementTime = getSelectedMeasurementTime();
        String reminderTime = timeReminderText.getText().toString().replace("Time for reminder: ", "");

        // Create intent and pass data
        Intent intent = new Intent(this, Measurements_Result.class);
        intent.putExtra("SYSTOLIC", systolic);
        intent.putExtra("DIASTOLIC", diastolic);
        intent.putExtra("GLUCOSE", glucose);
        intent.putExtra("MEASUREMENT_TIME", measurementTime);
        intent.putExtra("REMINDER_TIME", reminderTime);

        startActivity(intent);
    }

    private String getSelectedMeasurementTime() {
        int selectedId = glucoseMeasuredOptions.getCheckedRadioButtonId();
        if (selectedId == R.id.before_meal) return "Before meal";
        if (selectedId == R.id.after_meal) return "After meal";
        if (selectedId == R.id.fasting) return "Fasting";
        if (selectedId == R.id.none) return "None";
        return "";
    }
}