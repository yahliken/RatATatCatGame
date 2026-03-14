package com.example.ratatatcat.model;


import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.ratatatcat.R;

public class Card {
    private int value;
    private int idFront;
    private int idBack;
    private int idShown;
    private float x, y;

    public Card (int value, int idFront, int idShown){
        this.value = value;
        this.idFront = idFront;
        this.idBack = R.drawable.back;
        this.idShown = idShown;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Card(){
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getIdFront() {
        return idFront;
    }

    public void setIdFront(int idFront) {
        this.idFront = idFront;
    }

    public int getIdBack() {
        return idBack;
    }

    public void setIdBack(int idBack) {
        this.idBack = idBack;
    }

    public int getIdShown() {
        return idShown;
    }

    public void setIdShown(int idShown) {
        this.idShown = idShown;
    }
    public void Draw(Canvas canvas , Bitmap bitmap){
        canvas.drawBitmap(bitmap, x, y, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return value == card.value && idFront == card.idFront && x== card.x;
    }

    @Override
    public int hashCode() {
        int result = value;
        result = (int) (31 * result + idFront + x);
        return result;
    }
    //דרסתי את פונקציית ההשוואה שמקבל כל עצם (שיורש מאובייקט) כי ההשוואה הרגילה היא לפי רפרנס
    //וכל פעם שהפיירבייס שומר נתונים הרשימה שלי נטענת עם רפרנסים חדשים
    //לכן כאשר ניגשתי לפעולה שבודקת אם הקלף בתוך הרשימה של ההפוכים קיבלתי שגיאה כי נשמר שם הרפרנס הישן
    //לכן משווים עם משהו שלא השתנה
    //למה הדריסה גם של HASHCODE: כי כאשר אומרים ששני עצמים זהים בEQUALS אז מניחים שגם ההאש קוד זהים
    // בגלל שההאש קוד היה מספר שתלויי ברפרנס שיניתי שהוא יהיה תלויי בפרמטרים שאיתם אני משווה כעת עצם
}
