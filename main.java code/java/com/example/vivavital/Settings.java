package com.example.vivavital;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

class Settings extends AppCompatActivity {

    private static final String PREFS_NAME = "AppSettings";
    private static final int MIN_FONT_SIZE = 10;
    private static final int MAX_FONT_SIZE = 100;
    private static final int DEFAULT_FONT_SIZE = 50;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private float currentFontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeSharedPreferences();
        initializeViews();
        loadSettings();
        setupListeners();
    }

    private void initializeSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void initializeViews() {
        SeekBar fontSizeSeekBar = findViewById(R.id.font_size_seekbar);
        fontSizeSeekBar.setMax(MAX_FONT_SIZE - MIN_FONT_SIZE);
        fontSizeSeekBar.setProgress(DEFAULT_FONT_SIZE - MIN_FONT_SIZE);

        Spinner languageSpinner = findViewById(R.id.language_spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.languages_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
    }

    private void loadSettings() {
        try {
            setSwitchState(R.id.notifications_switch, "notifications", true);
            setSwitchState(R.id.medication, "medication_reminders", true);
            setSwitchState(R.id.Appointment, "appointment_reminders", true);
            setSwitchState(R.id.Health_Tips, "health_tips", true);
            setSwitchState(R.id.Daily, "daily_alerts", true);
            setSwitchState(R.id.switch1, "high_contrast", false);

            int savedFontSize = sharedPreferences.getInt("font_size", DEFAULT_FONT_SIZE);
            savedFontSize = Math.max(MIN_FONT_SIZE, Math.min(savedFontSize, MAX_FONT_SIZE));
            ((SeekBar) findViewById(R.id.font_size_seekbar)).setProgress(savedFontSize - MIN_FONT_SIZE);
            applyFontSize(savedFontSize);

            String savedLanguage = sharedPreferences.getString("language", "English");
            Spinner spinner = findViewById(R.id.language_spinner2);
            for (int i = 0; i < spinner.getCount(); i++) {
                if (spinner.getItemAtPosition(i).toString().equals(savedLanguage)) {
                    spinner.setSelection(i);
                    break;
                }
            }

            boolean highContrast = sharedPreferences.getBoolean("high_contrast", false);
            applyContrastMode(highContrast);

        } catch (Exception e) {
            Log.e("SettingsActivity", "Error loading settings", e);
            Toast.makeText(this, "Error loading settings", Toast.LENGTH_SHORT).show();
        }
    }

    private void setSwitchState(int switchId, String prefKey, boolean defaultValue) {
        Switch switchView = findViewById(switchId);
        if (switchView != null) {
            switchView.setChecked(sharedPreferences.getBoolean(prefKey, defaultValue));
        }
    }
    private void setupListeners() {
        SeekBar fontSizeSeekBar = findViewById(R.id.font_size_seekbar);
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int lastProgress = -1;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && progress != lastProgress) {
                    int actualProgress = progress + MIN_FONT_SIZE;
                    applyFontSize(actualProgress);
                    lastProgress = progress;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveSettings();
            }
        });

        Switch contrastSwitch = findViewById(R.id.switch1);
        if (contrastSwitch != null) {
            contrastSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                applyContrastMode(isChecked);
                saveSettings();
            });
        }

        Button updateButton = findViewById(R.id.update_button5);
        if (updateButton != null) {
            updateButton.setOnClickListener(v -> {
                saveSettings();
                recreate();
            });
        }
        setupSwitchListener(R.id.medication, "Medication reminders");
        setupSwitchListener(R.id.Appointment, "Appointment reminders");
        setupSwitchListener(R.id.Health_Tips, "Health tips");
        setupSwitchListener(R.id.Daily, "Daily alerts");
    }
    private void setupSwitchListener(int switchId, String toastMessage) {
        Switch switchView = findViewById(switchId);
        if (switchView != null) {
            switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String message = toastMessage + (isChecked ? " enabled" : " disabled");
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            });
        }
    }
    private void applyFontSize(int progress) {
        try {
            progress = Math.max(MIN_FONT_SIZE, Math.min(progress, MAX_FONT_SIZE));

            float scale = 0.8f + ((progress - MIN_FONT_SIZE) / (float)(MAX_FONT_SIZE - MIN_FONT_SIZE)) * 0.8f;
            currentFontSize = 16 * scale;

            ViewGroup root = findViewById(android.R.id.content);
            applyFontSizeToViews(root, currentFontSize);

        } catch (Exception e) {
            Log.e("SettingsActivity", "Error applying font size", e);
        }
    }

    private void applyFontSizeToViews(ViewGroup parent, float size) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            } else if (child instanceof ViewGroup) {
                applyFontSizeToViews((ViewGroup) child, size);
            }
        }
    }

    private void applyContrastMode(boolean enabled) {
        try {
            if (enabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } catch (Exception e) {
            Log.e("SettingsActivity", "Error applying contrast mode", e);
        }
    }
    private void saveSettings() {
        try {
            saveSwitchState(R.id.notifications_switch, "notifications");
            saveSwitchState(R.id.medication, "medication_reminders");
            saveSwitchState(R.id.Appointment, "appointment_reminders");
            saveSwitchState(R.id.Health_Tips, "health_tips");
            saveSwitchState(R.id.Daily, "daily_alerts");
            saveSwitchState(R.id.switch1, "high_contrast");

            int fontSizeProgress = ((SeekBar) findViewById(R.id.font_size_seekbar)).getProgress() + MIN_FONT_SIZE;
            editor.putInt("font_size", fontSizeProgress);

            Spinner languageSpinner = findViewById(R.id.language_spinner2);
            if (languageSpinner != null) {
                editor.putString("language", languageSpinner.getSelectedItem().toString());
            }
            editor.apply();
            Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("SettingsActivity", "Error saving settings", e);
            Toast.makeText(this, "Error saving settings", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSwitchState(int switchId, String prefKey) {
        Switch switchView = findViewById(switchId);
        if (switchView != null) {
            editor.putBoolean(prefKey, switchView.isChecked());
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        saveSettings();
        onBackPressed();
        return true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        saveSettings();
    }
}