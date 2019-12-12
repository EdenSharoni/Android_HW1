package com.example.android_hw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PlayerActivity extends AppCompatImageView {

    private int image_id;
    private ImageView player;
    private Functions functions;

    public PlayerActivity(GameActivity context) {
        super(context);
        setPlayer(context);
    }

    private void setPlayer(GameActivity context) {
        player = new ImageView(context);
        player.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        player.setBackground(getResources().getDrawable(R.drawable.player1));
        player.setX(functions.getLegoWidth(context) / 2 - (functions.getLegoWidth(context) / 2));
        player.setY(functions.getScreenHeight() - functions.getLegoHeight(context));
    }
}
