package com.example.demoktra2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demoktra2.model.FBUser;
import com.example.demoktra2.model.GGUser;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvName,tvDes;
    private EditText edUserEmail, edUserDisplayName, edUserPassword, edUserPhoto;
    private ImageView img;
    private Button btChoosePhoto, btSave, btBack;
    private FirebaseUser user;
    private GoogleSignInAccount account;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initViews();
        btSave.setOnClickListener(this);
        btBack.setOnClickListener(this);
        btChoosePhoto.setOnClickListener(this);
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        if (FBUser.getCurrent_user() != null){
            user = FBUser.getCurrent_user();
        }
        else {
            account = GGUser.getCurrent_user();
            btSave.setCursorVisible(false);
            btSave.setFocusable(false);
            btSave.setFocusableInTouchMode(false);
        }
        setUserInfo();
    }

    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvDes = findViewById(R.id.tvDes);
        edUserEmail = findViewById(R.id.edUserEmail);
        edUserDisplayName = findViewById(R.id.edUserDisplayName);
        edUserPassword = findViewById(R.id.edUserPassword);
        edUserPhoto = findViewById(R.id.edUserPhoto);
        btChoosePhoto = findViewById(R.id.btChoosePhoto);
        img = findViewById(R.id.img);
        btSave = findViewById(R.id.btSave);
        btBack = findViewById(R.id.btBack);
    }

    public void setUserInfo() {
        if (user != null){
            Picasso.get().load(user.getPhotoUrl()).into(img);
            tvName.setText(user.getDisplayName());
            tvDes.setText(user.getEmail());
            edUserEmail.setText(user.getEmail());
            edUserDisplayName.setText(user.getDisplayName());

            if(user.getPhotoUrl() != null) {
                edUserPhoto.setText(user.getPhotoUrl().toString());
            }
            else{
                edUserPhoto.setText("");
            }
        }
        else {
            Picasso.get().load(account.getPhotoUrl()).into(img);
            tvName.setText(account.getDisplayName());
            tvDes.setText(account.getEmail());
            edUserEmail.setText(account.getEmail());
            edUserEmail.setFocusable(false);
            edUserEmail.setFocusableInTouchMode(false);
            edUserEmail.setCursorVisible(false);

            edUserDisplayName.setText(account.getDisplayName());
            edUserDisplayName.setFocusable(false);
            edUserDisplayName.setFocusableInTouchMode(false);
            edUserDisplayName.setCursorVisible(false);


            edUserPhoto.setText(account.getPhotoUrl().toString());
            edUserPhoto.setFocusable(false);
            edUserPhoto.setFocusableInTouchMode(false);
            edUserPhoto.setCursorVisible(false);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == btSave){
            if (user != null) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(edUserDisplayName.getText().toString())
                        .setPhotoUri(Uri.parse(edUserPhoto.getText().toString()))
                        .build();

                user.updateEmail(edUserEmail.getText().toString());
                if (edUserPassword.getText().toString().length() > 0) {
                    user.updatePassword(edUserPassword.getText().toString());
                }

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UserInfoActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } else {
                finish();
            }
        }
        if(view == btChoosePhoto) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
        if(view == btBack){
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the URI of the selected photo
            Uri uri = data.getData();

            edUserPhoto.setText(uri.toString());

            // Upload the photo to Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child( user.getUid() + ".jpg");

            UploadTask uploadTask = imageRef.putFile(uri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Get the download URL for the uploaded file
                return imageRef.getDownloadUrl();
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    Log.d("TAG", "Upload photo successfully: " + downloadUri);
                    edUserPhoto.setText(downloadUri.toString());
                } else {
                    Log.w("TAG", "Upload photo failed.", task.getException());
                }
            }
        });

        }
    }
}