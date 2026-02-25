package com.example.ratatatcat;

import android.content.Context;

import com.example.ratatatcat.helpers.FbModule;
import com.example.ratatatcat.model.Card;

import java.util.ArrayList;
import java.util.Random;

public class GameModule {
    private final int DRAW2 = -1, PEEK = -2, SWAP = -3;
    public static ArrayList<Card> deck = new ArrayList<Card>(), trash = new ArrayList<Card>(), player1 = new ArrayList<Card>(), player2 = new ArrayList<Card>();
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
        if(GameActivity.player==BoardGame.HOST){
            instance = FbModule.getInstance(context);
            instance.setDeck(deck, "deck");
            instance.setDeck(player1, "player1");
            instance.setDeck(player2, "player2");
            instance.setDeck(trash,"trash");
        }
        //תחילה רק המנהל משחק יעלה את החפיסות
    }

    public void setDecksFromFB(){
        instance = FbModule.getInstance(context);
        instance.setDeck(deck, "deck");
        instance.setDeck(player1, "player1");
        instance.setDeck(player2, "player2");
        instance.setDeck(trash,"trash");
        //אחרי הפעם הראשונה שני המכשירים מעלים לפיירבייס
    }
}
