package com.example.android_hw;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

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
    private User localUser;

    @BindView(R.id.gameOverTitle)
    ImageView gameOverTitle;
    @BindView(R.id.score)
    TextView highestScoreText;
    @BindView(R.id.googleBtn)
    ImageView googleSignIn;
    @BindView(R.id.signOutBtn)
    ImageView googleSignOut;
    @BindView(R.id.playBtn)
    ImageView play;
    @BindView(R.id.helpBtn)
    ImageView help;
    @BindView(R.id.highestScoreBtn)
    ImageView highestScore;
    @BindView(R.id.settingsBtn)
    ImageView settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        db = FirebaseFirestore.getInstance();

        user = getInstance().getCurrentUser();

        if (user == null)
            Log.e(TAG, "Start: user is null");
        else
            Log.e(TAG, "Start: user is NOT null");
        if (user == null) {
            localUser = new User(null, null, 0, true, true, 80);

            googleSignIn.setVisibility(View.VISIBLE);
            googleSignOut.setVisibility(View.GONE);
        } else {
            handleButtons(false);


            googleSignIn.setVisibility(View.GONE);
            googleSignOut.setVisibility(View.VISIBLE);
            getUserFromDB();
        }
        /*set highest score to 0
        getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0).edit().putInt("highestScore", 0).apply();*/
    }

    private void signIn() {
        //Starts the algorithm
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(true)
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                .build(), RC_SIGN_IN);
    }

    @Override
    @OnClick({R.id.playBtn, R.id.helpBtn, R.id.exitBtn, R.id.highestScoreBtn, R.id.settingsBtn, R.id.googleBtn, R.id.signOutBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playBtn:
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                intent.putExtra(String.valueOf(R.string.vibrate), localUser.isVibrateSettings());
                intent.putExtra(String.valueOf(R.string.music), localUser.isMusicSettings());
                intent.putExtra(getString(R.string.vibrationNumber), localUser.getVibrationNumber());
                startActivityForResult(intent, 1);
                break;
            case R.id.googleBtn:
                signIn();
                break;
            case R.id.signOutBtn:
                signOut();
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
                intent2.putExtra(getString(R.string.localUser), localUser);
                startActivityForResult(intent2, 2);
                break;
            case R.id.exitBtn:
                finish();
                break;
        }
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        googleSignIn.setVisibility(View.VISIBLE);
        googleSignOut.setVisibility(View.GONE);
        user = getInstance().getCurrentUser();
        if (user == null)
            Log.e(TAG, "user is null");
        else
            Log.e(TAG, "user is NOT null");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO - super has added check if function still good
        super.onActivityResult(requestCode, resultCode, data);

        //Google Request
        if (requestCode == RC_SIGN_IN) {
            user = getInstance().getCurrentUser();
            if (user == null)
                Log.e(TAG, "Google sign in: user is null");
            else
                Log.e(TAG, "Google sign in: user is NOT null");
            googleSignIn.setVisibility(View.GONE);
            googleSignOut.setVisibility(View.VISIBLE);
            if (user != null)
                getUserFromDB();
        }


        //GameActivity Request
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                gameOverTitle.setVisibility(View.VISIBLE);
                pref = getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0);
                highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, pref.getInt(getString(R.string.highestScore), 0))));
                if (localUser.getScore() < pref.getInt(getString(R.string.highestScore), 0)) {
                    localUser.setScore(pref.getInt(getString(R.string.highestScore), 0));
                    if (user != null)
                        setUserDB();
                }
            }
        }

        //Settings Request
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                localUser = (User) data.getExtras().get(getString(R.string.localUser));
                Log.e(TAG, "onActivityResult: VibrationNumber: " + localUser.getVibrationNumber());
                if (user != null) {
                    handleButtons(false);
                    setUserDB();
                }
            }
        }
        if (requestCode == 4) {
            localUser.setName(data.getStringExtra("name"));
            if (user != null)
                setUserDB();
        }
    }

    public void setUserDB() {

        db.collection("Users")
                .document(user.getUid())
                .set(localUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                handleButtons(true);
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
                                Log.e(TAG, "new User");
                                localUser = new User(user.getUid(), user.getDisplayName(), 0, true, true, 80);
                                setUserDB();
                            }
                            handleButtons(true);
                            if (localUser.getName().isEmpty()) {
                                startActivityForResult(new Intent(getApplicationContext(), PopUpNameActivity.class), 4);
                            }
                        }
                );
    }

    private void handleButtons(boolean bool) {
        play.setEnabled(bool);
        help.setEnabled(bool);
        highestScore.setEnabled(bool);
        settings.setEnabled(bool);
    }

    @Override
    protected void onStart() {
        super.onStart();
        pref = getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0);
        highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, pref.getInt(getString(R.string.highestScore), 0))));
    }
}