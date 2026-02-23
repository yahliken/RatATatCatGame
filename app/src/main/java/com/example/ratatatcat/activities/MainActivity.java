package com.example.ratatatcat.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ratatatcat.GameActivity;
import com.example.ratatatcat.InstructionsActivity;
import com.example.ratatatcat.R;
import com.example.ratatatcat.helpers.UserDetails;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSignUp, btnPlay, btnJoin, btnInstructions;
    private TextView tvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);

        btnJoin = findViewById(R.id.btnJoin);
        btnJoin.setOnClickListener(this);

        btnInstructions = findViewById(R.id.btnInstructions);
        btnInstructions.setOnClickListener(this);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);

        tvHello = findViewById(R.id.tvHello);
        tvHello.setText("");
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        if (UserDetails.getInstance(this).getUserName() != "") {
            tvHello.setText("Hello " + UserDetails.getInstance(this).getUserName());
            btnSignUp.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
            btnJoin.setVisibility(View.VISIBLE);
        }
        else {
            btnSignUp.setVisibility(View.VISIBLE);
            btnJoin.setVisibility(View.GONE);
            btnPlay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == btnSignUp){
            Intent i = new Intent(this, SignUpActivity.class);
            startActivity(i);
        }
        else if(view == btnInstructions){
            Intent i = new Intent(this, InstructionsActivity.class);
            startActivity(i);
        }
        else if (view == btnPlay) {
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            i.putExtra("player", 0);
            startActivity(i);
        }
        else if (view == btnJoin) {
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            i.putExtra("player", 1);
            startActivity(i);
        }
    }
    }