package com.example.ratatatcat.helpers;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.ratatatcat.logic.GameModule;
import com.example.ratatatcat.model.Card;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

        //להוסיף פעולת עדכון אחרי כל תור שמה נשנה את הערך בCOUNT וננקה את החבילות בGAMEMODULE
        turnCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        decks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    GameModule.deck.clear();
                    DataSnapshot deckSnapshot = snapshot.child("deck");
                    for (DataSnapshot userSnapshot : deckSnapshot.getChildren()) {
                        Card currentCard = userSnapshot.getValue(Card.class);
                        GameModule.deck.add(currentCard);
                    }
                    //הuserSnapshot מייצג הפנייה לכל איברי הקלפים שמתחת לצומת
                    GameModule.player1.clear();
                    DataSnapshot player1Snapshot = snapshot.child("player1");
                    for (DataSnapshot userSnapshot : player1Snapshot.getChildren()) {
                        Card currentCard = userSnapshot.getValue(Card.class);
                        GameModule.player1.add(currentCard);
                    }
                    GameModule.player2.clear();
                    DataSnapshot player2Snapshot = snapshot.child("player2");
                    for (DataSnapshot userSnapshot : player2Snapshot.getChildren()) {
                        Card currentCard = userSnapshot.getValue(Card.class);
                        GameModule.player2.add(currentCard);
                    }
                    GameModule.trash.clear();
                    DataSnapshot trashSnapshot = snapshot.child("trash");
                    for (DataSnapshot userSnapshot : trashSnapshot.getChildren()) {
                        Card currentCard = userSnapshot.getValue(Card.class);
                        GameModule.trash.add(currentCard);
                    }
                    //להוסיף פעולת SETCHANGE שם נעשה INVALIDATE
                }


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

    public void setDeck(ArrayList<Card> arrayList, String deckName){
        //יוצר את החפיסה בדטהבייס בפעם הראשונה עבור כל חפיסה בנפרד
        DatabaseReference myRef = firebaseDatabase.getReference("Decks/" + deckName +"/");
        myRef.setValue(arrayList);
    }

    public void ClearDecksFromFb(){
        //יוצר את החפיסה בדטהבייס בפעם הראשונה עבור כל חפיסה בנפרד
        DatabaseReference myRef = firebaseDatabase.getReference("Decks");
        myRef.removeValue();
    }
}
