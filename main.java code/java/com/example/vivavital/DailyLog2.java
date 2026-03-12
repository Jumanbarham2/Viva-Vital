package com.example.vivavital;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DailyLog2 extends AppCompatActivity {

    private Spinner spinner;
    private List<String> items;
    private ArrayAdapter<String> adapter;
    private Button saveLog;
    private static final String OTHER_OPTION = "Other...";
    private RadioGroup activityGroup;
    private RadioGroup stressGroup;
    private TextView activityAdviceText;
    private TextView stressAdviceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_log2);

        saveLog = findViewById(R.id.saveLog);
        activityGroup = findViewById(R.id.activityGroup);
        stressGroup = findViewById(R.id.stressGroup);

        // Initialize advice TextViews
        activityAdviceText = new TextView(this);
        stressAdviceText = new TextView(this);

        // Configure activity advice text
        activityAdviceText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        activityAdviceText.setTextSize(14);
        activityAdviceText.setPadding(16, 8, 16, 0);

        // Configure stress advice text
        stressAdviceText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        stressAdviceText.setTextSize(14);
        stressAdviceText.setPadding(16, 8, 16, 0);

        // Add the TextViews to the layout
        LinearLayout container = findViewById(R.id.container); // You'll need to add this ID to your LinearLayout
        int activityIndex = container.indexOfChild(findViewById(R.id.activityGroup)) + 1;
        int stressIndex = container.indexOfChild(findViewById(R.id.stressGroup)) + 1;
        container.addView(activityAdviceText, activityIndex);
        container.addView(stressAdviceText, stressIndex);

        spinner = findViewById(R.id.Spinner);
        items = new ArrayList<>();

        items.add("Fatigue");
        items.add("Dizziness");
        items.add("Headache");
        items.add("Shortness Of Breath");
        items.add("Difficulty Concentrating");
        items.add("Irregular heartbeat");
        items.add(OTHER_OPTION);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        saveLog.setOnClickListener(v -> {
            startActivity(new Intent(DailyLog2.this, MainPage.class));
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                if (selectedItem.equals(OTHER_OPTION)) {
                    CustomInputDialog();
                } else {
                    Toast.makeText(DailyLog2.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set up radio group listeners
        activityGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.activityNo) {
                activityAdviceText.setText("Try to move for at least 15 minutes to maintain your energy levels.");
                activityAdviceText.setVisibility(View.VISIBLE);
            } else {
                activityAdviceText.setVisibility(View.GONE);
            }
        });

        // In your activity's onCreate() method
        RadioGroup stressGroup = findViewById(R.id.stressGroup);
        TextView stressAdviceText = findViewById(R.id.stressAdviceText);

        stressGroup.setOnCheckedChangeListener((group, checkedId) -> {
             if (checkedId == R.id.stressYes) {
                stressAdviceText.setText("Stress Breathing! Inhale through your nose for 4 seconds, hold for 7, and exhale through your mouth for 8. Repeat 4 times to calm your stress.");
                stressAdviceText.setVisibility(View.VISIBLE);
            } else {
                stressAdviceText.setVisibility(View.GONE);
            }
        });
    }

    private void CustomInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Your symptoms:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String customText = input.getText().toString().trim();
            if (!customText.isEmpty()) {
                items.add(items.size() - 1, customText);
                adapter.notifyDataSetChanged();
                spinner.setSelection(items.indexOf(customText));
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}