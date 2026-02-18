package com.example.ratatatcat.helpers;

import android.content.Context;

//מחלקה סינגלטון המכילה את שם המשתמש במידה והתחבר - אם לא זה ריק
//כדיי להציג שהמשתמש מחובר במסך הבית ולשנות את נראות המסך בהתאם
public class UserDetails {
    private String userName = "";
    private static UserDetails instance;

    private UserDetails(Context context) {
    }

    public static UserDetails getInstance(Context context) {
        if (instance == null) {
            instance = new UserDetails(context.getApplicationContext());
        }
        return instance;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }
}
