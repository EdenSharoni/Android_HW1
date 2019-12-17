package com.example.android_hw;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.android_hw.R.string.vibrate;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";
    private SharedPreferences pref;
    private boolean vibrate = true;
    private boolean music = true;
    @BindView(R.id.gameOverTitle)
    ImageView gameOverTitle;
    @BindView(R.id.score)
    TextView highestScoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        gameOverTitle.setVisibility(View.GONE);

        pref = getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0);
        highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, pref.getInt(getString(R.string.highestScore), -1))));

        /*set highest score to 0
        getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0).edit().putInt("highestScore", 0).apply();*/
    }

    @Override
    @OnClick({R.id.playBtn, R.id.helpBtn, R.id.exitBtn, R.id.highestScoreBtn, R.id.settingsBtn})
    public void onClick(View v) {
        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(20);
        switch (v.getId()) {
            case R.id.playBtn:
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                intent.putExtra(String.valueOf(R.string.vibrate), vibrate);
                intent.putExtra(String.valueOf(R.string.music), music);
                startActivityForResult(intent, 1);
                break;
            case R.id.helpBtn:
                startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                break;
            case R.id.highestScoreBtn:
                HighestScoreFragment highestScoreFragment = new HighestScoreFragment();
                highestScoreFragment.show(getSupportFragmentManager(), TAG);
                break;
            case R.id.settingsBtn:
                Intent intent2 = new Intent(getApplicationContext(), SettingsActivity.class);
                intent2.putExtra(String.valueOf(R.string.vibrate), vibrate);
                intent2.putExtra(String.valueOf(R.string.music), music);
                startActivityForResult(intent2, 2);
                break;
            case R.id.exitBtn:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                gameOverTitle.setVisibility(View.VISIBLE);
                pref = getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0);
                highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, pref.getInt(getString(R.string.highestScore), -1))));
            }
            if (resultCode == RESULT_CANCELED) {
                //TODO
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                vibrate = data.getBooleanExtra(String.valueOf(R.string.vibrate), true);
                music = data.getBooleanExtra(String.valueOf(R.string.music), true);
            }
            if (resultCode == RESULT_CANCELED) {
                //TODO
            }
        }
    }
}
