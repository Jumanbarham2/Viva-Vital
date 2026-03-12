package com.example.vivavital;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class LoginPage extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private TextView usernameError, passwordError, loginError;
    private LottieAnimationView lottieAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        lottieAnimation = findViewById(R.id.lottieAnimation);
        usernameEditText = findViewById(R.id.Username);
        passwordEditText = findViewById(R.id.Password);
        usernameError = findViewById(R.id.UsernameError);
        passwordError = findViewById(R.id.PasswordError);
        loginError = findViewById(R.id.LoginError);
        Button loginButton = findViewById(R.id.Login);
        TextView forgotPassword = findViewById(R.id.ForgotPassword);
        TextView registerText = findViewById(R.id.Register);

        lottieAnimation.setAnimation(R.raw.login_page);
        lottieAnimation.loop(true);
        lottieAnimation.playAnimation();

        loginButton.setOnClickListener(v -> validateLogin());

        forgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginPage.this, Forgot_Password.class));
        });

        registerText.setOnClickListener(v -> {
            startActivity(new Intent(LoginPage.this, RegisterPage.class));
        });
    }

    private void validateLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        usernameError.setVisibility(View.GONE);
        passwordError.setVisibility(View.GONE);
        loginError.setVisibility(View.GONE);

        boolean isValid = true;

        if (TextUtils.isEmpty(username)) {
            usernameError.setText("Username is required");
            usernameError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordError.setText("Password is required");
            passwordError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (isValid) {
            startActivity(new Intent(this, DailyLog.class));
            finish();
        }
    }
}