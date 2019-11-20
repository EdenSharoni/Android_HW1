package com.example.android_hw;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = GameActivity.class.getSimpleName();
    private LinearLayout linearLayoutManager;
    private float height;
    private float legoHeight;
    private int loop;
    private int amountOfLegoColumn = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getScreenHeightDividedByLegoSize();
        initLinearLayoutManager();

        for (int i = 0; i < amountOfLegoColumn; i++) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setWeightSum(1);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayoutManager.addView(linearLayout);
            for (int j = 0; j < loop; j++) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams(lparams);
                imageView.setBackground(getResources().getDrawable(R.drawable.blue_lego));
                linearLayout.addView(imageView);
            }
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
        loop = Math.round(height/legoHeight);
    }
}
