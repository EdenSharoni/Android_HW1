package com.example.android_hw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = GameOverActivity.class.getSimpleName();
    private ImageView playAgain;
    private ImageView exit;
    private TextView highestScoreText;
    private Intent intent;
    private Vibrator vibe;
    private int highestScore;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        exit = findViewById(R.id.exitBtn);
        playAgain = findViewById(R.id.playAgainBtn);
        highestScoreText = findViewById(R.id.score);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        highestScoreText.setText("Highest Score: " + pref.getInt("highestScore", -1));
        exit.setOnClickListener(this);
        playAgain.setOnClickListener(this);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onClick(View v) {
        vibe.vibrate(20);
        switch (v.getId()) {
            case R.id.playAgainBtn:
                intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivityForResult(intent, 1);
                finish();
                break;
            case R.id.exitBtn:
                finish();
                break;
        }
    }
}
