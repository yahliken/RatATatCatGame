package com.example.ratatatcat.model;


import com.example.ratatatcat.R;

public class Card {
    private int value;
    private int idFront;
    private int idBack;
    private int idShown;

    public Card (int value, int idFront, int idShown){
        this.value = value;
        this.idFront = idFront;
        this.idBack = R.drawable.back;
        this.idShown = idShown;
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
}
