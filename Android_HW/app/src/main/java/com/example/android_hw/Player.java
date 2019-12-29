package com.example.android_hw;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

public class Player extends AppCompatImageView {

    private static final String TAG = Player.class.getSimpleName();
    private final long INIT_TIME = System.currentTimeMillis();
    private final int MAX_NUM_OF_LIVES = 3;
    private int num_lives = MAX_NUM_OF_LIVES;
    private GameActivity gameActivity;
    private ObjectAnimator scaleX;
    private ObjectAnimator scaleY;
    private AnimatorSet scale;
    private OrientationData orientationData;

    public Player(GameActivity context) {
        super(context);
        setPlayer();
        initOrientationData(context);
        this.gameActivity = context;
    }

    private void setPlayer() {
        this.setImageResource(R.drawable.player1);
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        this.setX(Resources.getSystem().getDisplayMetrics().widthPixels / 2 - (getResources().getDrawable(R.drawable.player1).getMinimumWidth() / 2));
        this.setY(Resources.getSystem().getDisplayMetrics().heightPixels - getResources().getDrawable(R.drawable.blue_lego).getMinimumHeight());
    }

    public OrientationData getOrientationData() {
        return orientationData;
    }

    public void initOrientationData(GameActivity context) {
        orientationData = new OrientationData(context);
    }

    public int getNum_lives() {
        return num_lives;
    }

    public void animatePlayer(Drawable lego) {
        final Player player = this;
        scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1.3f);
        scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1.3f);
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
                scaleX = ObjectAnimator.ofFloat(player, "scaleX", 1f);
                scaleY = ObjectAnimator.ofFloat(player, "scaleY", 1f);
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

    public float updatePlayerPositionUsingMotionSensor(long lastTime) {
        float newX = 0;
        if (gameActivity.getLocalUser().getControls().equals(gameActivity.getString(R.string.motion))) {
            float endX = Resources.getSystem().getDisplayMetrics().widthPixels - this.getWidth();
            if (lastTime < INIT_TIME) {
                lastTime = INIT_TIME;
            }
            int elapsedTime = (int) (System.currentTimeMillis() - lastTime);
            if (orientationData.getOrientation() != null && orientationData.getStartOrientation() != null) {
                //float pitch = orientationData.getOrientation()[1] - orientationData.getStartOrientation()[1];
                float roll = orientationData.getOrientation()[2] - orientationData.getStartOrientation()[2];

                float xSpeed = 2 * roll * Resources.getSystem().getDisplayMetrics().widthPixels / 50f;
                //Log.d(TAG, "updatePositionUsingMotionSensor: currentXSpeed = "+xSpeed);
                //float ySpeed = pitch * Resources.getSystem().getDisplayMetrics().heightPixels / 50f;
                //int changeInXPosition = (int)(this.getX() + Math.abs(xSpeed * elapsedTime));
                float newPosition = this.getX() + xSpeed * elapsedTime;
                if (newPosition < 0) {
                    newX = 0;
                } else if (newPosition > endX) {
                    newX = endX;
                } else {
                    newX = newPosition;
                }
            }
        }
        return newX;
    }
}
