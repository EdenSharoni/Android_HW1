package com.example.android_hw;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

import java.util.Random;

public class Lego extends AppCompatImageView implements ValueAnimator.AnimatorUpdateListener {

    private Context gameActivity;
    private boolean hitPlayer = false;
    public int imageID;

    public Lego(Context context) {
        super(context);
        this.gameActivity = context;
        setLego();
    }

    public void setLego() {
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setColor();
    }

    protected void setColor() {
        Random random = new Random();
        int i = random.nextInt(7);
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
        animY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!hitPlayer) {
                    ((GameActivity)gameActivity).getScore().updateHighestScore();
                }
                lego.setVisibility(GONE);
            }
        });
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (!hitPlayer && checkHit(((GameActivity)gameActivity).getPlayer()) && !((GameActivity)gameActivity).gameHasEnded()) {
            MediaPlayer mediaPlayer;
            if (this instanceof SuperHead) {
                if (((GameActivity)gameActivity).getLocalUser().isMusicSettings()) {
                    mediaPlayer = MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.super_head);
                    mediaPlayer.setLooping(false);
                    mediaPlayer.start();
                }
                ((GameActivity)gameActivity).getPlayer().animatePlayer(this.getDrawable());
                this.setVisibility(GONE);
            } else if (this instanceof Coin) {
                if (((GameActivity)gameActivity).getLocalUser().isMusicSettings()) {
                    mediaPlayer = MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.collect_coin);
                    mediaPlayer.setLooping(false);
                    mediaPlayer.start();
                }
                ((GameActivity)gameActivity).getScore().superCoin();
                disappearAnimation();
            } else {
                ((GameActivity)gameActivity).getPlayer().hit();
                this.setVisibility(GONE);
            }
        }
    }

    private void disappearAnimation() {
        final Lego lego = this;
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, getContext().getString(R.string.scalex), 2.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, getContext().getString(R.string.scaley), 2.5f);
        scaleX.setDuration(50);
        scaleY.setDuration(50);
        AnimatorSet scale = new AnimatorSet();
        scale.play(scaleX).with(scaleY);
        scale.start();
        scale.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                lego.setVisibility(GONE);
            }
        });
    }

    public boolean checkHit(Player player) {
        if (GameActivity.checkCollision(player, this)) {
            hitPlayer = true;
            return true;
        }
        return false;
    }
}