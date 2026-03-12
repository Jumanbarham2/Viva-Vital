package com.example.vivavital;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Forgot_Password extends AppCompatActivity {

    // Nullable views (initialized in onCreate)
    private EditText emailEditText;
    private Button resetButton;
    private ProgressBar progressBar;
    private TextView errorTextView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase Auth
        try {
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            showToast("Firebase initialization failed");
            finish();
            return;
        }

        // Initialize views with null checks
        emailEditText = findViewById(R.id.email_input);
        resetButton = findViewById(R.id.reset_button);
        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.error_text);

        // Verify all critical views are initialized
        if (emailEditText == null || resetButton == null || progressBar == null || errorTextView == null) {
            showToast("UI initialization error");
            finish();
            return;
        }

        resetButton.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        // Defensive null checks
        if (emailEditText == null || mAuth == null) {
            showError("System error. Please restart the app");
            return;
        }

        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            showError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please provide a valid email");
            return;
        }

        hideError();
        showProgress(true);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    showProgress(false);

                    if (task.isSuccessful()) {
                        showSuccess();
                    } else {
                        handleError(task.getException());
                    }
                });
    }

    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (resetButton != null) {
            resetButton.setEnabled(!show);
        }
    }

    private void showSuccess() {
        showToast("Check your email to reset your password");
        finish();
    }

    private void handleError(@Nullable Exception exception) {
        String errorMessage = "Something went wrong. Please try again later.";

        if (exception != null && exception.getMessage() != null) {
            String exceptionMessage = exception.getMessage();
            if (exceptionMessage.contains("user-not-found")) {
                errorMessage = "No account found with this email address";
            } else if (exceptionMessage.contains("invalid-email")) {
                errorMessage = "The email address is badly formatted";
            } else if (exceptionMessage.contains("network-request-failed")) {
                errorMessage = "Network error. Please check your connection";
            }
        }

        showError(errorMessage);
    }

    private void showError(String message) {
        if (errorTextView != null) {
            errorTextView.setText(message);
            errorTextView.setVisibility(View.VISIBLE);
        }
    }

    private void hideError() {
        if (errorTextView != null) {
            errorTextView.setVisibility(View.GONE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}