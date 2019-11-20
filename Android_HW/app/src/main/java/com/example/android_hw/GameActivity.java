package com.example.android_hw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        LinearLayout linearLayoutManager = new LinearLayout(this);
        linearLayoutManager.setBackground(getResources().getDrawable(R.drawable.background_lego));
        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutManager.setGravity(Gravity.CENTER);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setWeightSum(1);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

        linearLayoutManager.addView(linearLayout);

        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(lparams);
        imageView.setBackground(getResources().getDrawable(R.drawable.blue_lego));

        linearLayout.addView(imageView);

        ImageView imageView2 = new ImageView(this);
        LinearLayout.LayoutParams lparams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView2.setLayoutParams(lparams2);
        imageView2.setBackground(getResources().getDrawable(R.drawable.red_lego));


        linearLayout.addView(imageView2);


        setContentView(linearLayoutManager);
    }
}
