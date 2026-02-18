package com.example.ratatatcat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ratatatcat.activities.MainActivity;
import com.example.ratatatcat.gemini.GeminiCallback;
import com.example.ratatatcat.gemini.GeminiManager;

public class InstructionsAIActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etQuestion;
    private Button btnSend, btnX;
    private TextView tvAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_instructions_aiactivity);

        etQuestion = findViewById(R.id.etQuestion);
        tvAnswer = findViewById(R.id.tvAnswer);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        btnX = findViewById(R.id.btnX);
        btnX.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnX) {
            Intent i = new Intent(InstructionsAIActivity.this, MainActivity.class);
            startActivity(i);
        }
        else {
            String q = etQuestion.getText().toString();
            String prompt = "The goal of the game is to end up with the lowest total score among your four face-down cards.\n" +
                    "Deal and Peek: Each player is dealt 4 face-down cards. At the start, you may peek only at your two outer cards (the far right and far left). Memorize them well!\n" +
                    "Draw from the Deck: On your turn, draw the top card from the face-down deck. You now have two options:\n" +
                    "Swap: Replace one of your 4 face-down cards with the card you just drew. Place the old card face-up in the discard pile.\n" +
                    "Discard: If the card you drew is high (a rat) or unwanted, place it directly into the discard pile.\n" +
                    "Using Action Cards: If you draw an Action Card from the deck, you can use its power (then discard it):\n" +
                    "Peek: Allows you to look at one of your own cards to decide if you should replace it.\n" +
                    "Swap: Allows you to trade one of your cards with an opponent’s card (without looking at them first).\n" +
                    "Draw 2: Gives you another chance to draw from the deck if the first card wasn't useful.\n" +
                    "Calling 'Rat-a-Tat Cat' Once you believe your cards have a low enough total, and then everyone reveals their cards\n"+"The question is: " + q+ "\n" + "Answer with no more than 30 words";

            GeminiManager.getInstance().sendMessage(prompt, new GeminiCallback() {
                @Override
                public void onSuccess(String result) {
                    tvAnswer.setText(result);
                }

                @Override
                public void onError(Throwable error) {
                    tvAnswer.setText("ERROR1");
                }

                @Override
                public void onError(Exception e) {
                    tvAnswer.setText("ERROR2");
                }
            });
        }
    }
}
