package com.example.android_hw;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int RC_SIGN_IN = 3;
    private final String TAG = "MainActivity";
    private SharedPreferences pref;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private AuthMethodPickerLayout customLayout;
    private User localUser;

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
                intent.putExtra(String.valueOf(R.string.vibrate), localUser.isVibrateSettings());
                intent.putExtra(String.valueOf(R.string.music), localUser.isMusicSettings());
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
                intent2.putExtra(String.valueOf(R.string.vibrate), localUser.isVibrateSettings());
                intent2.putExtra(String.valueOf(R.string.music), localUser.isMusicSettings());
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
                highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, pref.getInt(getString(R.string.highestScore), 0))));
                if (localUser.getScore() < pref.getInt(getString(R.string.highestScore), 0)) {
                    localUser.setScore(pref.getInt(getString(R.string.highestScore), 0));
                    setUserDB();
                }
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                localUser.setVibrateSettings(data.getBooleanExtra(String.valueOf(R.string.vibrate), true));
                localUser.setMusicSettings(data.getBooleanExtra(String.valueOf(R.string.music), true));
                setUserDB();
            }
        }
        if (requestCode == 4) {
            localUser.setName(data.getStringExtra("name"));
            setUserDB();
        }
    }


    public User getLocalUser() {
        return localUser;
    }

    public void setUserDB() {

        db.collection("Users")
                .document(user.getUid())
                .set(localUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO
            }
        });
    }


    private void getUserFromDB() {
        db.collection("Users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                            localUser = documentSnapshot.toObject(User.class);
                            if (localUser == null) {
                                localUser = new User(user.getUid(), user.getDisplayName(), 0, true, true);
                                setUserDB();
                            }
                            if (localUser.getName().isEmpty()) {
                                startActivityForResult(new Intent(getApplicationContext(), PopUpNameActivity.class), 4);
                            }
                        }
                );
    }


    @Override
    protected void onStart() {
        super.onStart();
        pref = getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0);
        highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, pref.getInt(getString(R.string.highestScore), 0))));

        user = getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if (user == null) {
            play.setVisibility(View.GONE);
            google.setVisibility(View.VISIBLE);
            initGoogle();
        } else {
            play.setVisibility(View.VISIBLE);
            google.setVisibility(View.GONE);
            getUserFromDB();
        }
    }
}
