package com.example.android_hw;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = GameActivity.class.getSimpleName();
    private LinearLayout linearLayoutManager;
    private LinearLayout.LayoutParams layoutParams;
    private Drawable drawable;
    private float height;
    private float legoHeight;
    private int screenHeightDividedByLegoSize;
    private int amountOfLegoColumn = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getScreenHeightDividedByLegoSize();
        initLinearLayoutManager();
        // initLinearLayoutWithLegoBlocks();
       // initPlayer();
        initButtons();
        setContentView(linearLayoutManager);
    }

    private void initButtons() {
        for (int i = 0; i < 2; i++) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));

            Button button = new Button(this);
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            button.setLayoutParams(layoutParams);
           // button.setVisibility(View.INVISIBLE);
            linearLayout.addView(button);
            linearLayoutManager.addView(linearLayout);
        }

    }

    private void initPlayer() {

        for (int i = 0; i < amountOfLegoColumn; i++) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));

            ImageView imageView = new ImageView(this);
            imageView.setBackground(getResources().getDrawable(R.drawable.player_lego));
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.BOTTOM;
            imageView.setLayoutParams(layoutParams);
            linearLayout.addView(imageView);
            linearLayoutManager.addView(linearLayout);
        }
    }


    private void initLinearLayoutWithLegoBlocks() {

        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        drawable = getResources().getDrawable(R.drawable.blue_lego);

        for (int i = 0; i < amountOfLegoColumn; i++) {

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));

            setColor();

            for (int j = 0; j < screenHeightDividedByLegoSize; j++) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(layoutParams);
                imageView.setBackground(drawable);
                linearLayout.addView(imageView);
            }
            linearLayoutManager.addView(linearLayout);
        }
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


    private void initLinearLayoutManager() {
        linearLayoutManager = new LinearLayout(this);
        linearLayoutManager.setBackground(getResources().getDrawable(R.drawable.background_lego));
        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutManager.setGravity(Gravity.CENTER);
    }

    private void getScreenHeightDividedByLegoSize() {
        height = Resources.getSystem().getDisplayMetrics().heightPixels;
        legoHeight = getResources().getDrawable(R.drawable.blue_lego).getMinimumHeight();
        screenHeightDividedByLegoSize = Math.round(height / legoHeight);
    }
}
