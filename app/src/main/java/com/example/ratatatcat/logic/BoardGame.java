package com.example.ratatatcat.logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.ratatatcat.R;
import com.example.ratatatcat.activities.GameActivity;
import com.example.ratatatcat.model.Card;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BoardGame extends View {

    public static final int HOST = 0;
    private Context context;
    private GameModule gameModule;
    private int canvasWidth, canvasHeight;
    private boolean isFirstTime = true;
    public static boolean FbExist = false;
    private ArrayList<Card> revealedCards;
    private Handler revealHandler;
    private Card drawnCard = null; // הקלף שנמצא כרגע במרכז
    private boolean isCardDrawn = false; // מונע משיכה נוספת עד שהנוכחי יטופל
    private boolean isDragging = false; // האם הקלף נגרר כרגע
    private float dragOffsetX, dragOffsetY; // ההפרש בין נקודת הלחיצה למיקום הקלף

    public BoardGame(Context context) {
        super(context);
        this.context = context;
        gameModule = new GameModule(context);
        this.setBackgroundResource(R.drawable.gamebackground);

        revealedCards = new ArrayList<Card>();
        revealHandler = new Handler(Looper.getMainLooper());

        if (GameActivity.player == HOST) {
            gameModule.startGame();
        }
        else {
            gameModule.joinGame();
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if(FbExist == false && GameActivity.player != HOST){
            invalidate();
            return;
        }
        //אם עדיין לא התמלאו החבילות מFB מצייר מחדש כלומר חוזר לפעולה זו שוב
        if (GameModule.deck == null || GameModule.deck.isEmpty() ||
                GameModule.player1 == null || GameModule.player1.isEmpty() ||
                GameModule.player2 == null || GameModule.player2.isEmpty()) {
            invalidate(); // בקש ציור מחדש בעוד רגע
            return;
        }

        if (isFirstTime) {
            canvasWidth = canvas.getWidth();
            canvasHeight = canvas.getHeight();
            revealedCards.clear();

            if (GameActivity.player == HOST) {
                revealCardTemporarily(gameModule.player1.get(0), 5);
                revealCardTemporarily(gameModule.player1.get(3), 5);
            }
            else {
                revealCardTemporarily(gameModule.player2.get(0), 5);
                revealCardTemporarily(gameModule.player2.get(3), 5);
            }
            isFirstTime = false;
        }
        for (int i = 0; i < 4; i++) {
            if (GameActivity.player == HOST) {
                // HOST רואה את הקלפים שלו למטה
                gameModule.player1.get(i).setX((canvasWidth / 4) * i + 35);
                gameModule.player1.get(i).setY(canvasHeight - 450);

                //בודק איזה קלפים ברשימה של אלו שיתהפכו וקובע את התמונה שנראה ללא לעדכן את הפיירבייס
                int imageToShow;
                if (isCardRevealed(gameModule.player1.get(i))) {
                    imageToShow = gameModule.player1.get(i).getIdFront();
                }
                else {
                    imageToShow = gameModule.player1.get(i).getIdShown();
                }

                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), imageToShow);
                bitmap1 = Bitmap.createScaledBitmap(bitmap1, canvasWidth / 4 - 70, 350, false);
                gameModule.player1.get(i).Draw(canvas, bitmap1);

                // HOST רואה את קלפי היריב למעלה מוסתרים
                gameModule.player2.get(i).setX((canvasWidth / 4) * i + 35);
                gameModule.player2.get(i).setY(200);

                Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), gameModule.player2.get(i).getIdShown());
                bitmap2 = Bitmap.createScaledBitmap(bitmap2, canvasWidth / 4 - 70, 350, false);
                gameModule.player2.get(i).Draw(canvas, bitmap2);

            }
            //אותו הדבר לשחקן השני
            else{

                gameModule.player2.get(i).setX((canvasWidth / 4) * i + 35);
                gameModule.player2.get(i).setY(canvasHeight - 450);

                int imageToShow;
                if (isCardRevealed(gameModule.player2.get(i))){
                    imageToShow = gameModule.player2.get(i).getIdFront();
                } else {
                    imageToShow = gameModule.player2.get(i).getIdShown();
                }

                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), imageToShow);
                bitmap1 = Bitmap.createScaledBitmap(bitmap1, canvasWidth / 4 - 70, 350, false);
                gameModule.player2.get(i).Draw(canvas, bitmap1);

                gameModule.player1.get(i).setX((canvasWidth / 4) * i + 35);
                gameModule.player1.get(i).setY(200);

                Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), gameModule.player1.get(i).getIdShown());
                bitmap2 = Bitmap.createScaledBitmap(bitmap2, canvasWidth / 4 - 70, 350, false);
                gameModule.player1.get(i).Draw(canvas, bitmap2);
            }
        }



        gameModule.deck.get(0).setX(canvasWidth-250);
        gameModule.deck.get(0).setY((canvasHeight/2)-140);
        Bitmap bitmapDeck = BitmapFactory.decodeResource(getResources(), gameModule.deck.get(0).getIdShown());
        bitmapDeck = Bitmap.createScaledBitmap(bitmapDeck, canvasWidth / 4 - 70, 350, false);
        gameModule.deck.get(0).Draw(canvas, bitmapDeck);


        int trashSize = gameModule.trash.size();
        if(trashSize!=0){
            gameModule.trash.get(trashSize-1).setX(50);
            gameModule.trash.get(trashSize-1).setY((canvasHeight/2)-140);
            gameModule.trash.get(trashSize-1).setIdShown(gameModule.trash.get(trashSize-1).getIdFront());
            Bitmap bitmapTrash = BitmapFactory.decodeResource(getResources(), gameModule.trash.get(trashSize-1).getIdShown());
            bitmapTrash = Bitmap.createScaledBitmap(bitmapTrash, canvasWidth / 4 - 70, 350, false);
            gameModule.trash.get(trashSize-1).Draw(canvas, bitmapTrash);
        }

        // ציור הקלף שנמשך במרכז (אם קיים כזה)
        if (isCardDrawn && drawnCard != null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawnCard.getIdShown());
            bitmap = Bitmap.createScaledBitmap(bitmap, canvasWidth / 4 - 70, 350, false);
            drawnCard.Draw(canvas, bitmap);
        }

        gameModule.setDecksFromFB();
    }

    public void setChanges() {
        invalidate();
    }

    //הפעולה שכתבתי מקבלת קלף ומוסיפה לרשימה של אלו שצריכים להיות חשופים ולכמה זמן
    //אחכ כשבודקים אם הקלפים ברשימה הם מקבלים את הID של הקדימה
    //לבסוף ההנדלר נקרא שוב ומוציא אותם מהרשימה
    public void revealCardTemporarily(final Card card, int seconds) {
        // הוספת הקלף ללרשימה
        revealedCards.add(card);

        // קורא שוב לONDRAW כאשר הוא כרגע מיועד להיות הפוך
        invalidate();

        // ההנדלר מוציא את הקלף מהרשימה לאחר X מילי שניות
        revealHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                revealedCards.remove(card);
                invalidate(); //ההוצאה
            }
        }, seconds * 1000); // הפיכה למילי שניות
    }
    private boolean isCardRevealed(Card card) {
        return revealedCards.contains(card);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int cardWidth = canvasWidth / 4 - 70;
        int cardHeight = 350;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN: {
                // --- לחיצה על הקופה: משיכת קלף ---
                int deckX = canvasWidth - 250;
                int deckY = (canvasHeight / 2) - 140;

                if (!isCardDrawn && !GameModule.deck.isEmpty()
                        && x >= deckX && x <= deckX + cardWidth
                        && y >= deckY && y <= deckY + cardHeight) {

                    drawnCard = GameModule.deck.remove(0);
                    drawnCard.setIdShown(drawnCard.getIdFront());
                    drawnCard.setX((canvasWidth / 2f) - (cardWidth / 2f));
                    drawnCard.setY((canvasHeight / 2f) - (cardHeight / 2f));

                    isCardDrawn = true;
                    gameModule.setDecksFromFB();
                    invalidate();
                    return true;
                }

                // --- לחיצה על הקלף שנמשך: התחלת גרירה ---
                if (isCardDrawn && drawnCard != null
                        && x >= drawnCard.getX() && x <= drawnCard.getX() + cardWidth
                        && y >= drawnCard.getY() && y <= drawnCard.getY() + cardHeight) {

                    isDragging = true;
                    // שומרים את ההפרש כדי שהקלף לא "יקפוץ" לאצבע
                    dragOffsetX = x - drawnCard.getX();
                    dragOffsetY = y - drawnCard.getY();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // --- גרירת הקלף ---
                if (isDragging && drawnCard != null) {
                    drawnCard.setX(x - dragOffsetX);
                    drawnCard.setY(y - dragOffsetY);
                    invalidate();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (isDragging && drawnCard != null) {
                    isDragging = false;

                    // --- בדיקה: האם הקלף הונח על הטראש? ---
                    int trashX = 50;
                    int trashY = (canvasHeight / 2) - 140;

                    float cardCenterX = drawnCard.getX() + cardWidth / 2f;
                    float cardCenterY = drawnCard.getY() + cardHeight / 2f;

                    boolean droppedOnTrash = cardCenterX >= trashX && cardCenterX <= trashX + cardWidth
                            && cardCenterY >= trashY && cardCenterY <= trashY + cardHeight;

                    if (droppedOnTrash) {
                        // הנחה על הטראש
                        GameModule.trash.add(drawnCard);
                        drawnCard = null;
                        isCardDrawn = false;
                        gameModule.setDecksFromFB();
                    } else {
                        // חזרה למרכז אם לא הונח על הטראש
                        drawnCard.setX((canvasWidth / 2f) - (cardWidth / 2f));
                        drawnCard.setY((canvasHeight / 2f) - (cardHeight / 2f));
                    }
                    invalidate();
                    return true;
                }
                break;
            }
        }
        return super.onTouchEvent(event);
    }
}

