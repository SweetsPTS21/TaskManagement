package com.example.demoktra2.dal;

import androidx.annotation.NonNull;

import com.example.demoktra2.model.FBUser;
import com.example.demoktra2.model.GGUser;
import com.example.demoktra2.model.UserTask;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    private FirebaseDatabase database;
    private DatabaseReference ref;
    public List<UserTask> userTaskList;

    public FirebaseHelper() {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        userTaskList = new ArrayList<>();
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public DatabaseReference getRef() {
        return ref;
    }



}
