package com.example.android_hw;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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

    public Player(GameActivity context) {
        super(context);
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
        scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1.2f);
        scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1.2f);
        scaleX.setDuration(100);
        scaleY.setDuration(100);
        scale = new AnimatorSet();
        scale.play(scaleX).with(scaleY);
        scale.start();

        this.setImageDrawable(lego);
        ++num_lives;
        gameActivity.addLife();

        scale.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                scaleX = ObjectAnimator.ofFloat(player, "scaleX", 1f);
                scaleY = ObjectAnimator.ofFloat(player, "scaleY", 1f);
                scaleX.setDuration(100);
                scaleY.setDuration(100);
                scale = new AnimatorSet();
                scale.play(scaleX).with(scaleY);
                scale.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

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
}
