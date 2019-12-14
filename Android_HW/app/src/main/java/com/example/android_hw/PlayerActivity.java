package com.example.android_hw;

import android.content.res.Resources;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

public class PlayerActivity extends AppCompatImageView {

    private static final String TAG = PlayerActivity.class.getSimpleName();
    private ImageView player;

    public PlayerActivity(GameActivity context) {
        super(context);
        setPlayer();
    }

    private void setPlayer() {
        this.setImageResource(R.drawable.player1);
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        this.setX(Resources.getSystem().getDisplayMetrics().widthPixels / 2 - (getResources().getDrawable(R.drawable.player1).getMinimumWidth() / 2));
        this.setY(Resources.getSystem().getDisplayMetrics().heightPixels - getResources().getDrawable(R.drawable.blue_lego).getMinimumHeight());
    }
}
