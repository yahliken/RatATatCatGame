package com.example.ratatatcat.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ratatatcat.R;

public class InstructionsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnX, btnAI;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_instructions);


        btnX = findViewById(R.id.btnX);
        btnX.setOnClickListener(this);

        btnAI = findViewById(R.id.btnAI);
        btnAI.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v == btnX){
            finish();
        } else if (v == btnAI) {
            Intent i = new Intent(InstructionsActivity.this, InstructionsAIActivity.class);
            startActivity(i);
        }
    }
}