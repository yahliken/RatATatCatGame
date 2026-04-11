package com.example.ratatatcat.logic;

import android.app.Dialog;
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

import android.widget.Button;
import android.widget.Toast;
import com.example.ratatatcat.helpers.FbModule;

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
    private boolean isPeekMode = false; // האם יצא קלף PEEK
    private int cardsToDraw = 0; // כמה קלפים נותרו לקחת DRAW2 - (0/1/2)
    private Card draw2Card = null; // קלף ה-DRAW2 עצמו - מוצג בפינה
    private boolean isSwapMode = false; // האם יצא קלף SWAP
    private Card swapDraggedCard = null; // הקלף של השחקן שנגרר להחלפה
    private int swapDraggedIndex = -1; //  האינדקס של הקלף שנגרר (לפניה)

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
                // אם עכשיו גוררים קלף בSWAP אז לא נעדכן לקלף הנגרר מיקום לזה שפה (התחלתי)
                if (!isSwapMode || swapDraggedIndex != i) {
                    gameModule.player1.get(i).setX((canvasWidth / 4) * i + 35);
                    gameModule.player1.get(i).setY(canvasHeight - 450);
                }
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
                // אם עכשיו גוררים קלף כחלק מSWAP אז לא נעדכן לנגרר את המיקום למה שפה (להתחלתי)
                if (!isSwapMode || swapDraggedIndex != i) {
                    gameModule.player2.get(i).setX((canvasWidth / 4) * i + 35);
                    gameModule.player2.get(i).setY(canvasHeight - 450);
                }

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
        if(trashSize!=0){ //מראה רק את הקלף האחרון שנוסף לזבל
            gameModule.trash.get(trashSize-1).setX(50);
            gameModule.trash.get(trashSize-1).setY((canvasHeight/2)-140);
            gameModule.trash.get(trashSize-1).setIdShown(gameModule.trash.get(trashSize-1).getIdFront());
            Bitmap bitmapTrash = BitmapFactory.decodeResource(getResources(), gameModule.trash.get(trashSize-1).getIdShown());
            bitmapTrash = Bitmap.createScaledBitmap(bitmapTrash, canvasWidth / 4 - 70, 350, false);
            gameModule.trash.get(trashSize-1).Draw(canvas, bitmapTrash);
        }

        //ציור כפתור סיום
        Bitmap buttonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.btn_end);
        buttonBitmap = Bitmap.createScaledBitmap(buttonBitmap, 400, 150, false);
        canvas.drawBitmap(buttonBitmap, canvasWidth -450, (canvasHeight/2) +520, null);

        //צחור תור מי
        if (GameModule.currentTurn == GameActivity.player){
            Bitmap yourTurnBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.your_turn);
            yourTurnBitmap = Bitmap.createScaledBitmap(yourTurnBitmap, 440, 120, false);
            canvas.drawBitmap(yourTurnBitmap, 0, (canvasHeight/2) +370, null);
        }
        else {
            Bitmap opponentTurnBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.opponent_turn);
            opponentTurnBitmap = Bitmap.createScaledBitmap(opponentTurnBitmap, 440, 120, false);
            canvas.drawBitmap(opponentTurnBitmap, 0, (canvasHeight/2) +370, null);
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

    // חושף את כל 8 הקלפים של שני השחקנים ללא טיימר
    public void revealAllCards() {
        revealedCards.clear();
        //מוסיפים את כל הקלפים של שניהם לרשימת חשיפה כך שבON DRAW זה יצייר אותם הפוך
        for (int i = 0; i < 4; i++) {
            revealedCards.add(gameModule.player1.get(i));
            revealedCards.add(gameModule.player2.get(i));
        }
        invalidate();
    }

    // חושף את כל הקלפים ומחשב ניקוד וקורא לדיאלוג סיום משחק
    public void triggerGameOver() {
        revealAllCards();
        //סכום הקלפים
        int hostSum = 0, joinSum = 0;
        for (int i = 0; i < 4; i++) {
            hostSum += gameModule.player1.get(i).getValue();
            joinSum += gameModule.player2.get(i).getValue();
        }

        //איזה סכום של מי
        int mySum, opponentSum;
        if (GameActivity.player == HOST) {
            mySum = hostSum;
            opponentSum = joinSum;
        } else {
            mySum = joinSum;
            opponentSum = hostSum;
        }

        if (context instanceof GameActivity) {
            ((GameActivity) context).showGameOverDialog(mySum, opponentSum);
        }
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
                // אם לא התור של השחקן הזה נתעלם
                if (GameModule.currentTurn != GameActivity.player) return true;

                //  לחיצה על הקופה ומשיכת קלף
                int deckX = canvasWidth - 250;
                int deckY = (canvasHeight / 2) - 140;

                if (!isCardDrawn && x >= deckX && x <= deckX + cardWidth && y >= deckY && y <= deckY + cardHeight) {
                    if(GameModule.deck.size() == 1){
                        gameModule.fillDeck();
                    }
                    //משיכת הקלף מהרשימה
                    drawnCard = GameModule.deck.remove(0);

                    //הופכים את הקלף וקובעים מיקום למרכז המסך
                    drawnCard.setIdShown(drawnCard.getIdFront());
                    drawnCard.setX((canvasWidth / 2) - (cardWidth / 2));
                    drawnCard.setY((canvasHeight / 2) - (cardHeight / 2));

                    //עדכון Firebase
                    isCardDrawn = true;
                    // אם הקלף הנמשך הוא PEEK אז שנדע שהוא נשלף
                    if (drawnCard.getValue() == -2) {
                        isPeekMode = true;
                    }
                    // אם הקלף הנמשך הוא SWAP
                    if (drawnCard.getValue() == -3) {
                        isSwapMode = true;
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

                // אם נלחץ קלף של שחקן ונשלף כבר קלף הצץ
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
                                // נגמר תור
                                FbModule.getInstance(context).setNewMove(1 - GameActivity.player);
                            }
                            // אם עוד נשארו קלפים לקחת ב DRAW2 לא מעבירים תור
                        }
                        else {
                            // אם זה היה קלף בודד פשוט נעביר תור בסיום
                            FbModule.getInstance(context).setNewMove(1 - GameActivity.player);
                        }
                        gameModule.setDecksFromFB();
                        invalidate();
                        return true;
                    }
                }
                //  לחיצה על הקלף שנמשך מהקופה והתחלת גרירה
                if (isCardDrawn && drawnCard != null && x >= drawnCard.getX() && x <= drawnCard.getX() + cardWidth
                        && y >= drawnCard.getY() && y <= drawnCard.getY() + cardHeight) {

                    isDragging = true;
                    // לשמור את ההפרש כדי שהקלף לא יקפוץ לאצבע - שהגרירה תהיה לפי מיקום הלחיצה
                    dragOffsetX = x - drawnCard.getX();
                    dragOffsetY = y - drawnCard.getY();
                    return true;
                }

                // אם לחצנו על קלף כאשר נשלף SWAP נתחיל גרירה
                if (isSwapMode && !isDragging && swapDraggedCard == null) {
                    ArrayList<Card> myCards;
                    //בדיקה איזה רשימה גוררים ממנה
                    if(GameActivity.player == HOST){
                        myCards = GameModule.player1;
                    }
                    else{
                        myCards = GameModule.player2;
                    }
                    int playerCardY = canvasHeight - 450;
                    //בדיקה האם לחצתי על קלף משלי
                    for (int i = 0; i < 4; i++) {
                        int playerCardX = (canvasWidth / 4) * i + 35;
                        if (x >= playerCardX && x <= playerCardX + cardWidth && y >= playerCardY && y <= playerCardY + cardHeight) {
                            swapDraggedCard = myCards.get(i);
                            swapDraggedIndex = i;
                            isDragging = true;
                            //כדי שהגרירה תהיה בהתאם לנקודה עליה לחצנו והקלף לא יקפוץ
                            dragOffsetX = x - swapDraggedCard.getX();
                            dragOffsetY = y - swapDraggedCard.getY();
                            return true;
                        }
                    }
                }

                //שחקן יכול ללחוץ חתחתול רק בתורו
                int btnEndX = canvasWidth-450;
                int btnEndY = (canvasHeight/2) +520;

                if(x >= btnEndX && x <= btnEndX + 400 && y >= btnEndY && y <= btnEndY + 150){
                    FbModule.getInstance(context).setGameOver();
                    triggerGameOver();
                }

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                //  גרירת קלף
                if (isDragging && drawnCard != null && !isSwapMode) {
                    drawnCard.setX(x - dragOffsetX);
                    drawnCard.setY(y - dragOffsetY);
                    invalidate();
                    return true;
                }
                // גרירת קלף ה-SWAP עצמו לזבל
                if (isDragging && isSwapMode && swapDraggedCard == null && drawnCard != null) {
                    drawnCard.setX(x - dragOffsetX);
                    drawnCard.setY(y - dragOffsetY);
                    invalidate();
                    return true;
                }
                // גרירת קלף מחפיסת השחקן ב SWAP
                if (isDragging && isSwapMode && swapDraggedCard != null) {
                    swapDraggedCard.setX(x - dragOffsetX);
                    swapDraggedCard.setY(y - dragOffsetY);
                    invalidate();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                //  סיום גרירה ב SWAP
                if (isDragging && isSwapMode && swapDraggedCard != null) {
                    isDragging = false;

                    float cardCenterX = swapDraggedCard.getX() + cardWidth / 2;
                    float cardCenterY = swapDraggedCard.getY() + cardHeight / 2;

                    //  האם הקלף הונח על אחד מקלפי היריב
                    ArrayList<Card> myCards, opponentCards;
                    if(GameActivity.player == HOST){
                        myCards = GameModule.player1;
                        opponentCards = GameModule.player2;
                    }
                    else {
                        myCards = GameModule.player2;
                        opponentCards = GameModule.player1;
                    }

                    int opponentCardY = 200;
                    int opponentIndex = -1; // המיקום עליו הונח הקלף - ברירת מחדל אף אחד

                    //מציאת אינדקס הקלף שאליו גררו
                    for (int i = 0; i < 4; i++) {
                        int opponentCardX = (canvasWidth / 4) * i + 35;
                        if (cardCenterX >= opponentCardX && cardCenterX <= opponentCardX + cardWidth
                                && cardCenterY >= opponentCardY && cardCenterY <= opponentCardY + cardHeight) {
                            opponentIndex = i;
                            break;
                        }
                    }

                    //אם הקלף הנגרר הונח על קלף יריב
                    if (opponentIndex != -1) {
                        // שמירה במיקום קלף היריב שהנחנו עליו את הקלף הנגרר
                        Card opponentCard = opponentCards.get(opponentIndex);
                        opponentCards.set(opponentIndex, swapDraggedCard);
                        myCards.set(swapDraggedIndex, opponentCard);

                        // קלף SWAP הולך לזבל
                        GameModule.trash.add(drawnCard);
                        drawnCard = null;
                        isCardDrawn = false;
                        isSwapMode = false;
                        swapDraggedCard = null;

                        // TOAST למכשיר הזה
                        String mySwapInfo = "Swap done — your " + (swapDraggedIndex + 1) + " card swapped with opponent's " + (opponentIndex + 1) + " card";
                        Toast.makeText(context, mySwapInfo, Toast.LENGTH_LONG).show();

                        // עדכון פיירבייס דרך פעולה שתוביל לONDATA שם היריב יקבל גם TOAST
                        FbModule fbModule = FbModule.getInstance(context);
                        fbModule.updateSwap(swapDraggedIndex, opponentIndex, GameActivity.player);

                        swapDraggedIndex = -1; // איפוס בסוף כי השתמשתי בו בTOAST

                        // אם זה חלק מ DRAW2 - מורידים מכמות הקלפים שיש לקחת
                        if (cardsToDraw > 0) {
                            cardsToDraw--;
                            if (cardsToDraw == 0) {
                                // אם זה היה הקלף האחרון זורקים לזבל את DRAW2
                                GameModule.trash.add(draw2Card);
                                draw2Card = null;
                                // נגמר תור
                                fbModule.setNewMove(1 - GameActivity.player);
                            }
                            // אם עוד נשארו קלפים לקחת ב DRAW2 לא מעבירים תור
                        }
                        else {
                            // סיום תור רגיל
                            fbModule.setNewMove(1 - GameActivity.player);//לא צריך GET INSTANCE כי הגדרנו פה את הפיירבייס
                        }

                        //קריאה לONDRAW
                        gameModule.setDecksFromFB();
                        invalidate();
                        return true;
                    }

                    // מחזירים את הקלף למקומו הרגיל אם לא נגרר על קלף יריב
                    swapDraggedCard.setX((canvasWidth / 4f) * swapDraggedIndex + 35);
                    swapDraggedCard.setY(canvasHeight - 450);
                    swapDraggedCard = null;
                    swapDraggedIndex = -1;
                    invalidate();
                    return true;
                }

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
                        isPeekMode = false; // אם גררו את PEEK לזבל ללא שימוש בו
                        isSwapMode = false; // אם גררו את SWAP לזבל ללא שימוש בו
                        // אם זה חלק מ DRAW2 - מורידים מכמות הקלפים שיש לקחת
                        if (cardsToDraw > 0) {
                            cardsToDraw--;
                            if (cardsToDraw == 0) {
                                // אם זה  היה הקלף האחרון זורקים את DRAW2 לזבל
                                GameModule.trash.add(draw2Card);
                                draw2Card = null;
                                // הקלף נזרק והתור נגמר
                                FbModule.getInstance(context).setNewMove(1 - GameActivity.player);
                            }
                            // אם נשארו קלפים לקחת ב DRAW2 לא מעבירים תור
                        }
                        else {
                            // סיום תור רגיל
                            FbModule.getInstance(context).setNewMove(1 - GameActivity.player);
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
                                // נגמר המהלכים והתור
                                FbModule.getInstance(context).setNewMove(1 - GameActivity.player);
                            }
                            // אם נשאר קלפים לקחת ב DRAW2 לא נעביר תור
                        }
                        else {
                            // סיום תור רגיל
                            FbModule.getInstance(context).setNewMove(1 - GameActivity.player);
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

