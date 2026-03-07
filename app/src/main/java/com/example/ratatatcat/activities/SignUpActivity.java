package com.example.ratatatcat.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ratatatcat.R;
import com.example.ratatatcat.helpers.FbModule;
import com.example.ratatatcat.helpers.UserDetails;
import com.example.ratatatcat.model.User;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewFlipper viewFlipper;
    private TextView tvSwitchToLogIn, tvSwitchToSignUp;
    private Button btnSignUp, btnLogIn;
    private EditText etUserNameS, etPasswordS, etPasswordVerification, etUserNameL, etPasswordL;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        viewFlipper = findViewById(R.id.viewFlipper);

        tvSwitchToLogIn = findViewById(R.id.tvSwitchToLogIn);
        tvSwitchToLogIn.setOnClickListener(this);

        tvSwitchToSignUp = findViewById(R.id.tvSwitchToSignUp);
        tvSwitchToSignUp.setOnClickListener(this);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);

        btnLogIn = findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(this);

        etUserNameS = findViewById(R.id.etUserNameS);
        etPasswordS = findViewById(R.id.etPasswordS);
        etPasswordVerification = findViewById(R.id.etPasswordVerification);
        etUserNameL = findViewById(R.id.etUserNameL);
        etPasswordL = findViewById(R.id.etPasswordL);

    }

    @Override
    public void onClick(View view) {
        if(view == tvSwitchToLogIn){
            viewFlipper.showNext();
        }
        else if (view == tvSwitchToSignUp){
            viewFlipper.showPrevious();
        }
        else if(view == btnSignUp){
            signUp();
        }
        else if(view == btnLogIn){
            logIn();
        }
    }

    //שתי בדיקות האם אחד השדות ריקים או שהאימות סיסמה שגוי
    //בודק אם שם המשתמש קיים בSHARED PREFERENCES
    private void signUp() {
        String userName= etUserNameS.getText().toString();
        String password = etPasswordS.getText().toString();
        String passwordVerification = etPasswordVerification.getText().toString();

        if(userName.isEmpty() || password.isEmpty() || passwordVerification.isEmpty()){
            Toast.makeText(this, "one or more fields are empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(passwordVerification)) {
            Toast.makeText(this, "passwords do not match", Toast.LENGTH_SHORT).show();
            etPasswordVerification.setText("");
            return;
        }

        // שמירה מקומית במכשיר במקום בפיירבייס
        SharedPreferences sp = getSharedPreferences("Users", MODE_PRIVATE);

        // בדיקה אם המשתמש כבר קיים מקומית
        if(sp.contains(userName)) {
            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
        }
        else {
            sp.edit().putString(userName, password).apply(); // שמירת שם וסיסמה
            sp.edit().putString("currentLoggedUser", userName).apply(); // שמירת המשתמש המחובר כרגע
            Toast.makeText(this, "User added", Toast.LENGTH_SHORT).show();
            UserDetails.getInstance(this).setUserName(userName);
            finish();
        }
    }

    //אותו קטע קוד רק הפוך בתנאי
    private void logIn() {
        String userName = etUserNameL.getText().toString();
        String password = etPasswordL.getText().toString();

        if (userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sp = getSharedPreferences("Users", MODE_PRIVATE);
        String savedPassword = sp.getString(userName, null);

        if (sp.contains(userName) && savedPassword != null && savedPassword.equals(password)) {
            sp.edit().putString("currentLoggedUser", userName).apply();
            Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show();
            UserDetails.getInstance(this).setUserName(userName);
            finish();
        } else {
            Toast.makeText(this, "User does not exsit", Toast.LENGTH_SHORT).show();
        }
    }
}