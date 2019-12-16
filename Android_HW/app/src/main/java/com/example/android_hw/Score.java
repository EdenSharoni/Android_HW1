package com.example.android_hw;

import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.appcompat.widget.AppCompatTextView;

public class Score extends AppCompatTextView {
    private SharedPreferences pref;
    private int highestScore = 0;
    private ObjectAnimator scoreAnimation;
    private int delayMillis = 400;

    public Score(GameActivity context) {
        super(context);
        pref = context.getApplicationContext().getSharedPreferences(getResources().getString(R.string.MyPref), 0);
        setScore();
        setScoreAnimation();
    }

    private void setScore() {
        this.setText(getResources().getString(R.string.score, highestScore));
        this.setTextColor(Color.BLACK);
        this.setTextSize(30f);
        this.setGravity(Gravity.CENTER | Gravity.TOP);
        this.setPadding(0, 300, 0, 0);
        this.setVisibility(View.VISIBLE);
    }

    private void setScoreAnimation() {
        scoreAnimation = ObjectAnimator.ofFloat(this, "rotation", 0f, 5f, 0f, -5f, 0f); // rotate o degree then 5 degree and so on for one loop of rotation.
        scoreAnimation.setInterpolator(new AccelerateInterpolator());
        scoreAnimation.setRepeatCount(2); // repeat the loop 20 times
        scoreAnimation.setDuration(100); // animation play time 100 ms
    }

    public void updateHighestScore() {
        ++highestScore;
        if (pref.getInt((getResources().getString(R.string.highestScore)), -1) < highestScore && pref.getInt((getResources().getString(R.string.highestScore)), -1) > 0) {
            this.setTextColor(Color.RED);
            this.setText(String.format("%s %s", (getResources().getString(R.string.highest)), (getResources().getString(R.string.score, highestScore))));
        } else
            this.setText((getResources().getString(R.string.score, highestScore)));
        if (highestScore % 10 == 0) {
            scoreAnimation.start();
            if (delayMillis > 200) {
                delayMillis -= 10;
            }
        }
    }

    public int getDelayMillis() {
        return delayMillis;
    }

    public void setHighestScoreEndGame(){
        if (pref.getInt(getResources().getString(R.string.highestScore), -1) < highestScore) {
            pref.edit().putInt(getResources().getString(R.string.highestScore), highestScore).apply();
        }
    }
}
