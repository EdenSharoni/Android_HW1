package com.example.android_hw;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

import java.util.Random;

public class Lego extends AppCompatImageView implements ValueAnimator.AnimatorUpdateListener {

    private static final String TAG = Lego.class.getSimpleName();
    private GameActivity gameActivity;
    private boolean hitPlayer = false;
    private int imageID;
    private boolean superHead = false;

    public Lego(GameActivity context) {
        super(context);
        this.gameActivity = context;
        setLego();
    }

    private void setLego() {
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setColor();
    }

    private void setColor() {
        Random random = new Random();
        int randomNumber = 7;

        if (random.nextInt(10) == 5) //Random superHead
            randomNumber = 16;

        int i = random.nextInt(randomNumber);

        switch (i) {
            case 0:
                imageID = R.drawable.blue_lego;
                break;
            case 1:
                imageID = R.drawable.red_lego;
                break;
            case 2:
                imageID = R.drawable.yellow_lego;
                break;
            case 3:
                imageID = R.drawable.green_lego;
                break;
            case 4:
                imageID = R.drawable.pink_lego;
                break;
            case 5:
                imageID = R.drawable.orange_lego;
                break;
            case 6:
                imageID = R.drawable.purple_lego;
                break;
            case 7:
                imageID = R.drawable.player1;
                break;
            case 8:
                imageID = R.drawable.player2;
                break;
            case 9:
                imageID = R.drawable.player3;
                break;
            case 10:
                imageID = R.drawable.player4;
                break;
            case 11:
                imageID = R.drawable.player5;
                break;
            case 12:
                imageID = R.drawable.player6;
                break;
            case 13:
                imageID = R.drawable.player7;
                break;
            case 14:
                imageID = R.drawable.player8;
                break;
            case 15:
                imageID = R.drawable.player9;
                break;

        }
        if (i > 6) {
            superHead = true;
        } else {
            superHead = false;
        }
        this.setImageResource(imageID);
    }

    public void animateLego() {
        final Lego lego = this;
        int legoFallDelayMillis = 700;
        ObjectAnimator animY = ObjectAnimator.ofFloat(this, "Y", 0f, Resources.getSystem().getDisplayMetrics().heightPixels);
        animY.setDuration(legoFallDelayMillis);
        animY.setInterpolator(new LinearInterpolator());
        animY.addUpdateListener(this);
        animY.start();
        animY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!hitPlayer) {
                    gameActivity.getScore().updateHighestScore();
                }
                lego.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (!hitPlayer && checkHit(gameActivity.getPlayer()) && !gameActivity.gameHasEnded()) {
            gameActivity.getPlayer().hit(superHead, this.getDrawable());
        }
    }

    public boolean checkHit(Player player) {
        if (GameActivity.checkCollision(player, this)) {
            hitPlayer = true;
            return true;
        }
        return false;
    }
}
