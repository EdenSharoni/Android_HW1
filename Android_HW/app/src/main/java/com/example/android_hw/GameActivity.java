package com.example.android_hw;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
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
    private Drawable drawable;
    private float height;
    private float legoHeight;
    private int screenHeightDividedByLegoSize;
    private int amountOfLegoColumn = 5;
    private ImageView lego;
    private ImageView player;
    private LinearLayout linearLayout;
    private LinearLayout linearLayoutManager;
    private Button button;
    private int playerCurrentPosition;
    private int legoCurrentPosition = 0;
    private ArrayList<ImageView> playerArrayList = new ArrayList<>();
    private ArrayList<ImageView> legoArrayList = new ArrayList<>();
    private int lives = 3;
    private Random random;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getScreenHeightDividedByLegoSize();
        initFrameLayoutManager();
        initLives();
        initLinearLayoutWithLegoBlocks();
        initButton();
        initPlayer();
        tickEndlessly();
        setContentView(frameLayoutManager);
    }

    private void initLives() {
        TextView textView = new TextView(this);
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
        playerCurrentPosition = amountOfLegoColumn / 2;

        linearLayoutManager = new LinearLayout(this);
        initLinearLayoutManager(linearLayoutManager);

        for (int j = 0; j < amountOfLegoColumn; j++) {

            linearLayout = new LinearLayout(this);
            initLinearLayout(linearLayout, Gravity.CENTER | Gravity.BOTTOM);

            player = new ImageView(this);
            player.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            player.setBackground(getResources().getDrawable(R.drawable.player_lego));
            player.setId(j);
            playerArrayList.add(player);
            if (player.getId() == amountOfLegoColumn / 2)
                player.setVisibility(View.VISIBLE);
            else
                player.setVisibility(View.INVISIBLE);

            linearLayout.addView(player);
            linearLayoutManager.addView(linearLayout);
        }
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
        height = Resources.getSystem().getDisplayMetrics().heightPixels;
        legoHeight = getResources().getDrawable(R.drawable.blue_lego).getMinimumHeight();
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
        player = playerArrayList.get(playerCurrentPosition);
        switch (v.getId()) {
            case 0: //left
                if (playerCurrentPosition != 0) {
                    player.setVisibility(View.INVISIBLE);
                    playerCurrentPosition--;
                    player = playerArrayList.get(playerCurrentPosition);
                    player.setVisibility(View.VISIBLE);
                }
                break;
            case 1: //right
                if (playerCurrentPosition != (amountOfLegoColumn - 1)) {
                    player.setVisibility(View.INVISIBLE);
                    playerCurrentPosition++;
                    player = playerArrayList.get(playerCurrentPosition);
                    player.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void tickEndlessly() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    random = new Random();
                    legoGame();
                    tick();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void legoGame() {
        //legoCurrentPosition = random.nextInt(2 - 0);
        legoCurrentPosition++;
        Log.e(TAG, "legoGame: " + legoCurrentPosition);
        lego = legoArrayList.get(legoCurrentPosition);
        lego.setVisibility(View.VISIBLE);
    }

    private void tick() {
        Log.d(TAG, "ticking");
    }
}
