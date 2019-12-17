package com.example.android_hw;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = GameOverActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        ButterKnife.bind(this);
        TextView highestScoreText = findViewById(R.id.score);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0);
        highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, pref.getInt(getString(R.string.highestScore), -1))));
        //getSupportFragmentManager().beginTransaction().attach(new HighestScoreFragment()).hide(new HighestScoreFragment());
    }

    @Override
    @OnClick({R.id.playAgainBtn, R.id.exitBtn, R.id.highestScoreBtn})
    public void onClick(View v) {
        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(20);
        switch (v.getId()) {
            case R.id.playAgainBtn:
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivityForResult(intent, 1);
                finish();
                break;
            case R.id.highestScoreBtn:
                HighestScoreFragment highestScoreFragment = new HighestScoreFragment();
                highestScoreFragment.show(getSupportFragmentManager(), TAG);
                break;
            case R.id.exitBtn:
                finish();
                break;
        }
    }
}
