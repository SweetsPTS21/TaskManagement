package com.example.demoktra2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.demoktra2.model.FBUser;
import com.example.demoktra2.model.GGUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private CardView googleLogin, emailLogin;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        //Check last login Google account
        if (account != null) {
            Log.d("TAG", "Last login account");
            Log.d("TAG", "name: " + account.getDisplayName());
            Log.d("TAG", "id: " + account.getId());

            GGUser.setCurrent_user(account);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else {
            Log.d("TAG", "No last Google login account");
        }
        //Check last login Firebase account
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(mAuth != null && user != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            FBUser.setCurrent_user(mAuth.getCurrentUser());
            startActivity(intent);
        }
        else {
            Log.d("TAG", "No last Firebase login account");
        }

        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, 11);

            }
        });

        emailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, LoginByEmailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        googleLogin = findViewById(R.id.Signin);
        emailLogin = findViewById(R.id.EmailSignin);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==11) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignIn(task);
            GGUser.setCurrent_user(task.getResult());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
    private void handleSignIn(Task<GoogleSignInAccount> task) {
        GoogleSignInAccount account = task.getResult();
        if (account != null) {
            Log.d("TAG", "name: " + account.getDisplayName());
            Log.d("TAG", "id: " + account.getId());
        }
    }
    @Override
    public void onBackPressed() {
        // Do nothing to prevent going back
    }
}

