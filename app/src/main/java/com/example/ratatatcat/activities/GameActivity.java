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

        instance = FbModule.getInstance(this);
        player =getIntent().getIntExtra("player", 0);
        boardGame = new BoardGame(this);
        instance = FbModule.getInstance(this);
        instance.setContext(this);
        setContentView(boardGame);

    }

    public void setChanges() {
        boardGame.setChanges();
    }
}