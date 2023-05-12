package com.example.demoktra2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demoktra2.databinding.ActivityMainBinding;
import com.example.demoktra2.model.FBUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginByEmailActivity extends AppCompatActivity {

    private EditText email, password;
    private TextView tv_forgot_password, tv_SignUp;
    private Button btnSignIn;
    private FloatingActionButton fab_back;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_email);
        initViews();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginByEmailActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginByEmailActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        tv_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginByEmailActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();

        if(TextUtils.isEmpty(emailStr)) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passwordStr)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginByEmailActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginByEmailActivity.this, MainActivity.class);
                            FBUser.setCurrent_user(mAuth.getCurrentUser());
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(LoginByEmailActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initViews() {
        email = findViewById(R.id.ed_username);
        password = findViewById(R.id.ed_password);
        btnSignIn = findViewById(R.id.btnSignIn);
        fab_back = findViewById(R.id.fab_back);
        tv_SignUp = findViewById(R.id.tv_SignUp);
        tv_forgot_password = findViewById(R.id.tv_forgot_password);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
        // Do nothing to prevent going back
    }
}