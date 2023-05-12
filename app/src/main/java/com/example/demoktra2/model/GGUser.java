package com.example.demoktra2.model;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class GGUser {
    public static GoogleSignInAccount current_user;

    public GGUser() {
    }

    public static GoogleSignInAccount getCurrent_user() {
        return current_user;
    }

    public static void setCurrent_user(GoogleSignInAccount current_user) {
        GGUser.current_user = current_user;
    }

    public static void signOut() {
        GGUser.current_user = null;
    }
}
