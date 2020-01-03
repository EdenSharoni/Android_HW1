package com.example.android_hw;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_GPS = 1;
    private final int GAME_REQUEST_CODE = 1;
    private final int SETTINGS_REQUEST_CODE = 2;
    private final int POP_UP_NAME_REQUEST_CODE = 4;

    private final String TAG = "MainActivity";
    private FirebaseUser user;
    private FirebaseFirestore db;
    private User localUser;
    private Intent intent;
    private GPSService gpsService;
    private FirebaseAuth mAuth;

    @BindView(R.id.gameOverTitle)
    ImageView gameOverTitle;
    @BindView(R.id.score)
    TextView highestScoreText;
    @BindView(R.id.playBtn)
    ImageView play;
    @BindView(R.id.helpBtn)
    ImageView help;
    @BindView(R.id.highestScoreBtn)
    ImageView highestScore;
    @BindView(R.id.settingsBtn)
    ImageView settings;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        handleButtons(false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
        } else {
            findUser();
        }

        /*set highest score to 0
        getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0).edit().putInt("highestScore", 0).apply();*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findUser();
            }
        }
    }

    private void findUser() {
        gpsService = new GPSService(MainActivity.this);
        if (user == null) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                user = mAuth.getCurrentUser();
                                localUser = new User(user.getUid(), "", 0, true, 80, getString(R.string.screen), gpsService.getMyLocation().getLatitude(), gpsService.getMyLocation().getLongitude());
                                highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, localUser.getScore())));
                                setUserDB();
                            }
                        }
                    });
        } else {
            getUserFromDB();
        }
    }

    public void setUserDB() {
        db.collection("Users")
                .document(user.getUid())
                .set(localUser).addOnSuccessListener(aVoid -> checkNameValid()).addOnFailureListener(e -> Log.e(TAG, getString(R.string.error_saving_data)));
    }

    private void checkNameValid() {
        if (localUser.getName().isEmpty()) {
            startActivityForResult(new Intent(getApplicationContext(), PopUpNameActivity.class), POP_UP_NAME_REQUEST_CODE);
        } else {
            handleButtons(true);
        }
    }

    private void getUserFromDB() {
        db.collection("Users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                            localUser = documentSnapshot.toObject(User.class);
                            if (localUser == null) {
                                localUser = new User(user.getUid(), user.getDisplayName(), 0, true, 80, getString(R.string.screen), gpsService.getMyLocation().getLatitude(), gpsService.getMyLocation().getLongitude());
                                setUserDB();
                            } else {
                                checkNameValid();
                            }
                            highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, localUser.getScore())));
                        }
                );
    }

    @Override
    @OnClick({R.id.playBtn, R.id.helpBtn, R.id.exitBtn, R.id.highestScoreBtn, R.id.settingsBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playBtn:
                startGame();
                break;
            case R.id.helpBtn:
                startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                break;
            case R.id.highestScoreBtn:
                showHighestScore();
                break;
            case R.id.settingsBtn:
                settingsActivity();
                break;
            case R.id.exitBtn:
                finish();
                break;
        }
    }

    private void startGame() {
        intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.putExtra(getString(R.string.localUser), localUser);
        startActivityForResult(intent, GAME_REQUEST_CODE);
    }

    private void showHighestScore() {
        intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra(getString(R.string.localUser), localUser);
        startActivity(intent);
    }

    private void settingsActivity() {
        intent = new Intent(getApplicationContext(), SettingsActivity.class);
        intent.putExtra(getString(R.string.localUser), localUser);
        startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == GAME_REQUEST_CODE) {
                gameOverTitle.setVisibility(View.VISIBLE);
                SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0);
                if (localUser.getScore() < pref.getInt(getString(R.string.highestScore), 0)) {
                    localUser.setScore(pref.getInt(getString(R.string.highestScore), 0));
                    highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, localUser.getScore())));
                }
            }

            if (requestCode == SETTINGS_REQUEST_CODE) {
                localUser = (User) Objects.requireNonNull(data.getExtras()).get(getString(R.string.localUser));
            }

            if (requestCode == POP_UP_NAME_REQUEST_CODE) {
                localUser.setName(data.getStringExtra("name"));
            }

            if (user != null) {
                handleButtons(false);
                setUserDB();
            }
        }
    }

    private void handleButtons(boolean bool) {
        play.setEnabled(bool);
        help.setEnabled(bool);
        highestScore.setEnabled(bool);
        settings.setEnabled(bool);
    }
}