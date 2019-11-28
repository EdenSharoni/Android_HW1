package com.example.android_hw;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private static final String TAG = GameActivity.class.getSimpleName();
    private FrameLayout frameLayoutManager;
    private LinearLayout linearLayoutManager;
    private LinearLayout linearLayout;
    private TextView liveText;
    private TextView scoreText;
    private Drawable drawable;
    private Random random;
    private int legoCurrentPosition = 0;
    private int amountOfLegoColumn = 5;
    private int lives = 2;
    private boolean die = false;
    private Vibrator vibe;
    private int highestScore = 0;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int delayMillis = 1000;
    private int legoFallDelayMillis = 500;
    private ObjectAnimator animY;
    private ObjectAnimator scoreAnimation;
    private ImageView lego;
    private ImageView player;
    private float legoHeight;
    private float screenHeight;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        playMusic();
        getScreenHeightAndLegoHeight();
        initFrameLayoutManager();
        initPlayer();
        initLives();
        initScore();
        tickEndlessly();
        setContentView(frameLayoutManager);
    }

    private void getScreenHeightAndLegoHeight() {
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        legoHeight = getResources().getDrawable(R.drawable.blue_lego).getMinimumHeight();
    }

    private void playMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
            }
        });
    }

    private void initScore() {
        scoreText = new TextView(this);
        scoreText.setText("Score: " + highestScore);
        scoreText.setTextColor(Color.BLACK);
        scoreText.setTextSize(30f);
        scoreText.setGravity(Gravity.CENTER | Gravity.TOP);
        scoreText.setPadding(0, 300, 0, 0);
        scoreText.setVisibility(View.VISIBLE);
        frameLayoutManager.addView(scoreText);


        scoreAnimation = ObjectAnimator.ofFloat(scoreText, "rotation", 0f, 5f, 0f, -5f, 0f); // rotate o degree then 5 degree and so on for one loop of rotation.
        // animateView (View object)
        scoreAnimation.setInterpolator(new AccelerateInterpolator());
        scoreAnimation.setRepeatCount(2); // repeat the loop 20 times
        scoreAnimation.setDuration(100); // animation play time 100 ms
    }

    private void initLives() {
        liveText = new TextView(this);
        liveText.setText("Lives: " + (lives + 1));
        liveText.setTextColor(Color.BLACK);
        liveText.setTextSize(30f);
        frameLayoutManager.addView(liveText);
    }

    private void initFrameLayoutManager() {
        frameLayoutManager = new FrameLayout(this);
        frameLayoutManager.setBackground(getResources().getDrawable(R.drawable.background_lego));
    }

    private void initPlayer() {

        player = new ImageView(this);
        player.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        player.setBackground(getResources().getDrawable(R.drawable.player_lego));
        player.setX(Resources.getSystem().getDisplayMetrics().widthPixels / 2 - (getResources().getDrawable(R.drawable.player_lego).getMinimumWidth() / 2));
        player.setY(Resources.getSystem().getDisplayMetrics().heightPixels - legoHeight);
        frameLayoutManager.addView(player);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        player.setX(x - (legoHeight / 2));
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
        int[] location = new int[2];
        lego.getLocationOnScreen(location);

        if ((location[0] > player.getX() && location[0] < player.getX() + getResources().getDrawable(R.drawable.black_lego).getMinimumWidth()) || (location[0] < player.getX() && location[0] + getResources().getDrawable(R.drawable.black_lego).getMinimumWidth() > player.getX())) {
            vibe.vibrate(1000);
            if (lives > 0) {
                lives--;
                liveText.setText("Lives: " + (lives + 1));
                lego.setVisibility(View.INVISIBLE);
            } else {
                die = true;
                editor = pref.edit();
                if (pref.getInt("highestScore", -1) < highestScore) {
                    editor.putInt("highestScore", highestScore);
                    editor.commit();
                }
                mediaPlayer.stop();
                Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            ++highestScore;
            scoreText.setText("Score: " + highestScore);
            legoFallDelayMillis -= 2;
            if (highestScore % 10 == 0) {
                scoreAnimation.start();
                scoreAnimation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
            if (pref.getInt("highestScore", -1) < highestScore && pref.getInt("highestScore", -1) > 0)
                scoreText.setTextColor(Color.RED);
        }
    }

    private void legoGame() {
        random = new Random();
        if (die)
            return;

        legoCurrentPosition = random.nextInt(amountOfLegoColumn - 0);

        linearLayoutManager = new LinearLayout(this);
        initLinearLayoutManager(linearLayoutManager);

        for (int i = 0; i < amountOfLegoColumn; i++) {

            linearLayout = new LinearLayout(this);
            initLinearLayout(linearLayout);

            if (i == legoCurrentPosition) {
                lego = new ImageView(this);
                lego.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                setColor();
                lego.setBackground(drawable);
                linearLayout.addView(lego);
            }
            linearLayoutManager.addView(linearLayout);
        }


        frameLayoutManager.addView(linearLayoutManager);

        animY = ObjectAnimator.ofFloat(lego, "Y", 0f, screenHeight);
        animY.setInterpolator(new AccelerateInterpolator());
        animY.setDuration(legoFallDelayMillis);
        animY.start();
        animY.addListener(new Animator.AnimatorListener() {
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
        });
    }
}
