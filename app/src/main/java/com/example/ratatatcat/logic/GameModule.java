package com.example.ratatatcat.logic;

import android.content.Context;

import com.example.ratatatcat.R;
import com.example.ratatatcat.activities.GameActivity;
import com.example.ratatatcat.helpers.FbModule;
import com.example.ratatatcat.model.Card;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import android.os.Handler;
import android.os.Looper;
import java.util.Random;

public class GameModule {
    private final int DRAW2 = -1, PEEK = -2, SWAP = -3;
    public static ArrayList<Card> deck = new ArrayList<Card>(), trash = new ArrayList<Card>(), player1 = new ArrayList<Card>(), player2 = new ArrayList<Card>();
    public static int currentTurn = 0; // מנהל מתחיל -0 ומצטרף זה -1
    public ArrayList<Card> revealedCards = new ArrayList<>();
    private Handler revealHandler = new Handler(Looper.getMainLooper());
    private Context context;
    private FbModule instance;

    public GameModule(Context context) {
        this.context = context;
    }

    private void newDeck(){
        deck.clear();
        int back = R.drawable.back;
        int [] cardsImg = {R.drawable.card0, R.drawable.card1, R.drawable.card2, R.drawable.card3, R.drawable.card4, R.drawable.card5, R.drawable.card6, R.drawable.card7, R.drawable.card8, R.drawable.card9, R.drawable.draw2, R.drawable.peek, R.drawable.swap};
        for (int i = 0; i < 10; i++) {

            if(i<9){
                for(int k=0; k<4; k++){
                    Card c = new Card(i, cardsImg[i], back);
                    deck.add(c);
                }
            }
            else {
                for(int k=0; k<9; k++){
                    Card c = new Card(i, cardsImg[i], back);
                    deck.add(c);
                }
            }
        }

        for (int i = 10; i < 13; i++) {

            if(i == 10){
                for(int k=0; k<3; k++){
                    Card c = new Card(DRAW2, cardsImg[i], back);
                    deck.add(c);
                }
            }
            if(i == 11){
                for(int k=0; k<3; k++){
                    Card c = new Card(PEEK, cardsImg[i], back);
                    deck.add(c);
                }
            }
            if(i == 12){
                for(int k=0; k<3; k++){
                    Card c = new Card(SWAP, cardsImg[i], back);
                    deck.add(c);
                }
            }

        }
    }

    public void shuffle(){
        int size = deck.size();
        ArrayList<Card> temp = new ArrayList<>();
        Random rnd = new Random();
        for (int i = 0; i < size; i++) {
            int x = rnd.nextInt(deck.size());
            temp.add(deck.get(x));
            deck.remove(x);
        }
        for (int i = 0; i < size; i++) {
            deck.add(temp.remove(0));
        }
    }

    public void startGame(){
        player1.clear();
        player2.clear();
        trash.clear();
        newDeck();
        shuffle();
        int count = 1;
        int i = 0;
        while (count != 9){
            Card c = deck.get(i);
            if(c.getValue() >= 0){
                if(player1.size() < 4){
                    player1.add(deck.remove(i));
                    count++;
                }
                else {
                    player2.add(deck.remove(i));
                    count++;
                }

            }
            i++;
        }
        shuffle();
        //בגלל שבדקנו שכולם מספרים נותרנו עם הרבה מיוחדים שדילגנו עליהם לכן נערבב שוב כדי שלא יהיה מצב שכל ההתחלה של הקופה מיוחדים
        setDecksFromFB();
        //תחילה רק המנהל משחק יעלה את החפיסות
        BoardGame.FbExist= true;
    }

    public void setDecksFromFB(){
        instance = FbModule.getInstance(context);
        instance.setDeck(deck, "deck");
        instance.setDeck(player1, "player1");
        instance.setDeck(player2, "player2");
        instance.setDeck(trash,"trash");
        //אחרי הפעם הראשונה שני המכשירים מעלים לפיירבייס
    }

    public void joinGame() {
        instance = FbModule.getInstance(context);
    }

    public void fillDeck(){
        while (trash.size() > 0){
            Card c = trash.remove(0);
            c.setIdShown(c.getIdBack());
            deck.add(c);
        }
        shuffle();

    }

    //הפעולה מקבלת קלף ומוסיפה לרשימה של אלו שצריכים להיות חשופים ולכמה זמן
    //אחכ כשבודקים אם הקלפים ברשימה הם מקבלים את הID של הקדימה
    //לבסוף ההנדלר נקרא שוב ומוציא אותם מהרשימה
    public void revealCardTemporarily(final Card card, int seconds, BoardGame boardGame) {
        // הוספת הקלף ללרשימה
        revealedCards.add(card);

        // קורא שוב לONDRAW כאשר הוא כרגע מוגדר להיות הפוך דרך SETCHANGES
        if (boardGame != null) {
            boardGame.setChanges();
        }
        // ההנדלר מוציא את הקלף מהרשימה לאחר X מילי שניות
        revealHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                revealedCards.remove(card);
                boardGame.setChanges(); //ההוצאה
            }
        }, seconds * 1000); // הפיכה למילי שניות
    }

    public boolean isCardRevealed(Card card) {
        return revealedCards.contains(card);
    }

    // חושף את כל 8 הקלפים של שני השחקנים ללא טיימר
    public void revealAllCards(BoardGame boardGame) {
        revealedCards.clear();
        //מוסיפים את כל הקלפים של שניהם לרשימת חשיפה כך שבON DRAW זה יצייר אותם הפוך
        for (int i = 0; i < 4; i++) {
            revealedCards.add(player1.get(i));
            revealedCards.add(player2.get(i));
        }
        if (boardGame != null) {
            boardGame.setChanges();
        }
    }

    // הפעולה מחזירה את הקלף שנלחץ מתוך כל 8 הקלפים על השולחן או null אם לא נלחץ קלף
    public Card findTappedCard(float x, float y, int cardWidth, int cardHeight, int canvasWidth, int canvasHeight) {
        int opponentRowY = 200; //שורת קלפים למעלה
        int myRowY = canvasHeight - 450; //שורת קלפים למטה

        for (int i = 0; i < 4; i++) {
            int playerCardX = (canvasWidth / 4) * i + 35;

            //  קלפי היריב
            if (x >= playerCardX && x <= playerCardX + cardWidth
                    && y >= opponentRowY && y <= opponentRowY + cardHeight) {
                if (GameActivity.player == 0){ // HOST = 0
                    //כלומר קלפי שחקן 2 עבורו למעלה
                    return player2.get(i);
                }
                else {
                    return player1.get(i);
                }
            }

            // הקלפים שלי
            if (x >= playerCardX && x <= playerCardX + cardWidth
                    && y >= myRowY && y <= myRowY + cardHeight) {
                if (GameActivity.player == 0){ // HOST = 0
                    //כלומר קלפי שחקן 1 עבורו למטה
                    return player1.get(i);
                }
                else {
                    return player2.get(i);
                }
            }
        }
        return null;
    }

   /* public int playerSum (int playerNum){
        int player1Sum=0, player2Sum =0;
        for (int i = 0; i < 4; i++) {
            player1Sum += player1.get(i).getValue();
            player2Sum += player2.get(i).getValue();
        }
        if(playerNum == 0){
            return player1Sum;
        }
        else {
            return player2Sum;
        }
    }*/

}
