package com.example.android_hw;

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
    private int height;
    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getScreenHeightAndWidth();


        /*linearLayoutManager = new LinearLayout(this);
        linearLayoutManager.setBackground(getResources().getDrawable(R.drawable.background_lego));
        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutManager.setGravity(Gravity.CENTER);

        for (int i = 0; i < 5; i++) {

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setWeightSum(1);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayoutManager.addView(linearLayout);
            for (int j = 0; j < height; j++) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams(lparams);
                imageView.setBackground(getResources().getDrawable(R.drawable.blue_lego));
                linearLayout.addView(imageView);
            }
        }
        setContentView(linearLayoutManager);*/
    }

    private void getScreenHeightAndWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
    }
}
