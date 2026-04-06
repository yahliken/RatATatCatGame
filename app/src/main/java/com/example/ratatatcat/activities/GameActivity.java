package com.example.ratatatcat.activities;

import android.app.Dialog;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ratatatcat.helpers.FbModule;
import com.example.ratatatcat.logic.BoardGame;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;

public class GameActivity extends AppCompatActivity {

    public static int player;
    private BoardGame boardGame;
    private FbModule instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        player =getIntent().getIntExtra("player", 0);
        instance = FbModule.getInstance(this);
        boardGame = new BoardGame(this);
        setContentView(boardGame);

    }

    public void setChanges() {
        boardGame.setChanges();
    }

    // נקרא מFbModule כשהיריב לחץ על rat-a-tat-cat
    public void triggerGameOver() {
        boardGame.triggerGameOver();
    }

    //  מקפיץ דיאלוג סיום משחק עם תוצאות וכפתור חזרה לMAIN
    public void showGameOverDialog(int myScore, int opponentScore) {
        String title, results;

        if (myScore < opponentScore) {
            title = "YOU WON";
        }
        else if (myScore > opponentScore) {
            title = "YOU LOST";
        }
        else {
            title = "It's a Tie!";
        }

        results = "Your score: " + myScore + "\nOpponent's score: " + opponentScore;

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(results)
                .setCancelable(false)
                .setPositiveButton("Home", (dialog, which) -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .show();
    }
}