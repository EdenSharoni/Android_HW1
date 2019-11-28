package com.example.android_hw;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements Animator.AnimatorListener {
    private static final String TAG = GameActivity.class.getSimpleName();
    private FrameLayout frameLayoutManager;
    private TextView liveText;
    private TextView scoreText;
    private Drawable drawable;
    private Random random;
    private int playerCurrentPosition;
    private int legoCurrentPosition = 0;
    private int screenHeightDividedByLegoSize;
    private int amountOfLegoColumn = 5;
    private int lives = 3;
    private boolean die = false;
    private Vibrator vibe;
    private int highestScore = 0;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int delayMillis = 1000;
    private ObjectAnimator animY;
    private ImageView lego;
    private ImageView player;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        getScreenHeightDividedByLegoSize();
        initFrameLayoutManager();
        initPlayer();
        initLives();
        //initScore();
        tickEndlessly();
        setContentView(frameLayoutManager);
    }

    private void initScore() {
        scoreText = new TextView(this);
        scoreText.setText("Score: " + highestScore);
        scoreText.setTextColor(Color.BLACK);
        scoreText.setTextSize(30f);
        //TODO layout Gravity
        frameLayoutManager.addView(scoreText);
    }

    private void initLives() {
        liveText = new TextView(this);
        liveText.setText("Lives: " + lives);
        liveText.setTextColor(Color.BLACK);
        liveText.setTextSize(30f);
        frameLayoutManager.addView(liveText);
    }

    private void initFrameLayoutManager() {
        frameLayoutManager = new FrameLayout(this);
        frameLayoutManager.setBackground(getResources().getDrawable(R.drawable.background_lego));
    }

    private void initPlayer() {

        playerCurrentPosition = amountOfLegoColumn / 2;
        player = new ImageView(this);
        player.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        player.setBackground(getResources().getDrawable(R.drawable.player_lego));
        player.setX(playerCurrentPosition * getResources().getDrawable(R.drawable.player_lego).getMinimumHeight());
        player.setY(Resources.getSystem().getDisplayMetrics().heightPixels - getResources().getDrawable(R.drawable.player_lego).getMinimumHeight());
        frameLayoutManager.addView(player);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        player.setX(x - (getResources().getDrawable(R.drawable.player_lego).getMinimumHeight() / 2));
        return super.onTouchEvent(event);
    }

    private void setColor() {
        random = new Random();
        int i = random.nextInt(7 - 0);
        switch (i) {
            case 0:
                drawable = getResources().getDrawable(R.drawable.blue_lego);
                break;
            case 1:
                drawable = getResources().getDrawable(R.drawable.red_lego);
                break;
            case 2:
                drawable = getResources().getDrawable(R.drawable.yellow_lego);
                break;
            case 3:
                drawable = getResources().getDrawable(R.drawable.green_lego);
                break;
            case 4:
                drawable = getResources().getDrawable(R.drawable.pink_lego);
                break;
            case 5:
                drawable = getResources().getDrawable(R.drawable.orange_lego);
                break;
            case 6:
                drawable = getResources().getDrawable(R.drawable.purple_lego);
                break;
        }
    }

    private void getScreenHeightDividedByLegoSize() {
        float height = Resources.getSystem().getDisplayMetrics().heightPixels;
        float legoHeight = getResources().getDrawable(R.drawable.blue_lego).getMinimumHeight();
        screenHeightDividedByLegoSize = Math.round(height / legoHeight);
    }

    private void initLinearLayoutManager(LinearLayout linearLayoutManager) {
        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutManager.setGravity(Gravity.CENTER);
        linearLayoutManager.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private void initLinearLayout(LinearLayout linearLayout, int gravity) {
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(gravity);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
    }

    private void tickEndlessly() {
        Handler mainLayout = new Handler();
        mainLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!die)
                    tickEndlessly();
                legoGame();
            }
        }, delayMillis);
    }

    private void checkHit() {
        if (lego.getX() + getResources().getDrawable(R.drawable.black_lego).getMinimumWidth() > player.getX()) {
            vibe.vibrate(1000);
            if (lives > 0) {
                lives--;
                liveText.setText("Lives: " + lives);
                lego.setVisibility(View.INVISIBLE);
            } else {
                die = true;
                pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                editor = pref.edit();
                if (pref.getInt("highestScore", -1) < highestScore) {
                    editor.putInt("highestScore", highestScore);
                    editor.commit();
                }
                Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void legoGame() {

        random = new Random();
        if (die)
            return;

        legoCurrentPosition = random.nextInt(amountOfLegoColumn - 0);

        lego = new ImageView(this);
        lego.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setColor();
        lego.setX(legoCurrentPosition * getResources().getDrawable(R.drawable.blue_lego).getMinimumHeight());
        lego.setBackground(drawable);

        frameLayoutManager.addView(lego);

        animY = ObjectAnimator.ofFloat(lego, "Y", 0f, Resources.getSystem().getDisplayMetrics().heightPixels);
        animY.setInterpolator(new AccelerateInterpolator());
        animY.setDuration(800);
        animY.start();
        animY.addListener(this);
    }

    @Override
    public void onAnimationStart(Animator animation) {
        lego.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        lego.setVisibility(View.GONE);
        checkHit();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
