package com.example.warehouse.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.warehouse.R;
import com.example.warehouse.database.DatabaseHelper;

public class LoginActivity extends Activity {
    private DatabaseHelper databaseHelper;
    private EditText usernameInput;
    private EditText passwordInput;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        errorMessage = findViewById(R.id.login_error);
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> login());
    }

    private void login() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        usernameInput.setError(null);
        passwordInput.setError(null);
        errorMessage.setVisibility(View.GONE);

        if (username.isEmpty()) {
            usernameInput.setError(getString(R.string.required_field));
            return;
        }
        if (password.isEmpty()) {
            passwordInput.setError(getString(R.string.required_field));
            return;
        }

        if (databaseHelper.checkLogin(username, password)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            errorMessage.setText(R.string.login_failed);
            errorMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
