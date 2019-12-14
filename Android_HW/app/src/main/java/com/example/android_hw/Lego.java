package com.example.android_hw;

import android.animation.ValueAnimator;
import android.content.Context;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

import java.util.Random;

public class Lego extends AppCompatImageView implements ValueAnimator.AnimatorUpdateListener {

    private Random random;

    public Lego(Context context) {
        super(context);
        setLego();
    }

    private void setLego() {
        this.setImageResource(R.drawable.red_lego);
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setColor();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

    }

    private void setColor() {
        random = new Random();
        int i = random.nextInt(7);
        switch (i) {
            case 0:
                this.getResources().getDrawable(R.drawable.blue_lego);
                break;
            case 1:
                this.getResources().getDrawable(R.drawable.red_lego);
                break;
            case 2:
                this.getResources().getDrawable(R.drawable.yellow_lego);
                break;
            case 3:
                this.getResources().getDrawable(R.drawable.green_lego);
                break;
            case 4:
                this.getResources().getDrawable(R.drawable.pink_lego);
                break;
            case 5:
                this.getResources().getDrawable(R.drawable.orange_lego);
                break;
            case 6:
                this.getResources().getDrawable(R.drawable.purple_lego);
                break;
        }
    }
}
