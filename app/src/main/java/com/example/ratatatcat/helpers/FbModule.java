package com.example.ratatatcat.helpers;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FbModule {
    private String fbUrl = "https://ratatatcatnew-default-rtdb.firebaseio.com/";
    private static FbModule instance;
    private Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;

    private FbModule(Context context) {
        this.context = context;
        firebaseDatabase = FirebaseDatabase.getInstance(fbUrl);
        users = firebaseDatabase.getReference("users");
    }

    public static FbModule getInstance(Context context) {
        if (instance == null) {
            instance = new FbModule(context);
        }
        return instance;
    }

    public DatabaseReference getUsers() {
        return users;
    }
}
