package com.example.ratatatcat.helpers;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FbModule {
    private String fbUrl = "https://ratatatcatnew-default-rtdb.firebaseio.com/";
    private static FbModule instance;
    private Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users, decks, turnCount;

    private FbModule(Context context) {
        this.context = context;
        firebaseDatabase = FirebaseDatabase.getInstance(fbUrl);
        users = firebaseDatabase.getReference("users");
        decks = firebaseDatabase.getReference("decks");
        turnCount = firebaseDatabase.getReference("turnCount");

        turnCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
