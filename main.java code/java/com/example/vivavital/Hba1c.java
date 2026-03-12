package com.example.vivavital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Hba1c extends AppCompatActivity {
private EditText hba1cEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hba1c);

              hba1cEditText = findViewById(R.id.edit);
             Button saveButton = findViewById(R.id.save);
                saveButton.setOnClickListener(v -> validateAndSaveHba1c());
            }

            private void validateAndSaveHba1c() {
                String input = hba1cEditText.getText().toString().trim();

                if (input.isEmpty()) {
                    showError("Please enter a valid HbA1c percentage , between 4.0% and 14.0%");
                    return;
                }

                try {
                    String cleanInput = input.replace("%", "");
                    float value = Float.parseFloat(cleanInput);

                    if (value < 4.0f) {
                        showError("Entered HbA1c value is unusually low. Please enter a value between 4.0% and 14.0%.");
                    } else if (value > 14.0f) {
                        showError("Entered HbA1c value is unusually high. Please enter a value between 4.0% and 14.0%.");
                    } else {
                        saveHba1cValue(value);
                        startActivity(new Intent(this, Appointment_Reminder.class));
                    }
                } catch (NumberFormatException e) {
                    showError("Please enter a valid HbA1c percentage (e.g., 6.3%).");
                }
            }

            private void showError(String message) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                hba1cEditText.requestFocus();
            }

            private void saveHba1cValue(float value) {
                SharedPreferences prefs = getSharedPreferences("HealthPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putFloat("hba1c_value", value);
                editor.apply();
                Toast.makeText(this, "HbA1c value saved: " + value + "% ", Toast.LENGTH_SHORT).show();
            }
        }
