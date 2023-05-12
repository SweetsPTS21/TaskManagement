package com.example.demoktra2.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FBUser {
    public static FirebaseUser current_user;

    public FBUser() {
    }

    public static FirebaseUser getCurrent_user() {
        return current_user;
    }

    public static void setCurrent_user(FirebaseUser current_user) {
        FBUser.current_user = current_user;
    }

    public static void signOut() {
        FBUser.current_user = null;
    }
}
