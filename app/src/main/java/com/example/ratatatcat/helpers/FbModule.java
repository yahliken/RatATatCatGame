package com.example.ratatatcat.helpers;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.ratatatcat.activities.GameActivity;
import com.example.ratatatcat.logic.BoardGame;
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
    /*DatabaseReference users;*/
    DatabaseReference decks, turnCount;

    private FbModule(Context context) {
        this.context = context;
        firebaseDatabase = FirebaseDatabase.getInstance(fbUrl);
        /*users = firebaseDatabase.getReference("users");*/
        decks = firebaseDatabase.getReference("decks");
        turnCount = firebaseDatabase.getReference("turnCount");


        if(GameActivity.player== BoardGame.HOST)
        {
            ClearDecksFromFb();
        }

        /* turnCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        decks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null) {

                    if (snapshot.child("deck").exists()) {
                        updateList(snapshot.child("deck"), GameModule.deck);
                    }

                    if (snapshot.child("player1").exists()) {
                        updateList(snapshot.child("player1"), GameModule.player1);
                    }

                    if (snapshot.child("player2").exists()) {
                        updateList(snapshot.child("player2"), GameModule.player2);
                    }
                    if (snapshot.child("trash").exists()) {
                        updateList(snapshot.child("trash"), GameModule.trash);
                    }

                    // רק אם כל החפיסות הגיעו, נסמן שהנתונים קיימים
                    if(snapshot.hasChild("deck") && snapshot.hasChild("player1") && snapshot.hasChild("player2") && !GameModule.deck.isEmpty() && !GameModule.player1.isEmpty() && !GameModule.player2.isEmpty()) {
                        BoardGame.FbExist = true;
                    }

                    //בדיקה אם הcontext של הgameactivity
                    if (context instanceof GameActivity) {
                        ((GameActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((GameActivity) context).setChanges();
                            }
                        });
                    }
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
        //הcontext שיוחזר הוא חדש וכך נמנע שימוש בקיים למשל בsignup
        instance.context = context;
        return instance;
    }

    public void setContext (Context context){
        this.context=context;
    }


    public void setDeck(ArrayList<Card> arrayList, String deckName){
        //יוצר את החפיסה בדטהבייס בפעם הראשונה עבור כל חפיסה בנפרד
        DatabaseReference myRef = firebaseDatabase.getReference("decks/" + deckName +"/");
        myRef.setValue(arrayList);
    }


    public void ClearDecksFromFb(){
        //יוצר את החפיסה בדטהבייס בפעם הראשונה עבור כל חפיסה בנפרד
        DatabaseReference myRef = firebaseDatabase.getReference("decks");
        myRef.removeValue();
    }

    private void updateList(DataSnapshot snapshot, ArrayList<Card> list) {
        ArrayList<Card> tempList = new ArrayList<>(); // רשימה זמנית
        for (DataSnapshot child : snapshot.getChildren()) {
            Card card = child.getValue(Card.class);
            if (card != null) {
                tempList.add(card);
            }
        }
        // העדכון הממשי קורה בבת אחת - זה מונע מה-onDraw לתפוס רשימה ריקה
        list.clear();
        list.addAll(tempList);

    }
}
