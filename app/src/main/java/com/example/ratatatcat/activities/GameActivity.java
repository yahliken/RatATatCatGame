package com.example.ratatatcat.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ratatatcat.logic.BoardGame;

public class GameActivity extends AppCompatActivity {

    public static int player;
    private BoardGame boardGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        player =getIntent().getIntExtra("player", 0);
        boardGame = new BoardGame(this);

        setContentView(boardGame);

    }

    public void setChanges() {
        boardGame.setChanges();
    }
}