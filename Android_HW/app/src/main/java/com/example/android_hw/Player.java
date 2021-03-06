package com.example.android_hw;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

public class Player extends AppCompatImageView {

    private final int MAX_NUM_OF_LIVES = 3;
    private int num_lives = MAX_NUM_OF_LIVES;
    private Context gameActivity;
    private ObjectAnimator scaleX;
    private ObjectAnimator scaleY;
    private AnimatorSet scale;
    private Gyroscope gyroscope;
    private boolean left = false;
    private boolean right = false;

    public Player(GameActivity context) {
        super(context);
        gyroscope = new Gyroscope(context);
        setPlayer();
        this.gameActivity = context;
    }

    private void setPlayer() {
        this.setImageResource(R.drawable.player1);
    }

    public int getNum_lives() {
        return num_lives;
    }

    public void animatePlayer(Drawable lego) {
        final Player player = this;
        scaleX = ObjectAnimator.ofFloat(this, getContext().getString(R.string.scalex), 1.3f);
        scaleY = ObjectAnimator.ofFloat(this, getContext().getString(R.string.scaley), 1.3f);
        scaleX.setDuration(100);
        scaleY.setDuration(100);
        scale = new AnimatorSet();
        scale.play(scaleX).with(scaleY);
        scale.start();

        this.setImageDrawable(lego);
        ++num_lives;
        ((GameActivity) gameActivity).addLife();
        scale.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                scaleX = ObjectAnimator.ofFloat(player, getContext().getString(R.string.scalex), 1f);
                scaleY = ObjectAnimator.ofFloat(player, getContext().getString(R.string.scaley), 1f);
                scaleX.setDuration(100);
                scaleY.setDuration(100);
                scale = new AnimatorSet();
                scale.play(scaleX).with(scaleY);
                scale.start();
            }
        });
    }

    public void hit() {
        ((GameActivity) gameActivity).Vibrate();
        ((GameActivity) gameActivity).removeLife();
        --num_lives;
        if (num_lives == 0)
            ((GameActivity) gameActivity).EndGame();
    }

    public Gyroscope getGyroscope() {
        return gyroscope;
    }

    public void updatePlayerPositionUsingMotionSensor() {
        gyroscope.setListener((rx, ry, rz) -> {
            if (rz > 1f) {
                left = true;
                right = false;
            } else if (rz < -1f) {
                left = false;
                right = true;
            }
            if (left) {
                if (getX() < 0)
                    return;
                setX(getX() - 200);
            } else if (right) {
                if (getX() > getResources().getDisplayMetrics().widthPixels - getResources().getDrawable(R.drawable.player1).getMinimumWidth())
                    return;
                setX(getX() + 200);
            }
        });
    }
}
