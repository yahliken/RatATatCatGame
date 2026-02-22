package com.example.ratatatcat;

import android.content.Context;
import android.view.View;

public class BoardGame extends View {

    private final int HOST=1;
    private Context context;
    private GameModule gameModule;

    public BoardGame(Context context) {
        super(context);
        this.context = context;
        gameModule = new GameModule(context);
    }
}
