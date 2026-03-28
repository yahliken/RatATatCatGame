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
    private boolean isPeekMode = false; // האם השחקן במצב הצצה (קלף PEEK)
    private int cardsToDraw = 0; // כמה קלפים נותרו לקחת DRAW2 - (0/1/2)
    private Card draw2Card = null; // קלף ה-DRAW2 עצמו - מוצג בפינה כתזכורת

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

                //רשום למצב ששחקן הציץ ליריב ואז הקלף יכנס יחשף
                int imageToShow2;
                if (isCardRevealed(gameModule.player2.get(i))) {
                    imageToShow2 = gameModule.player2.get(i).getIdFront();
                } else {
                    imageToShow2 = gameModule.player2.get(i).getIdShown();
                }

                Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), imageToShow2);
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

                //למצב ששחקן מציץ
                int imageToShow2;
                if (isCardRevealed(gameModule.player1.get(i))) {
                    imageToShow2 = gameModule.player1.get(i).getIdFront();
                } else {
                    imageToShow2 = gameModule.player1.get(i).getIdShown();
                }

                Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), imageToShow2);
                bitmap2 = Bitmap.createScaledBitmap(bitmap2, canvasWidth / 4 - 70, 350, false);
                gameModule.player1.get(i).Draw(canvas, bitmap2);
            }
        }



        gameModule.deck.get(0).setX(canvasWidth-250);
        gameModule.deck.get(0).setY((canvasHeight/2)-140);
        Bitmap bitmapDeck = BitmapFactory.decodeResource(getResources(), gameModule.deck.get(0).getIdShown());
        bitmapDeck = Bitmap.createScaledBitmap(bitmapDeck, canvasWidth / 4 - 70, 350, false);
        gameModule.deck.get(0).Draw(canvas, bitmapDeck);

        // ציור קלף DRAW2 מעל הזבל
        if (cardsToDraw > 0 && draw2Card != null) {
            Bitmap bitmapDraw2 = BitmapFactory.decodeResource(getResources(), draw2Card.getIdFront());
            bitmapDraw2 = Bitmap.createScaledBitmap(bitmapDraw2, canvasWidth / 4 - 70, 350, false);
            canvas.drawBitmap(bitmapDraw2, 50, (canvasHeight / 2) - 570, null);
        }

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

        //gameModule.setDecksFromFB();
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

        // קורא שוב לONDRAW כאשר הוא כרגע מוגדר להיות הפוך
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

    // הפעולה מחזירה את הקלף שנלחץ מתוך כל 8 הקלפים על השולחן, או null אם לא נלחץ קלף
    private Card findTappedCard(float x, float y, int cardWidth, int cardHeight) {
        int opponentRowY = 200; //שורת קלפים למעלה
        int myRowY = canvasHeight - 450; //שורת קלפים למטה

        for (int i = 0; i < 4; i++) {
            int playerCardX = (canvasWidth / 4) * i + 35;

            //  קלפי היריב
            if (x >= playerCardX && x <= playerCardX + cardWidth
                    && y >= opponentRowY && y <= opponentRowY + cardHeight) {
                if (GameActivity.player == HOST){
                    //כלומר קלפי שחקן 2 עבורו למעלה
                    return GameModule.player2.get(i);
                }
                else {
                    return GameModule.player1.get(i);
                }
            }

            // הקלפים שלי
            if (x >= playerCardX && x <= playerCardX + cardWidth
                    && y >= myRowY && y <= myRowY + cardHeight) {
                if (GameActivity.player == HOST){
                    //כלומר קלפי שחקן 1 עבורו למטה
                    return GameModule.player1.get(i);
                }
                else {
                    return GameModule.player2.get(i);
                }
            }
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int cardWidth = canvasWidth / 4 - 70;
        int cardHeight = 350;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN: {
                //  לחיצה על הקופה ומשיכת קלף
                int deckX = canvasWidth - 250;
                int deckY = (canvasHeight / 2) - 140;

                if (!isCardDrawn && !GameModule.deck.isEmpty()
                        && x >= deckX && x <= deckX + cardWidth
                        && y >= deckY && y <= deckY + cardHeight) {

                    //משיכת הקלף מהרשימה
                    drawnCard = GameModule.deck.remove(0);

                    //הופכים את הקלף וקובעים מיקום למרכז המסך
                    drawnCard.setIdShown(drawnCard.getIdFront());
                    drawnCard.setX((canvasWidth / 2) - (cardWidth / 2));
                    drawnCard.setY((canvasHeight / 2) - (cardHeight / 2));

                    //עדכון Firebase
                    isCardDrawn = true;
                    // אם הקלף הנמשך הוא PEEK אז מצב הצצה
                    if (drawnCard.getValue() == -2) {
                        isPeekMode = true;
                    }
                    // אם הקלף הנמשך הוא DRAW2
                    if (drawnCard.getValue() == -1) {
                        if (cardsToDraw == 0) {
                            // בודקים אם אין קלף DRAW2 פתוח לפי התנאי
                            draw2Card = drawnCard;
                            cardsToDraw = 2;
                        } else {
                            // אם קיבלנו DRAW2 בתוך DRAW2 הוא הולך לזבל ולא מורידים מהכמות קלפים שיש עוד לקחת
                            GameModule.trash.add(drawnCard);
                        }
                        drawnCard = null;
                        isCardDrawn = false;
                        gameModule.setDecksFromFB();
                        invalidate();
                        return true;
                    }
                    gameModule.setDecksFromFB();
                    invalidate();
                    return true;
                }

                // אם נלחץ קלף של שחקן ואנחנו במצב הצצה
                if (isPeekMode) {
                    // בדיקת הקלף הנלחץ
                    Card tappedCard = findTappedCard(x, y, cardWidth, cardHeight);
                    if (tappedCard != null) {
                        // להפוך את הקלף רק למי שמשך הצץ
                        revealCardTemporarily(tappedCard, 3);
                        // זורקים את קלף ה-PEEK לזבל בסוף
                        GameModule.trash.add(drawnCard);
                        drawnCard = null;
                        isCardDrawn = false;
                        isPeekMode = false;
                        // אם זה חלק מ DRAW2 - מורידים מכמות הקלפים שיש לקחת
                        if (cardsToDraw > 0) {
                            cardsToDraw--;
                            if (cardsToDraw == 0) {
                                //אם זה היה הקלף האחרון זורקים לזבל את DRAW2
                                GameModule.trash.add(draw2Card);
                                draw2Card = null;
                            }
                        }
                        gameModule.setDecksFromFB();
                        invalidate();
                        return true;
                    }
                }
                //  לחיצה על הקלף שנמשך מהקופה והתחלת גרירה
                if (isCardDrawn && drawnCard != null
                        && x >= drawnCard.getX() && x <= drawnCard.getX() + cardWidth
                        && y >= drawnCard.getY() && y <= drawnCard.getY() + cardHeight) {

                    isDragging = true;
                    // לשמור את ההפרש כדי שהקלף לא יקפוץ לאצבע - שהגרירה תהיה לפי מיקום הלחיצה
                    dragOffsetX = x - drawnCard.getX();
                    dragOffsetY = y - drawnCard.getY();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                //  גרירת קלף
                if (isDragging && drawnCard != null) {
                    drawnCard.setX(x - dragOffsetX);
                    drawnCard.setY(y - dragOffsetY);
                    invalidate();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                //אם היינו במצב גרירה של קלף קיים
                if (isDragging && drawnCard != null) {
                    isDragging = false;

                    float cardCenterX = drawnCard.getX() + cardWidth / 2;
                    float cardCenterY = drawnCard.getY() + cardHeight / 2;

                    int trashX = 50;
                    int trashY = (canvasHeight / 2) - 140;

                    boolean droppedOnTrash = cardCenterX >= trashX && cardCenterX <= trashX + cardWidth
                            && cardCenterY >= trashY && cardCenterY <= trashY + cardHeight;

                    //  האם הקלף נגרר לזבל
                    if (droppedOnTrash) {
                        GameModule.trash.add(drawnCard);
                        drawnCard = null;
                        isCardDrawn = false;
                        isPeekMode = false; // אם גררו את ה-PEEK לזבל ללא שימוש בו
                        // אם זה חלק מ DRAW2 - מורידים מכמות הקלפים שיש לקחת
                        if (cardsToDraw > 0) {
                            cardsToDraw--;
                            if (cardsToDraw == 0) {
                                // אם זה  היה הקלף האחרון זורקים את DRAW2 לזבל
                                GameModule.trash.add(draw2Card);
                                draw2Card = null;
                            }
                        }
                        gameModule.setDecksFromFB();
                        invalidate();
                        return true;
                    }

                    //  האם הקלף נגרר לאחד מקלפי השחקן
                    // קלפים מיוחדים (ערך שלילי) לא יכולים - חוזרים למרכז
                    if (drawnCard.getValue() < 0) {
                        drawnCard.setX((canvasWidth / 2) - (cardWidth / 2));
                        drawnCard.setY((canvasHeight / 2) - (cardHeight / 2));
                        invalidate();
                        return true;
                    }
                    //בדיקה באיזה רשימת קלפים מדובר לפי השחקן
                    //לפי הרשימה שהעתקנו נשנה את מיקום הקלף החדש במידה ונגרר לאחד מהם
                    ArrayList<Card> myCards;
                    if(GameActivity.player == HOST){
                        myCards = GameModule.player1;
                    }
                    else{
                        myCards = GameModule.player2;
                    }
                    int playerCardY = canvasHeight - 450;

                    int droppedIndex = -1; //  האינדקס של הקלף שעליו הונח הקלף הנמשך - ברירת מחדל אף קלף
                    //מציאת האינדקס של הקלף אליו גררנו
                    for (int i = 0; i < 4; i++) {
                        int playerCardX = (canvasWidth / 4) * i + 35;
                        if (cardCenterX >= playerCardX && cardCenterX <= playerCardX + cardWidth
                                && cardCenterY >= playerCardY && cardCenterY <= playerCardY + cardHeight) {
                            droppedIndex = i;
                            break;
                        }
                    }

                    //אם נגרר לאחד הקלפים
                    if (droppedIndex != -1) {
                        // שולחים את הקלף הישן לזבל
                        Card replacedCard = myCards.get(droppedIndex);
                        GameModule.trash.add(replacedCard);

                        // שמים את הקלף הנמשך במקומו והפוך
                        drawnCard.setIdShown(drawnCard.getIdBack());
                        myCards.set(droppedIndex, drawnCard);

                        drawnCard = null;
                        isCardDrawn = false;
                        // אם זה כחלק מ DRAW2 - מורידים מהקלפים שיש לקחת
                        if (cardsToDraw > 0) {
                            cardsToDraw--;
                            if (cardsToDraw == 0) {
                                // אם זה הקלף האחרון נזרוק לזבל את DRAW2
                                GameModule.trash.add(draw2Card);
                                draw2Card = null;
                            }
                        }
                        gameModule.setDecksFromFB();
                        invalidate();
                        return true;
                    }

                    //  לא נגרר על שום דבר - חזרה למרכז
                    drawnCard.setX((canvasWidth / 2) - (cardWidth / 2));
                    drawnCard.setY((canvasHeight / 2) - (cardHeight / 2));
                    invalidate();
                    return true;
                }
                break;
            }
        }
        return super.onTouchEvent(event);
    }}

