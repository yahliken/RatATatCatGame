package com.example.ratatatcat.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ratatatcat.helpers.FbModule;
import com.example.ratatatcat.logic.BoardGame;

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
}