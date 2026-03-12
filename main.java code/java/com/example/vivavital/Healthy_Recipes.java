package com.example.vivavital;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Healthy_Recipes extends AppCompatActivity {
    private static final String TAG = "Healthy_Recipes";

    private RadioGroup mealTimeGroup;
    private MultiSpinner allergySpinner;
    private Spinner preferSpinner, preferenceSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthy_recipes);

        try {
            initializeViews();
            setupSpinners();
            setupEventHandlers();
        } catch (Exception e) {
            Log.e(TAG, "Initialization error", e);
            Toast.makeText(this, "Initialization failed", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void initializeViews() {
        mealTimeGroup = findViewById(R.id.mealTimeGroup);
        allergySpinner = findViewById(R.id.allergySpinner);
        preferSpinner = findViewById(R.id.preferSpinner);
        preferenceSpinner = findViewById(R.id.preferenceSpinner);
        Button searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(v -> performSearch());
    }

    public void setupSpinners() {
        try {
            // Setup Allergy Spinner
            List<String> allergyOptions = Arrays.asList(
                    getResources().getStringArray(R.array.allergy_options)
            );
            allergySpinner.setItems(allergyOptions, "Select Allergies", selected -> {
                if (selected != null && !selected.isEmpty()) {
                    Log.d(TAG, "Selected allergies: " + TextUtils.join(", ", selected));
                }
            });

            // Setup Prefer Spinner
            ArrayAdapter<CharSequence> preferAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.prefer_options,
                    android.R.layout.simple_spinner_item
            );
            preferAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            preferSpinner.setAdapter(preferAdapter);

            // Setup Preference Spinner
            ArrayAdapter<CharSequence> preferenceAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.preference_options,
                    android.R.layout.simple_spinner_item
            );
            preferenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            preferenceSpinner.setAdapter(preferenceAdapter);

        } catch (Exception e) {
            Log.e(TAG, "Spinner setup failed", e);
            throw new RuntimeException("Spinner initialization error", e);
        }
    }

    public void setupEventHandlers() {
        mealTimeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d(TAG, "Selected meal: " + getSelectedMeal());
        });
    }

    public void performSearch() {
        try {
            Intent intent = new Intent(this, shouldShowRecipes() ?
                    Recipe_Results.class : No_Results.class);

            intent.putExtra("meal", getSelectedMeal())
                    .putStringArrayListExtra("allergies",
                            new ArrayList<>(allergySpinner.getSelectedItems()))
                    .putExtra("prefer", preferSpinner.getSelectedItem().toString())
                    .putExtra("preference", preferenceSpinner.getSelectedItem().toString());

            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Search failed", e);
            Toast.makeText(this, "Search error", Toast.LENGTH_SHORT).show();
        }
    }

    public String getSelectedMeal() {
        int id = mealTimeGroup.getCheckedRadioButtonId();
        if (id == R.id.breakfast) return "Breakfast";
        if (id == R.id.lunch) return "Lunch";
        if (id == R.id.dinner) return "Dinner";
        if (id == R.id.snack) return "Snack";
        return "None";
    }

    public boolean shouldShowRecipes() {
        List<String> allergies = allergySpinner.getSelectedItems();
        String prefer = preferSpinner.getSelectedItem().toString();
        String preference = preferenceSpinner.getSelectedItem().toString();

        return !(prefer.equals("Vegan") ||
                preference.equals("Vegetarian") ||
                (!allergies.isEmpty() && !allergies.contains("None")));
    }
}