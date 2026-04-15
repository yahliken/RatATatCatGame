package com.example.ratatatcat.activities;

import android.app.Dialog;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ratatatcat.EndDialog;
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

        if(player == BoardGame.HOST) {
            // 1. קודם כל מנקים את ה-Firebase מנתוני המשחק הקודם
            instance.ClearFb();

            // 2. רק אז יוצרים את הלוח (שיקרא ל-startGame ב-GameModule)
            boardGame = new BoardGame(this);
        } else {
            // שחקן מצטרף רק יוצר את הלוח ומחכה לנתונים
            boardGame = new BoardGame(this);
        }
        //boardGame = new BoardGame(this);
        setContentView(boardGame);

    }

    public void setChanges() {
        boardGame.setChanges();
    }

    // הולך לפה כתוצאה משינוי בפיירבייס וכך גם לשחקן השני קופץ הדיאלוג
    public void GameOver() {
        boardGame.GameOver();
    }

    //  מקפיץ דיאלוג סיום משחק עם תוצאות וכפתור חזרה לMAIN
    public void showEndDialog(int mySum, int opponentSum) {
        EndDialog dialog = new EndDialog(this, mySum, opponentSum);
        dialog.show();
    }
}