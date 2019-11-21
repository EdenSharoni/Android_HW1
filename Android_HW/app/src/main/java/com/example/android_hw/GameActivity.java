package com.example.android_hw;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = GameActivity.class.getSimpleName();
    private FrameLayout frameLayoutManager;
    private Drawable drawable;
    private float height;
    private float legoHeight;
    private int screenHeightDividedByLegoSize;
    private int amountOfLegoColumn = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getScreenHeightDividedByLegoSize();
        initFrameLayoutManager();
        initLinearLayoutWithLegoBlocks();
        initButton();
        initPlayer();
        setContentView(frameLayoutManager);
    }

    private void initFrameLayoutManager() {
        frameLayoutManager = new FrameLayout(this);
        frameLayoutManager.setBackground(getResources().getDrawable(R.drawable.background_lego));
    }


    private void initLinearLayoutWithLegoBlocks() {

        LinearLayout linearLayoutManager = new LinearLayout(this);
        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutManager.setGravity(Gravity.CENTER);
        linearLayoutManager.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));


        drawable = getResources().getDrawable(R.drawable.blue_lego);

        for (int i = 0; i < amountOfLegoColumn; i++) {

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));

            setColor();

            for (int j = 0; j < screenHeightDividedByLegoSize; j++) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                imageView.setBackground(drawable);

                linearLayout.addView(imageView);
            }
            linearLayoutManager.addView(linearLayout);
        }
        frameLayoutManager.addView(linearLayoutManager);
    }

    private void initButton() {
        LinearLayout linearLayoutManager = new LinearLayout(this);
        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutManager.setGravity(Gravity.CENTER);
        linearLayoutManager.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        for (int i = 0; i < 2; i++) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
            Button button = new Button(this);
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setEnabled(true);
            button.setOnClickListener(this);
            linearLayout.addView(button);
            linearLayoutManager.addView(linearLayout);
        }
        frameLayoutManager.addView(linearLayoutManager);
    }

    private void initPlayer() {
        LinearLayout linearLayoutManager = new LinearLayout(this);
        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutManager.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        linearLayoutManager.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));


        for (int j = 0; j < amountOfLegoColumn; j++) {

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER | Gravity.BOTTOM);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));


            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            imageView.setBackground(getResources().getDrawable(R.drawable.player_lego));

            linearLayout.addView(imageView);
            linearLayoutManager.addView(linearLayout);
        }
        frameLayoutManager.addView(linearLayoutManager);
    }

    private void setColor() {
        Random random = new Random();
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

    @Override
    public void onClick(View v) {
        Toast.makeText(getApplicationContext(), "button", Toast.LENGTH_SHORT).show();
    }
}
