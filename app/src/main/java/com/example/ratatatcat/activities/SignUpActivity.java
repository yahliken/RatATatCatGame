package com.example.ratatatcat.activities;

import android.annotation.SuppressLint;
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
    private FbModule instance;
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

        instance = FbModule.getInstance(this);
        instance.setContext(this);

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
    //הולך לפיירבייס לצומת משתמשים ובודק אם קיימת רשומה עם אותו שם
    //אם הפעולה הצליחה בודק אם אותו צומת קיים או שהוא ריק - לא קיים אחרת רושם ארור
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
        instance.getUsers().child(userName).get().addOnSuccessListener(dataSnapshot -> {
            if(dataSnapshot.exists()){
                Toast.makeText(this, "user already exist", Toast.LENGTH_SHORT).show();
            }
            else {
                User user = new User(etUserNameS.getText().toString(), etPasswordS.getText().toString());
                //הולך לפיירבייס לצומת משתמשים ויוצר או מנווט בין הרשומות שם לפי הערך המיוחד להן - במקרה זה שם המשתמש. אם אין רשומה כזו הוא יוצר אותה
                instance.getUsers().child(user.getUserName()).setValue(user);
                UserDetails.getInstance(this).setUserName(user.getUserName());
                Toast.makeText(this, "user added", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "error occurred", Toast.LENGTH_SHORT).show();
        });

    }

    //אותו קטע קוד רק הפוך בתנאי
    //אם נמצא צומת כזה הוא יוצר משתמש עם נתוני אותו צומת ובודק שגם הסיסמה מתאימה
    private void logIn() {
        String userName = etUserNameL.getText().toString();
        String password = etPasswordL.getText().toString();

        if (userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        instance.getUsers().child(userName).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                User user = new User();
                user.setUserName(dataSnapshot.child("userName").getValue(String.class));
                user.setPassword(dataSnapshot.child("password").getValue(String.class));
                if (user.getPassword() != null && user.getPassword().equals(password)) {
                    UserDetails.getInstance(this).setUserName(userName);
                    Toast.makeText(this, "user logged in", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "incorrect password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "user does not exist", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}