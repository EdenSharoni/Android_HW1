package com.example.android_hw;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

public class Player extends AppCompatImageView {

    private static final String TAG = Player.class.getSimpleName();
    private final int MAX_NUM_OF_LIVES = 3;
    private int num_lives = MAX_NUM_OF_LIVES;
    private GameActivity gameActivity;
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
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        this.setX(Resources.getSystem().getDisplayMetrics().widthPixels / 2 - (getResources().getDrawable(R.drawable.player1).getMinimumWidth() / 2));
        this.setY(Resources.getSystem().getDisplayMetrics().heightPixels - getResources().getDrawable(R.drawable.blue_lego).getMinimumHeight());
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
        gameActivity.addLife();
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
        gameActivity.Vibrate();
        gameActivity.removeLife();
        --num_lives;
        if (num_lives == 0)
            gameActivity.EndGame();
    }

    public Gyroscope getGyroscope() {
        return gyroscope;
    }

    public void updatePlayerPositionUsingMotionSensor() {
        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rx, float ry, float rz) {
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
            }
        });
    }
}
