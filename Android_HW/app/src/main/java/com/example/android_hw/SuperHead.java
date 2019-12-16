package com.example.android_hw;

import android.util.Log;

import java.util.Random;

public class SuperHead extends Lego {
    private static final String TAG = SuperHead.class.getSimpleName();

    public SuperHead(GameActivity context) {
        super(context);
    }

    @Override
    protected void setColor() {
        Random random = new Random();
        int i = random.nextInt(9);
        switch (i) {
            case 0:
                imageID = R.drawable.player1;
                break;
            case 1:
                imageID = R.drawable.player2;
                break;
            case 2:
                imageID = R.drawable.player3;
                break;
            case 3:
                imageID = R.drawable.player4;
                break;
            case 4:
                imageID = R.drawable.player5;
                break;
            case 5:
                imageID = R.drawable.player6;
                break;
            case 6:
                imageID = R.drawable.player7;
                break;
            case 7:
                imageID = R.drawable.player8;
                break;
            case 8:
                imageID = R.drawable.player9;
                break;
        }
        this.setImageResource(imageID);
    }
}
