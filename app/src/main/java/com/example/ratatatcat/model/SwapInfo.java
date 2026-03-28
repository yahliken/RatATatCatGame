package com.example.ratatatcat.model;

public class SwapInfo {
    public int myIndex;
    public int opponentIndex;
    public int swappingPlayer;
    public long timestamp; // במידה ועשינו את אותו חילוף בדיוק שאר הנתונים לא ישתנו ולא יקפוץ ONDATA
    //הוספת נתון זמן החילוף שתמיד משתנה אז תמיד יקפוץ ONDATA

    public SwapInfo() {} //בונה ריקה לפיירבייס

    public int getMyIndex() {
        return myIndex;
    }

    public void setMyIndex(int myIndex) {
        this.myIndex = myIndex;
    }

    public int getOpponentIndex() {
        return opponentIndex;
    }

    public void setOpponentIndex(int opponentIndex) {
        this.opponentIndex = opponentIndex;
    }

    public int getSwappingPlayer() {
        return swappingPlayer;
    }

    public void setSwappingPlayer(int swappingPlayer) {
        this.swappingPlayer = swappingPlayer;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public SwapInfo(int myIndex, int opponentIndex, int swappingPlayer) {
        this.myIndex = myIndex;
        this.opponentIndex = opponentIndex;
        this.swappingPlayer = swappingPlayer;
        this.timestamp = System.currentTimeMillis();
    }
}
