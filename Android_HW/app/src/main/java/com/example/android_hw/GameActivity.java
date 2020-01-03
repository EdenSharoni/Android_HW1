package com.example.android_hw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = GameActivity.class.getSimpleName();
    private FrameLayout frameLayoutManager;
    private LinearLayout heartsLinearLayout;
    private ArrayList<ImageView> hearts = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private Player player;
    private Score scoreText;
    private boolean pauseIsGame = false;
    private User localUser;
    private long lastTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initGame();
        setContentView(frameLayoutManager);
    }

    private void initGame() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            localUser = (User) bundle.get(getString(R.string.localUser));
        } else {
            localUser = new User();
            localUser.setMusicSettings(true);
            localUser.setVibrationNumber(80);
            localUser.setControls(getString(R.string.screen));
        }

        //Setup main layout
        frameLayoutManager = new FrameLayout(this);
        frameLayoutManager.setBackground(getResources().getDrawable(R.drawable.background_lego));

        //Setup Player
        player = new Player(this);
        frameLayoutManager.addView(player);

        //SetUp Score
        scoreText = new Score(this);
        frameLayoutManager.addView(scoreText);

        //Play music
        if (localUser.isMusicSettings()) {
            mediaPlayer = MediaPlayer.create(this, R.raw.music);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(0.5f, 0.5f);
            mediaPlayer.start();
        }

        //Setup hearts layout
        heartsLinearLayout = new LinearLayout(this);
        addHeartsToGame();
        frameLayoutManager.addView(heartsLinearLayout);
    }

    private void addHeartsToGame() {
        heartsLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        clearHearts();
        for (int i = 0; i < player.getNum_lives(); i++) {
            addHeart(heartsLinearLayout);
        }
    }

    private void clearHearts() {
        for (int i = 0; i < hearts.size(); i++) {
            hearts.get(i).setVisibility(View.GONE);
        }
        hearts.clear();
    }

    private void addHeart(LinearLayout linearLayout) {
        Lives heartImg = new Lives(this);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (localUser.getControls().equals(getString(R.string.screen))) {
            float x = event.getX();
            player.setX(x - ((float) getResources().getDrawable(R.drawable.red_lego).getMinimumHeight() / 2));
        }
        return super.onTouchEvent(event);
    }

    private void runMotionControllerThread() {
        new Thread(new Runnable() {
            private float newX;

            @Override
            public void run() {
                while (!pauseIsGame) {
                    lastTime = System.currentTimeMillis();
                    newX = player.updatePlayerPositionUsingMotionSensor(lastTime);
                    player.post(new Runnable() {
                        @Override
                        // update player image view x position on UI thread
                        public void run() {
                            player.setX(newX);
                        }
                    });
                }
            }
        }).start();
    }

    private void tickEndlessly() {
        Handler mainLayout = new Handler();
        mainLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!pauseIsGame) {
                    tickEndlessly();
                }
                legoGame();
            }
        }, scoreText.getDelayMillis());
    }

    private void legoGame() {
        //Dynamic - Can be Changed
        int amountOfLegoColumn = 5;
        Random random = new Random();

        int legoCurrentPosition = random.nextInt(amountOfLegoColumn);

        LinearLayout linearLayoutManager = new LinearLayout(this);
        initLinearLayoutManager(linearLayoutManager);

        for (int i = 0; i < amountOfLegoColumn; i++) {

            LinearLayout linearLayout = new LinearLayout(this);
            initLinearLayout(linearLayout);

            if (i == legoCurrentPosition) {
                int legoNumber = random.nextInt(10);
                Lego lego;
                //Random superHead
                if (legoNumber == 5) {
                    lego = new SuperHead(this);
                }
                //Random coin
                else if (legoNumber == 7) {
                    lego = new Coin(this);
                }
                //Random lego
                else {
                    lego = new Lego(this);
                }
                lego.animateLego();
                linearLayout.addView(lego);
            }
            linearLayoutManager.addView(linearLayout);
        }
        frameLayoutManager.addView(linearLayoutManager);
    }

    public Score getScore() {
        return scoreText;
    }

    public void EndGame() {
        pauseIsGame = true;
        scoreText.setHighestScoreEndGame();
        if (localUser.isMusicSettings())
            mediaPlayer.stop();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(getString(R.string.localUser), localUser);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public boolean gameHasEnded() {
        return pauseIsGame;
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
        player.getOrientationData().pause();
        pauseIsGame = true;
        if (localUser.isMusicSettings())
            mediaPlayer.pause();
        super.onStop();
    }

    @Override
    protected void onResume() {
        pauseIsGame = false;
        if (localUser.getControls().equals(getString(R.string.motion))) {
            player.getOrientationData().register();
            runMotionControllerThread();
        }
        if (localUser.isMusicSettings())
            mediaPlayer.start();
        tickEndlessly();
        super.onResume();
    }

    public Player getPlayer() {
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
        if (localUser.getVibrationNumber() != 0)
            ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(localUser.getVibrationNumber() * 5);
    }

    public User getLocalUser() {
        return localUser;
    }
}
