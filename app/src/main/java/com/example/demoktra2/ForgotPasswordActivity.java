package com.example.demoktra2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText ed_email;
    private Button btResetPassword;
    private FloatingActionButton fab_back;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initViews();

        mAuth = FirebaseAuth.getInstance();

        btResetPassword.setOnClickListener(this);
        fab_back.setOnClickListener(this);
    }

    private void initViews() {
        ed_email = findViewById(R.id.ed_email);
        btResetPassword = findViewById(R.id.btResetPassword);
        fab_back = findViewById(R.id.fab_back);
    }

    @Override
    public void onClick(View view) {
        String email = ed_email.getText().toString().trim();
        if (view == btResetPassword) {
            if (email.isEmpty()) {
                ed_email.setError("Email is required");
                ed_email.requestFocus();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                ed_email.setError("Enter a valid email");
                ed_email.requestFocus();
                return;
            }

            mAuth.fetchSignInMethodsForEmail(ed_email.getText().toString().trim())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> results = task.getResult().getSignInMethods();
                            if (results != null && !results.isEmpty()) {
                                // User exists
                                resetPassword();
                            } else {
                                // User does not exist in Firebase Authentication
                                Toast.makeText(this, "User with email does not exist", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Error occurred while checking user existence
                            Toast.makeText(this, "Try again! Something wrong happened!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } 
        if (view == fab_back) {
            finish();
        }
    }

    private void resetPassword() {
        String email = ed_email.getText().toString().trim();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ed_email.setText("");
                ed_email.requestFocus();
                Toast.makeText(this, "Check your email to reset your password", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ForgotPasswordActivity.this, LoginByEmailActivity.class));
                finish();
            }
            else {
                Toast.makeText(this, "Try again! Something wrong happened!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}