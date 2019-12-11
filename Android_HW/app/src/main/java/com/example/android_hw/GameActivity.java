package com.example.android_hw;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements ValueAnimator.AnimatorUpdateListener {

    private static final String TAG = GameActivity.class.getSimpleName();
    private ArrayList<ImageView> legoArrayList = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private FrameLayout frameLayoutManager;
    private ObjectAnimator scoreAnimation;
    private ImageView lego;
    private ImageView player;
    private TextView liveText;
    private TextView scoreText;
    private Drawable drawable;
    private Random random;
    private boolean isDead = false;
    private boolean pauseIsGame = false;
    private int highestScore = 0;
    private int delayMillis = 400;
    private int lives = 2;
    private float legoHeight;
    private float legoWidth;
    private float screenHeight;
    private float screenWidth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        playMusic();
        getScreenHeightAndWidthAndLegoHeightAndWidth();
        initFrameLayoutManager();
        initPlayer();
        initLives();
        initScore();
        setContentView(frameLayoutManager);
    }

    private void playMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void getScreenHeightAndWidthAndLegoHeightAndWidth() {
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        legoHeight = getResources().getDrawable(R.drawable.blue_lego).getMinimumHeight();
        legoWidth = getResources().getDrawable(R.drawable.player1).getMinimumWidth();
    }

    private void initFrameLayoutManager() {
        frameLayoutManager = new FrameLayout(this);
        frameLayoutManager.setBackground(getResources().getDrawable(R.drawable.background_lego));
    }

    private void initPlayer() {

        player = new ImageView(this);
        player.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        player.setBackground(getResources().getDrawable(R.drawable.player1));
        player.setX(screenWidth / 2 - (legoWidth / 2));
        player.setY(screenHeight - legoHeight);
        frameLayoutManager.addView(player);
    }

    private void initLives() {
        liveText = new TextView(this);
        liveText.setText(getString(R.string.lives, (lives + 1)));
        liveText.setTextColor(Color.BLACK);
        liveText.setTextSize(30f);
        frameLayoutManager.addView(liveText);
    }

    private void initScore() {
        scoreText = new TextView(this);
        scoreText.setText(getString(R.string.score, highestScore));
        scoreText.setTextColor(Color.BLACK);
        scoreText.setTextSize(30f);
        scoreText.setGravity(Gravity.CENTER | Gravity.TOP);
        scoreText.setPadding(0, 300, 0, 0);
        scoreText.setVisibility(View.VISIBLE);
        frameLayoutManager.addView(scoreText);

        scoreAnimation = ObjectAnimator.ofFloat(scoreText, "rotation", 0f, 5f, 0f, -5f, 0f); // rotate o degree then 5 degree and so on for one loop of rotation.
        scoreAnimation.setInterpolator(new AccelerateInterpolator());
        scoreAnimation.setRepeatCount(2); // repeat the loop 20 times
        scoreAnimation.setDuration(100); // animation play time 100 ms
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        player.setX(x - (legoHeight / 2));
        return super.onTouchEvent(event);
    }

    private void setColor() {
        random = new Random();
        int i = random.nextInt(16);
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
            case 7:
                drawable = getResources().getDrawable(R.drawable.player1);
                break;
            case 8:
                drawable = getResources().getDrawable(R.drawable.player2);
                break;
            case 9:
                drawable = getResources().getDrawable(R.drawable.player3);
                break;
            case 10:
                drawable = getResources().getDrawable(R.drawable.player4);
                break;
            case 11:
                drawable = getResources().getDrawable(R.drawable.player5);
                break;
            case 12:
                drawable = getResources().getDrawable(R.drawable.player6);
                break;
            case 13:
                drawable = getResources().getDrawable(R.drawable.player7);
                break;
            case 14:
                drawable = getResources().getDrawable(R.drawable.player8);
                break;
            case 15:
                drawable = getResources().getDrawable(R.drawable.player9);
                break;
        }
    }

    private void tickEndlessly() {
        Handler mainLayout = new Handler();
        mainLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isDead && !pauseIsGame) {
                    tickEndlessly();
                }
                legoGame();
            }
        }, delayMillis);
    }

    private void legoGame() {
        Log.e(TAG, "legoGame: ");

        //Dynamic - Can be Changed
        int amountOfLegoColumn = 5;
        random = new Random();
        if (isDead)
            return;

        int legoCurrentPosition = random.nextInt(amountOfLegoColumn);

        LinearLayout linearLayoutManager = new LinearLayout(this);
        initLinearLayoutManager(linearLayoutManager);

        for (int i = 0; i < amountOfLegoColumn; i++) {

            LinearLayout linearLayout = new LinearLayout(this);
            initLinearLayout(linearLayout);

            if (i == legoCurrentPosition) {
                lego = new ImageView(this);
                lego.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                setColor();
                lego.setBackground(drawable);
                legoArrayList.add(lego);
                linearLayout.addView(lego);
            }
            linearLayoutManager.addView(linearLayout);
        }

        frameLayoutManager.addView(linearLayoutManager);

        ObjectAnimator animY = ObjectAnimator.ofFloat(lego, "Y", 0f, screenHeight - legoHeight);
        int legoFallDelayMillis = 700;
        animY.setDuration(legoFallDelayMillis);
        animY.setInterpolator(new LinearInterpolator());
        animY.start();
        animY.addUpdateListener(this);
        animY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                checkHit();
                int[] location = new int[2];
                legoArrayList.get(0).getLocationOnScreen(location);
                ObjectAnimator endAnimY = ObjectAnimator.ofFloat(legoArrayList.get(0), "Y", screenHeight - legoHeight, screenHeight);
                endAnimY.setDuration(100);
                endAnimY.start();
                endAnimY.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        checkHit();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        legoArrayList.get(0).setVisibility(View.GONE);
                        legoArrayList.remove(0);
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
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void checkHit() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0);

        if (checkValidity()) {
            isDead = true;
            ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
            if (lives > 0) {
                isDead = false;
                --lives;
                liveText.setText(getString(R.string.lives, (lives + 1)));
            } else {
                if (pref.getInt(getString(R.string.highestScore), -1) < highestScore) {
                    pref.edit().putInt(getString(R.string.highestScore), highestScore).apply();
                }
                mediaPlayer.stop();
                if (isDead) {
                    Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        } else {
            ++highestScore;
            if (pref.getInt(getString(R.string.highestScore), -1) < highestScore && pref.getInt(getString(R.string.highestScore), -1) > 0) {
                scoreText.setTextColor(Color.RED);
                scoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, highestScore)));
            } else
                scoreText.setText(getString(R.string.score, highestScore));
            if (highestScore % 10 == 0) {
                scoreAnimation.start();
                if (delayMillis > 200) {
                    delayMillis -= 10;
                }
            }

        }
    }

    private boolean checkValidity() {
        int[] location = new int[2];
        legoArrayList.get(0).getLocationOnScreen(location);
        if (isDead)
            return false;
        if ((location[0] > player.getX() && location[0] < player.getX() + legoWidth))
            return true;
        return (location[0] < player.getX() && location[0] + legoWidth > player.getX());
    }

    private void initLinearLayoutManager(LinearLayout linearLayoutManager) {
        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutManager.setGravity(Gravity.CENTER);
        linearLayoutManager.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private void initLinearLayout(LinearLayout linearLayout) {
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER | Gravity.TOP);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
    }

    @Override
    protected void onStop() {
        pauseIsGame = true;
        mediaPlayer.pause();
        super.onStop();
    }

    @Override
    protected void onResume() {
        pauseIsGame = false;
        mediaPlayer.start();
        tickEndlessly();
        super.onResume();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
    }
}
