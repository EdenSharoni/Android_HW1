package com.example.android_hw;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int RC_SIGN_IN = 3;
    private final String TAG = "MainActivity";
    private SharedPreferences pref;
    private boolean vibrate = true;
    private boolean music = true;
    private FirebaseUser user;
    private AuthMethodPickerLayout customLayout;

    @BindView(R.id.gameOverTitle)
    ImageView gameOverTitle;
    @BindView(R.id.score)
    TextView highestScoreText;
    @BindView(R.id.playBtn)
    ImageView play;
    @BindView(R.id.googleBtn)
    ImageView google;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        gameOverTitle.setVisibility(View.GONE);

        /*set highest score to 0
        getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0).edit().putInt("highestScore", 0).apply();*/
    }

    private void initGoogle() {
        //Handles the design
        customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_main)
                .setGoogleButtonId(R.id.googleBtn)
                .build();
    }

    private void signIn() {
        //Starts the algorithm
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(true)
                .setAuthMethodPickerLayout(customLayout)
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                .build(), RC_SIGN_IN);
    }

    @Override
    @OnClick({R.id.playBtn, R.id.helpBtn, R.id.exitBtn, R.id.highestScoreBtn, R.id.settingsBtn, R.id.googleBtn})
    public void onClick(View v) {
        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(20);
        switch (v.getId()) {
            case R.id.playBtn:
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                intent.putExtra(String.valueOf(R.string.vibrate), vibrate);
                intent.putExtra(String.valueOf(R.string.music), music);
                startActivityForResult(intent, 1);
                break;
            case R.id.googleBtn:
                signIn();
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
        //TODO - super has added check if function still good
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            play.setVisibility(View.VISIBLE);
            google.setVisibility(View.GONE);
        }

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                gameOverTitle.setVisibility(View.VISIBLE);
                pref = getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0);
                highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, pref.getInt(getString(R.string.highestScore), -1))));
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                vibrate = data.getBooleanExtra(String.valueOf(R.string.vibrate), true);
                music = data.getBooleanExtra(String.valueOf(R.string.music), true);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = getInstance().getCurrentUser();
        if (user == null) {
            play.setVisibility(View.GONE);
            google.setVisibility(View.VISIBLE);
            initGoogle();
        } else {
            play.setVisibility(View.VISIBLE);
            google.setVisibility(View.GONE);
        }

        pref = getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0);
        highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, pref.getInt(getString(R.string.highestScore), 0))));
    }
}
