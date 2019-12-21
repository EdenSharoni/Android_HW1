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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthMethodPickerLayout;
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
    private AuthMethodPickerLayout customLayout;
    private User localUser;

    @BindView(R.id.gameOverTitle)
    ImageView gameOverTitle;
    @BindView(R.id.score)
    TextView highestScoreText;
    @BindView(R.id.googleBtn)
    ImageView googleSignIn;
    @BindView(R.id.signOutBtn)
    ImageView googleSignOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
                intent2.putExtra(String.valueOf(R.string.vibrate), localUser.isVibrateSettings());
                intent2.putExtra(String.valueOf(R.string.music), localUser.isMusicSettings());
                intent2.putExtra("name", localUser.getName());
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

        if (requestCode == RC_SIGN_IN) {
            user = getInstance().getCurrentUser();
            if (user == null)
                Log.e(TAG, "user is null");
            else
                Log.e(TAG, "user is NOT null");
            googleSignIn.setVisibility(View.GONE);
            googleSignOut.setVisibility(View.VISIBLE);
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
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                localUser.setVibrateSettings(data.getBooleanExtra(String.valueOf(R.string.vibrate), true));
                localUser.setMusicSettings(data.getBooleanExtra(String.valueOf(R.string.music), true));
                localUser.setName(data.getStringExtra("name"));
                if (user != null)
                    setUserDB();
            }
        }
        if (requestCode == 4) {
            localUser.setName(data.getStringExtra("name"));
            if (user != null)
                setUserDB();
        }
    }


    public FirebaseUser getUser() {
        return user;
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
                                localUser.setId(user.getUid());
                                localUser.setName(user.getDisplayName());
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
        if (user == null)
            Log.e(TAG, "user is null");
        else
            Log.e(TAG, "user is NOT null");
        if (user == null) {
            localUser = new User();
            localUser.setVibrateSettings(true);
            localUser.setMusicSettings(true);
            localUser.setScore(0);
            googleSignIn.setVisibility(View.VISIBLE);
            googleSignOut.setVisibility(View.GONE);
        } else {
            googleSignIn.setVisibility(View.GONE);
            googleSignOut.setVisibility(View.VISIBLE);
            getUserFromDB();
        }
    }
}
