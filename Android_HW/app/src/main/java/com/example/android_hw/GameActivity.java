package com.example.android_hw;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = GameActivity.class.getSimpleName();
    private LinearLayout linearLayoutManager;
    private float height;
    private float legoHeight;
    private int screenHeightDividedByLegoSize;
    private int amountOfLegoColumn = 6;
    private LinearLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getScreenHeightDividedByLegoSize();
        initLinearLayoutManager();

        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < amountOfLegoColumn; i++) {

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));

            for (int j = 0; j < screenHeightDividedByLegoSize; j++) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(layoutParams);
                imageView.setBackground(getResources().getDrawable(R.drawable.blue_lego));
                linearLayout.addView(imageView);
            }
            linearLayoutManager.addView(linearLayout);
        }
        setContentView(linearLayoutManager);
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
