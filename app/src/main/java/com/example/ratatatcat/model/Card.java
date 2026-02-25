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
}
