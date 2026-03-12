package com.example.vivavital;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Activity_Tracker extends AppCompatActivity {

    private final ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private TextView completionMessage;
    private int totalTasks = 8;
    private LinearLayout checkboxesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        TextView dateTextView = findViewById(R.id.day_month_year);
        TextView dailyProgress = findViewById(R.id.my_daily_progress);
        completionMessage = findViewById(R.id.completion_message);
        Button trackAnotherButton = findViewById(R.id.track_another_activity);
        Button doneButton = findViewById(R.id.done);
        checkboxesContainer = findViewById(R.id.checkboxes_container);

        String currentDate = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        dateTextView.setText(currentDate);

        int[] checkboxIds = {
                R.id.exercised_today,
                R.id.drank_water,
                R.id.medication_taken,
                R.id.eaten_balanced_meal,
                R.id.avoided_sugary_foods,
                R.id.daily,
                R.id.sleep_hours_checked,
                R.id.screen_break_checked
        };

        for (int id : checkboxIds) {
            CheckBox checkBox = findViewById(id);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateCompletionMessage());
            checkBoxes.add(checkBox);
        }

        trackAnotherButton.setOnClickListener(v -> showAddActivityDialog());
        doneButton.setOnClickListener(v -> {
            Toast.makeText(this, "Back to Main Page!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Activity_Tracker.this, MainPage.class));
            finish();
        });

        updateCompletionMessage();
    }
    public void showAddActivityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_activity, null);
        final EditText activityNameInput = dialogView.findViewById(R.id.activity_name_input);

        builder.setView(dialogView)
                .setTitle("Add New Activity")
                .setPositiveButton("Add", (dialog, which) -> {
                    String activityName = activityNameInput.getText().toString().trim();
                    if (!TextUtils.isEmpty(activityName)) {
                        addNewActivity(activityName);
                    } else {
                        Toast.makeText(this, "Please enter an activity name", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();

}
    public void addNewActivity(String activityName) {
        CheckBox newCheckBox = new CheckBox(this);
        newCheckBox.setText(activityName);
        newCheckBox.setTextSize(16);
        newCheckBox.setPadding(0, 16, 0, 16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 8, 0, 8);
        newCheckBox.setLayoutParams(params);

        try {
            checkboxesContainer.addView(newCheckBox);
            checkBoxes.add(newCheckBox);
            totalTasks++;
            newCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateCompletionMessage());
            Toast.makeText(this, "New activity added: " + activityName, Toast.LENGTH_SHORT).show();
            updateCompletionMessage();
        } catch (Exception e) {
            Log.e("Activity_Tracker", "Error adding view: " + e.getMessage());
            Toast.makeText(this, "Failed to add activity", Toast.LENGTH_SHORT).show();
        }
    }
    public void updateCompletionMessage() {
        int completedTasks = 0;
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox != null && checkBox.isChecked()) {
                completedTasks++;
            }
        }

        String message;
        if (completedTasks == totalTasks) {
            message = "Excellent! You've completed all " + totalTasks + " tasks today!";
        } else if (completedTasks == 0) {
            message = "Let's get started! You have " + totalTasks + " tasks to complete today.";
        } else if (completedTasks >= 6) {
            message = "Good progress! You've completed " + completedTasks + "/" + totalTasks + " tasks today.";
        } else
            message= "Well Done! you've completed"+ completedTasks+"/"+totalTasks+" tasks today, try a bit harder tomorrow!";

        completionMessage.setText(message);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}