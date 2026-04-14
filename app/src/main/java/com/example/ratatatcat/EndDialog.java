package com.example.ratatatcat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ratatatcat.activities.MainActivity;

public class EndDialog extends Dialog implements View.OnClickListener {

    private TextView tvTitle, tvResults;
    private Context context;
    private Button btnHome;
    private int mySum, opponentSum;
    public EndDialog(@NonNull Context context, int mySum, int opponentSum) {
        super(context);
        this.context = context;
        this.mySum = mySum;
        this.opponentSum = opponentSum;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_end_dialog);

        tvTitle = findViewById(R.id.tvTitle);
        tvResults = findViewById(R.id.tvResults);

        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(this);

        //בדיקה מי ניצח לפי הנתונים בפעולה בונה שנקבל
        if(mySum > opponentSum){
            tvTitle.setText("YOU WON!");
        }
        else if(mySum < opponentSum){
            tvTitle.setText("YOU LOST!");
        }
        else {
            tvTitle.setText("DRAW!");
        }

        tvResults.setText(mySum +" VS "+ opponentSum);


    }

    @Override
    public void onClick(View view) {
        if(view == btnHome){
            dismiss();
            Intent i = new Intent(context, MainActivity.class);
            context.startActivity(i);
        }
    }
}