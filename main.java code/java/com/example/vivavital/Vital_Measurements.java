package com.example.vivavital;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Vital_Measurements extends AppCompatActivity {
    private EditText bloodPressureSystolicEditText;
    private EditText bloodPressureDiastolicEditText;
    private EditText heartRateEditText;
    private EditText oxygenSaturationEditText;
    private EditText bloodGlucoseEditText;
    private EditText respiratoryRateEditText;
    private EditText weightEditText;
    private Button updateButton;
    private TextView dateTextView;
    private Button editDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vital_measurements);

        initializeViews();
        setupCurrentDate();
        setupInputFilters();
        setupUpdateButton();
    }

    private void initializeViews() {
        bloodPressureSystolicEditText = findViewById(R.id.blood_pressure_systolic);
        bloodPressureDiastolicEditText = findViewById(R.id.blood_pressure_diastolic);
        heartRateEditText = findViewById(R.id.heart_rate);
        oxygenSaturationEditText = findViewById(R.id.oxygen_saturation);
        bloodGlucoseEditText = findViewById(R.id.blood_glucose);
        respiratoryRateEditText = findViewById(R.id.respiratory_rate);
        weightEditText = findViewById(R.id.weight);
        updateButton = findViewById(R.id.update_button);
        dateTextView = findViewById(R.id.date_text_view);
        editDateButton = findViewById(R.id.edit_date_button);
    }

    private void setupCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        dateTextView.setText(currentDate);

        editDateButton.setOnClickListener(v -> {
            dateTextView.setFocusableInTouchMode(true);
            dateTextView.setFocusable(true);
            dateTextView.setClickable(true);
            dateTextView.setInputType(InputType.TYPE_CLASS_DATETIME);
            dateTextView.requestFocus();
        });
    }

    private void setupInputFilters() {
        setupBloodPressureFields();
        setupHeartRateField();
        setupOxygenSaturationField();
        setupGlucoseField();
        setupRespiratoryRateField();
        setupWeightField();
    }

    private void setupBloodPressureFields() {
        bloodPressureSystolicEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        bloodPressureSystolicEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(3),
                new MinMaxInputFilter(70, 200)
        });

        bloodPressureDiastolicEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        bloodPressureDiastolicEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(3),
                new MinMaxInputFilter(40, 120)
        });
    }

    private void setupHeartRateField() {
        heartRateEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        heartRateEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(3),
                new MinMaxInputFilter(40, 220)
        });
    }

    private void setupOxygenSaturationField() {
        oxygenSaturationEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        oxygenSaturationEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(3),
                new MinMaxInputFilter(0, 100)
        });
    }

    private void setupGlucoseField() {
        bloodGlucoseEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        bloodGlucoseEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(3),
                new MinMaxInputFilter(50, 300)
        });
    }

    private void setupRespiratoryRateField() {
        respiratoryRateEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        respiratoryRateEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(2),
                (source, start, end, dest, dstart, dend) -> {
                    String newText = dest.toString().substring(0, dstart) + source + dest.toString().substring(dend);
                    if (!newText.isEmpty()) {
                        try {
                            int value = Integer.parseInt(newText);
                            if (value >= 40) return "";
                        } catch (NumberFormatException e) {
                            return "";
                        }
                    }
                    return null;
                }
        });
    }

    private void setupWeightField() {
        weightEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        weightEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(5),
                (source, start, end, dest, dstart, dend) -> {
                    String newText = dest.toString().substring(0, dstart) + source + dest.toString().substring(dend);
                    if (newText.contains(".") && newText.indexOf('.') != newText.lastIndexOf('.')) return "";
                    if (newText.contains(".")) {
                        int decimalIndex = newText.indexOf(".");
                        if (decimalIndex > 3 || (newText.length() - decimalIndex > 2)) return "";
                    } else if (newText.length() > 3) return "";
                    try {
                        float weight = Float.parseFloat(newText);
                        if (weight < 0 || weight > 300) return "";
                    } catch (NumberFormatException e) {
                        return "";
                    }
                    return null;
                }
        });
    }

    private void setupUpdateButton() {
        updateButton.setOnClickListener(v -> {
            if (validateAndSave()) {
                Toast.makeText(this, "Measurement Saved Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Appointment_Reminder.class));
                finish();
            }
        });
    }

    private boolean validateAndSave() {
        return validateBloodPressure() &&
                validateHeartRate() &&
                validateOxygenSaturation() &&
                validateGlucose() &&
                validateRespiratoryRate() &&
                validateWeight();
    }

    private boolean validateBloodPressure() {
        String systolicText = bloodPressureSystolicEditText.getText().toString();
        String diastolicText = bloodPressureDiastolicEditText.getText().toString();

        if (systolicText.isEmpty()) {
            bloodPressureSystolicEditText.setError("Enter systolic value");
            return false;
        }

        if (diastolicText.isEmpty()) {
            bloodPressureDiastolicEditText.setError("Enter diastolic value");
            return false;
        }

        int systolic = Integer.parseInt(systolicText);
        int diastolic = Integer.parseInt(diastolicText);

        if (systolic >= 170) {
            bloodPressureSystolicEditText.setError("Systolic must be < 170");
            return false;
        }
        if (diastolic >= 90) {
            bloodPressureDiastolicEditText.setError("Diastolic must be < 90");
            return false;
        }
        if (systolic <= diastolic) {
            bloodPressureSystolicEditText.setError("Systolic must be > diastolic");
            return false;
        }
        return true;
    }

    private boolean validateHeartRate() {
        String hrText = heartRateEditText.getText().toString();
        if (hrText.isEmpty()) {
            heartRateEditText.setError("Enter heart rate (40-220)");
            return false;
        }
        int hr = Integer.parseInt(hrText);
        if (hr < 40 || hr > 220) {
            heartRateEditText.setError("Must be between 40-220");
            return false;
        }
        return true;
    }

    private boolean validateOxygenSaturation() {
        String spo2Text = oxygenSaturationEditText.getText().toString();
        if (spo2Text.isEmpty()) {
            oxygenSaturationEditText.setError("Enter SpO2 value (≤100)");
            return false;
        }
        int spo2 = Integer.parseInt(spo2Text);
        if (spo2 > 100) {
            oxygenSaturationEditText.setError("Must be ≤100");
            return false;
        }
        return true;
    }

    private boolean validateGlucose() {
        String glucoseText = bloodGlucoseEditText.getText().toString();
        if (glucoseText.isEmpty()) {
            bloodGlucoseEditText.setError("Enter glucose level (50-300)");
            return false;
        }
        int glucose = Integer.parseInt(glucoseText);
        if (glucose < 50 || glucose > 300) {
            bloodGlucoseEditText.setError("Must be between 50-300");
            return false;
        }
        return true;
    }

    private boolean validateRespiratoryRate() {
        String rrText = respiratoryRateEditText.getText().toString();
        if (rrText.isEmpty()) {
            respiratoryRateEditText.setError("Enter respiratory rate (<40)");
            return false;
        }
        int rr = Integer.parseInt(rrText);
        if (rr >= 40) {
            respiratoryRateEditText.setError("Must be <40");
            return false;
        }
        return true;
    }

    private boolean validateWeight() {
        String weightText = weightEditText.getText().toString();
        if (weightText.isEmpty()) {
            weightEditText.setError("Enter weight");
            return false;
        }
        try {
            float weight = Float.parseFloat(weightText);
            if (weight <= 0 || weight > 300) {
                weightEditText.setError("Must be between 0-300 kg");
                return false;
            }
        } catch (NumberFormatException e) {
            weightEditText.setError("Invalid weight format");
            return false;
        }
        return true;
    }

    // MinMaxInputFilter class
    public static class MinMaxInputFilter implements InputFilter {
        private final int min;
        private final int max;

        public MinMaxInputFilter(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            try {
                String newVal = dest.toString().substring(0, dstart) + source + dest.toString().substring(dend);
                if (newVal.isEmpty()) return null;
                int input = Integer.parseInt(newVal);
                if (isInRange(min, max, input)) return null;
            } catch (NumberFormatException ignored) {
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
