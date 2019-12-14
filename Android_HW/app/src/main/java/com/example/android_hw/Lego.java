package com.example.android_hw;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

import java.util.Random;

public class Lego extends AppCompatImageView implements ValueAnimator.AnimatorUpdateListener {
    private static final String TAG = Lego.class.getSimpleName();
    private GameActivity gameActivity;
    private Random random;

    public Lego(GameActivity context) {
        super(context);
        setLego();
        this.gameActivity = context;
    }

    private void setLego() {
        this.setImageResource(R.drawable.red_lego);
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setColor();
    }

    private void setColor() {
        random = new Random();
        int i = random.nextInt(7);
        switch (i) {
            case 0:
                this.getResources().getDrawable(R.drawable.blue_lego);
                break;
            case 1:
                this.getResources().getDrawable(R.drawable.red_lego);
                break;
            case 2:
                this.getResources().getDrawable(R.drawable.yellow_lego);
                break;
            case 3:
                this.getResources().getDrawable(R.drawable.green_lego);
                break;
            case 4:
                this.getResources().getDrawable(R.drawable.pink_lego);
                break;
            case 5:
                this.getResources().getDrawable(R.drawable.orange_lego);
                break;
            case 6:
                this.getResources().getDrawable(R.drawable.purple_lego);
                break;
        }
    }

    public void animateLego() {
        final Lego lego = this;
        ObjectAnimator animY = ObjectAnimator.ofFloat(this, "Y", 0f, Resources.getSystem().getDisplayMetrics().heightPixels);
        int legoFallDelayMillis = 700;
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
        Log.e(TAG, "onAnimationUpdate: ");
        //gameActivity.checkHit();
    }
}
