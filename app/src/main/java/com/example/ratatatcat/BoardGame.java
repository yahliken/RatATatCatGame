package com.example.ratatatcat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;

public class BoardGame extends View {

    public static final int HOST = 0;
    private Context context;
    private GameModule gameModule;
    private int canvasWidth, canvasHeight;
    private boolean isFirstTime = true;

    public BoardGame(Context context) {
        super(context);
        this.context = context;
        gameModule = new GameModule(context);
        this.setBackgroundResource(R.drawable.gamebackground);
        if (GameActivity.player == HOST) {
            gameModule.startGame();
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

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
            if (isFirstTime) {
                isFirstTime = false;
            }
            gameModule.setDecksFromFB();
        }
    }
}
