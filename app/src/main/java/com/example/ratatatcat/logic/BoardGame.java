package com.example.ratatatcat.logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.ratatatcat.R;
import com.example.ratatatcat.activities.GameActivity;

public class BoardGame extends View {

    public static final int HOST = 0;
    private Context context;
    private GameModule gameModule;
    private int canvasWidth, canvasHeight;
    private boolean isFirstTime = true;
    public static boolean FbExist = false;

    public BoardGame(Context context) {
        super(context);
        this.context = context;
        gameModule = new GameModule(context);
        this.setBackgroundResource(R.drawable.gamebackground);
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
        }

        for (int i = 0; i < 4; i++) {
            if (GameActivity.player == HOST) {
                if (isFirstTime) {
                    if (i == 0 || i == 3) {
                        gameModule.player1.get(i).setIdShown(gameModule.player1.get(i).getIdFront());
                    }
                }
                //לבדוק למה זה לא עובד עבור הקלף הרביעי

                gameModule.player1.get(i).setX((canvasWidth / 4) * i + 35);
                gameModule.player1.get(i).setY(canvasHeight - 450);
                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), gameModule.player1.get(i).getIdShown());
                bitmap1 = Bitmap.createScaledBitmap(bitmap1, canvasWidth / 4 - 70, 350, false);
                gameModule.player1.get(i).Draw(canvas, bitmap1);

                gameModule.player2.get(i).setX((canvasWidth / 4) * i + 35);
                gameModule.player2.get(i).setY(200);
                Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), gameModule.player2.get(i).getIdShown());
                bitmap2 = Bitmap.createScaledBitmap(bitmap2, canvasWidth / 4 - 70, 350, false);
                gameModule.player2.get(i).Draw(canvas, bitmap2);

            }
            else{
                if (isFirstTime) {
                    if (i == 0 || i == 3) {
                        gameModule.player2.get(i).setIdShown(gameModule.player2.get(i).getIdFront());
                    }
                }
                gameModule.player2.get(i).setX((canvasWidth / 4) * i + 35);
                gameModule.player2.get(i).setY(canvasHeight - 450);
                Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), gameModule.player2.get(i).getIdShown());
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

        gameModule.setDecksFromFB();
        if (isFirstTime) {
            isFirstTime = false;
        }
    }

    public void setChanges() {
        invalidate();
    }
}
