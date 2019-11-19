package com.example.android_hw;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView playAgain;
    private ImageView exit;
    private Intent intent;
    private Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        exit = findViewById(R.id.exitBtn);
        playAgain = findViewById(R.id.playAgainBtn);
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
                startActivity(intent);
                finish();
                break;
            case R.id.exitBtn:
                finish();
                break;
        }
    }
}
