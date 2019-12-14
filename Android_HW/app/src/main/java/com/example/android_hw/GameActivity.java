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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = GameActivity.class.getSimpleName();
    private ArrayList<ImageView> legoArrayList = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private FrameLayout frameLayoutManager;
    private ObjectAnimator scoreAnimation;
    private PlayerActivity player;
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
    private Lives heartImg;
    private LinearLayout heartsLinearLayout;
    private ArrayList<ImageView> hearts = new ArrayList<>();
    private SharedPreferences pref;
    ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        pref = getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0);
        playMusic();
        getScreenHeightAndWidthAndLegoHeightAndWidth();
        initFrameLayoutManager();
        initPlayer();
        heartsLinearLayout = new LinearLayout(this);
        addHeartsToGame();
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
        player = new PlayerActivity(this);
        frameLayoutManager.addView(player);
    }

    private void addHeartsToGame() {
        heartsLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        clearHearts();
        for (int i = 0; i < player.getNum_lives(); i++) {
            addHeart(heartsLinearLayout);
        }
        frameLayoutManager.addView(heartsLinearLayout);
    }

    private void clearHearts() {
        for (int i = 0; i < hearts.size(); i++) {
            hearts.get(i).setVisibility(View.GONE);
        }
        hearts.clear();
    }

    private void addHeart(LinearLayout linearLayout) {
        heartImg = new Lives(this);
        hearts.add(heartImg);
        linearLayout.addView(heartImg);
    }

    public void addLife() {
        addHeartsToGame();
    }

    public void removeLife() {
        if (player.getNum_lives() >= 0)
            hearts.get(player.getNum_lives() - 1).setVisibility(View.GONE);
        heartsLinearLayout.removeView(hearts.get(player.getNum_lives() - 1));
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
                Lego lego = new Lego(this);
                lego.animateLego();
                legoArrayList.add(lego);
                linearLayout.addView(lego);
            }
            linearLayoutManager.addView(linearLayout);
        }
        frameLayoutManager.addView(linearLayoutManager);
    }

    public void checkHit() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0);
        if (true) {
            player.hit();
            isDead = true;
            ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
            if (player.getNum_lives() > 0) {
                isDead = false;
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

    public void updateHighestScore() {
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

    public void EndGame() {

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

    public PlayerActivity getPlayer() {
        return player;
    }

    public static boolean checkCollision(ImageView player, ImageView lego) {

        int player_width = player.getDrawable().getMinimumWidth();
        int player_height = player.getDrawable().getMinimumHeight();

        int lego_width = lego.getDrawable().getMinimumWidth();
        int lego_height = lego.getDrawable().getMinimumHeight();

        double gracePercentY = 0.6;
        double gracePercentX = 0.1;

        int[] location = new int[2];
        lego.getLocationOnScreen(location);

        Rect R1 = new Rect((int) ((int) player.getX() + player_width * gracePercentX), (int) ((int) player.getY() + gracePercentY * player_height), (int) (player.getX() + player_width * (1 - gracePercentX)), (int) ((int) player.getY() + player_height * (1 + gracePercentY)));
        Rect R2 = new Rect(location[0], location[1], (location[0] + lego_width), location[1] + lego_height);

        return R1.intersect(R2);
    }

    public void Vibrate() {
        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
    }
}
