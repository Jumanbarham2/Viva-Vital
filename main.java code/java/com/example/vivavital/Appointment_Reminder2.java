package com.example.vivavital;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;

public class Appointment_Reminder2 extends AppCompatActivity {

    private EditText appointmentDate;
    private EditText appointmentType;
    private Button pickTimeButton;
    private Button saveButton;
    private TextView timeDisplayTextView;
    private int selectedHour = 14; // Default to 2:00 PM (14:00)
    private int selectedMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_reminder2);
        appointmentDate = findViewById(R.id.appointment_date);
        appointmentType = findViewById(R.id.appointment_type);
        pickTimeButton = findViewById(R.id.pick_time);
        saveButton = findViewById(R.id.save_button);
        timeDisplayTextView = findViewById(R.id.time_display);
        updateTimeDisplay();
        pickTimeButton.setOnClickListener(v -> showTimePickerDialog());
        saveButton.setOnClickListener(v -> {
            String date = appointmentDate.getText().toString();
            String type = appointmentType.getText().toString();
            String time = getFormattedTime();
            saveReminder(date, time, type);
        });
    }
    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minute1;
                    updateTimeDisplay();
                },
                selectedHour,
                selectedMinute,
                false
        );
        timePickerDialog.setTitle("Select Reminder Time");
        timePickerDialog.show();
    }
    private void updateTimeDisplay() {
        timeDisplayTextView.setText(String.format("Time of reminder is: [%s]", getFormattedTime()));
    }
    private String getFormattedTime() {
        String amPm = selectedHour < 12 ? "AM" : "PM";
        int displayHour = selectedHour == 0 ? 12 : (selectedHour > 12 ? selectedHour - 12 : selectedHour);
        return String.format(Locale.getDefault(), "%d:%02d %s", displayHour, selectedMinute, amPm);
    }
    private void saveReminder(String date, String time, String type) {
        System.out.println("Saving reminder:");
        System.out.println("Date: " + date);
        System.out.println("Time: " + time);
        System.out.println("Type: " + type);

        if (date.isEmpty() || type.isEmpty()) {
            System.out.println("Error: All fields must be filled");
            return;
        }
    }
}