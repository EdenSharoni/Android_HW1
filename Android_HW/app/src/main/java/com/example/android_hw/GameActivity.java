package com.example.android_hw;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = GameActivity.class.getSimpleName();
    private FrameLayout frameLayoutManager;
    private LinearLayout linearLayout;
    private LinearLayout linearLayoutManager;
    private TextView textView;
    private Drawable drawable;
    private Random random;
    private ArrayList<ImageView> playerArrayList = new ArrayList<>();
    private ArrayList<ImageView> legoArrayList = new ArrayList<>();
    private int playerCurrentPosition;
    private int legoCurrentPosition = 0;
    private int legoCheck = -1;
    private int screenHeightDividedByLegoSize;
    private int amountOfLegoColumn = 3;
    private int lives = 3;
    private boolean hit = false;
    private Vibrator vibe;
    private int highestScore = 0;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        getScreenHeightDividedByLegoSize();
        initFrameLayoutManager();
        initLinearLayoutWithLegoBlocks();
        initButton();
        initPlayer();
        initLives();

        //test
        /*legoArrayList.get(screenHeightDividedByLegoSize*0).setVisibility(View.VISIBLE);
        legoArrayList.get(screenHeightDividedByLegoSize).setVisibility(View.VISIBLE);
        legoArrayList.get(screenHeightDividedByLegoSize*2).setVisibility(View.VISIBLE);
        legoArrayList.get(screenHeightDividedByLegoSize*3).setVisibility(View.VISIBLE);
        legoArrayList.get(screenHeightDividedByLegoSize*4).setVisibility(View.VISIBLE);*/

        tickEndlessly();
        setContentView(frameLayoutManager);
    }

    private void initLives() {
        textView = new TextView(this);
        textView.setText("Lives: " + lives);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(30f);
        frameLayoutManager.addView(textView);
    }

    private void initFrameLayoutManager() {
        frameLayoutManager = new FrameLayout(this);
        frameLayoutManager.setBackground(getResources().getDrawable(R.drawable.background_lego));
    }

    private void initLinearLayoutWithLegoBlocks() {
        ImageView lego;


        linearLayoutManager = new LinearLayout(this);
        initLinearLayoutManager(linearLayoutManager);

        for (int i = 0; i < amountOfLegoColumn; i++) {
            linearLayout = new LinearLayout(this);
            initLinearLayout(linearLayout, Gravity.CENTER);
            setColor();

            for (int j = 0; j < screenHeightDividedByLegoSize; j++) {
                lego = new ImageView(this);
                lego.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                lego.setBackground(drawable);
                lego.setVisibility(View.INVISIBLE);
                legoArrayList.add(lego);
                linearLayout.addView(lego);
            }
            linearLayoutManager.addView(linearLayout);
        }
        frameLayoutManager.addView(linearLayoutManager);
    }

    private void initButton() {
        Button button;
        linearLayoutManager = new LinearLayout(this);
        initLinearLayoutManager(linearLayoutManager);

        for (int i = 0; i < 2; i++) {
            linearLayout = new LinearLayout(this);
            initLinearLayout(linearLayout, Gravity.CENTER);
            button = new Button(this);
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setEnabled(true);
            button.setOnClickListener(this);
            button.setId(i);
            linearLayout.addView(button);
            linearLayoutManager.addView(linearLayout);
        }
        frameLayoutManager.addView(linearLayoutManager);
    }

    private void initPlayer() {
        ImageView player;

        playerCurrentPosition = amountOfLegoColumn / 2;

        linearLayoutManager = new LinearLayout(this);
        initLinearLayoutManager(linearLayoutManager);

        for (int j = 0; j < amountOfLegoColumn; j++) {

            linearLayout = new LinearLayout(this);
            initLinearLayout(linearLayout, Gravity.CENTER | Gravity.BOTTOM);

            player = new ImageView(this);
            player.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            player.setBackground(getResources().getDrawable(R.drawable.player_lego));
            player.setVisibility(View.INVISIBLE);
            playerArrayList.add(player);
            linearLayout.addView(player);
            linearLayoutManager.addView(linearLayout);
        }
        playerArrayList.get(playerCurrentPosition).setVisibility(View.VISIBLE);
        frameLayoutManager.addView(linearLayoutManager);
    }

    private void setColor() {
        random = new Random();
        int i = random.nextInt(5 - 0);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 0: //left
                if (playerCurrentPosition != 0) {
                    playerArrayList.get(playerCurrentPosition).setVisibility(View.INVISIBLE);
                    playerCurrentPosition--;
                    playerArrayList.get(playerCurrentPosition).setVisibility(View.VISIBLE);
                }
                break;
            case 1: //right
                if (playerCurrentPosition != (amountOfLegoColumn - 1)) {
                    playerArrayList.get(playerCurrentPosition).setVisibility(View.INVISIBLE);
                    playerCurrentPosition++;
                    playerArrayList.get(playerCurrentPosition).setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void tickEndlessly() {
        Handler mainLayout = new Handler();
        mainLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!hit)
                    tickEndlessly();
                legoGame();
            }
        }, 1000);
    }

    private void checkHit() {

        for (int i = 0; i < amountOfLegoColumn * screenHeightDividedByLegoSize; i++) {
            if (legoArrayList.get(i).getVisibility() == View.VISIBLE && i / (screenHeightDividedByLegoSize) == playerCurrentPosition && i % (screenHeightDividedByLegoSize) == (screenHeightDividedByLegoSize - 1) && !hit) {
                vibe.vibrate(1000);
                if (lives > 0) {
                    lives--;
                    textView.setText("Lives: " + lives);
                    legoArrayList.get(i).setVisibility(View.INVISIBLE);
                } else {
                    hit = true;

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
    }

    private void legoGame() {

        checkHit();
        random = new Random();
        if (hit)
            return;
        legoCurrentPosition = random.nextInt(amountOfLegoColumn - 0);

        /*if (legoCheck != legoCurrentPosition)
            legoCheck = legoCurrentPosition;
        else {
            if (legoCurrentPosition == (amountOfLegoColumn - 1))
                legoCurrentPosition--;
            else
                legoCurrentPosition++;
            legoCheck = legoCurrentPosition;
        }*/

        legoArrayList.get(legoCurrentPosition * screenHeightDividedByLegoSize).setVisibility(View.VISIBLE);

        for (int i = 0; i < amountOfLegoColumn * screenHeightDividedByLegoSize; i++) {

            if (i == legoCurrentPosition * screenHeightDividedByLegoSize) {
                continue;
            }

            if (legoArrayList.get(i).getVisibility() == View.VISIBLE) {
                legoArrayList.get(i).setVisibility(View.INVISIBLE);
                if (i % (screenHeightDividedByLegoSize) == (screenHeightDividedByLegoSize - 1)) {
                    highestScore++;
                    continue;
                }
                if (i != (amountOfLegoColumn * screenHeightDividedByLegoSize) - 1)
                    legoArrayList.get(++i).setVisibility(View.VISIBLE);
            }
        }
        checkHit();
    }
}
