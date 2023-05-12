package com.example.demoktra2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText ed_username, ed_password, ed_re_password;
    private TextView tv_SignIn;
    private Button btSignUp;
    private FloatingActionButton fab_back;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        mAuth = FirebaseAuth.getInstance();

        fab_back.setOnClickListener(this);
        btSignUp.setOnClickListener(this);
        tv_SignIn.setOnClickListener(this);
    }

    private void initViews() {
        ed_username = findViewById(R.id.ed_username);
        ed_password = findViewById(R.id.ed_password);
        ed_re_password = findViewById(R.id.ed_re_password);
        tv_SignIn = findViewById(R.id.tv_SignIn);
        btSignUp = findViewById(R.id.btSignUp);
        fab_back = findViewById(R.id.fab_back);
    }

    @Override
    public void onClick(View view) {
        if(view == fab_back){
            finish();
        }
        if(view == btSignUp){
            validate();
        }
        if(view == tv_SignIn){
            finish();
        }
    }

    private void register() {
        String email = ed_username.getText().toString().trim();
        String password = ed_password.getText().toString().trim();



        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User was successfully created
                        Toast.makeText(this, "Register Successfully, please login", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // User creation failed
                        Exception exception = task.getException();
                        // Handle exception
                        Log.e("TAG", "register: " + exception.getMessage());
                        Toast.makeText(this, "Try again! Something wrong happened!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void validate() {
        String email = ed_username.getText().toString().trim();
        String password = ed_password.getText().toString().trim();
        String re_password = ed_re_password.getText().toString().trim();
        if(email.isEmpty()){
            ed_username.setError("Email is required");
            ed_username.requestFocus();
            return;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            ed_username.setError("Enter a valid email");
            ed_username.requestFocus();
            return;
        }
        if(password.isEmpty()){
            ed_password.setError("Password is required");
            ed_password.requestFocus();
            return;
        }
        if(password.length() < 6){
            ed_password.setError("Password must be at least 6 characters");
            ed_password.requestFocus();
            return;
        }
        if(password.equals(re_password) == false){
            ed_re_password.setError("Password does not match");
            ed_re_password.requestFocus();
            return;
        }
        checkBeforeRegister();
    }
    public void checkBeforeRegister() {
        mAuth.fetchSignInMethodsForEmail(ed_username.getText().toString().trim())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> results = task.getResult().getSignInMethods();
                        if (results != null && !results.isEmpty()) {
                            // User exists
                            Toast.makeText(this, "User with email already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            // User does not exist in Firebase Authentication
                            register();
                        }
                    } else {
                        // Error occurred while checking user existence
                        Toast.makeText(this, "Try again! Something wrong happened!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}