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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_GPS = 1;
    private final int RC_SIGN_IN = 3;
    private final String TAG = "MainActivity";
    private SharedPreferences pref;
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
        gpsService = new GPSService(this);

        if (user == null) {
            Log.e(TAG, "user is null");
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                user = mAuth.getCurrentUser();
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    localUser = new User(user.getUid(), "", 0, true, 80, getString(R.string.screen), gpsService.getMyLocation().getLatitude(), gpsService.getMyLocation().getLongitude());
                                    setUserDB();
                                }
                            }
                        }
                    });
        } else {
            Log.e(TAG, "user is not null");
            getUserFromDB();
        }

        /*set highest score to 0
        getApplicationContext().getSharedPreferences(getString(R.string.MyPref), 0).edit().putInt("highestScore", 0).apply();*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "PERMISSION GRANTED");
                gpsService.getLocation(this);
                localUser = new User(user.getUid(), "", 0, true, 80, getString(R.string.screen), gpsService.getMyLocation().getLatitude(), gpsService.getMyLocation().getLongitude());
                setUserDB();
            } else {
                Log.e(TAG, "PERMISSION GRANTED");
            }
        }
    }

    @Override
    @OnClick({R.id.playBtn, R.id.helpBtn, R.id.exitBtn, R.id.highestScoreBtn, R.id.settingsBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playBtn:
                startGame();
                break;
            case R.id.helpBtn:
                showHelp();
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

    private void showHelp() {
        intent = new Intent(getApplicationContext(), HelpActivity.class);
        startActivity(intent);
    }

    private void showHighestScore() {
        intent = new Intent(getApplicationContext(), MapsActivity.class);
        if (gpsService.getMyLocation() != null) {
            Log.e(TAG, "onClick: getMyLocation is not null");
            gpsService.getLocation(MainActivity.this);
            localUser.setLatitude(gpsService.getMyLocation().getLatitude());
            localUser.setLongitude(gpsService.getMyLocation().getLongitude());
            setUserDB();
        }
        intent.putExtra(getString(R.string.localUser), localUser);
        startActivityForResult(intent, 5);
    }

    private void settingsActivity() {
        intent = new Intent(getApplicationContext(), SettingsActivity.class);
        intent.putExtra(getString(R.string.localUser), localUser);
        startActivityForResult(intent, 2);
    }

    private void startGame() {
        intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.putExtra(getString(R.string.localUser), localUser);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
                if (user != null) {
                    handleButtons(false);
                    setUserDB();
                }
            }
        }

        //Name Text
        if (requestCode == 4) {
            if (resultCode == RESULT_OK) {
                localUser.setName(data.getStringExtra("name"));
                if (user != null)
                    setUserDB();
            }
        }

        if (requestCode == 5) {
            if (resultCode == RESULT_OK) {
                if (user != null)
                    setUserDB();
            }
        }
    }

    public void setUserDB() {
        db.collection("Users")
                .document(user.getUid())
                .set(localUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (localUser.getName().isEmpty()) {
                    startActivityForResult(new Intent(getApplicationContext(), PopUpNameActivity.class), 4);
                } else {
                    handleButtons(true);
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), getString(R.string.error_saving_data), Toast.LENGTH_LONG));
    }


    private void getUserFromDB() {
        Log.e(TAG, "getUserFromDB: ");
        db.collection("Users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                            localUser = documentSnapshot.toObject(User.class);
                            if (localUser == null) {
                                Log.e(TAG, "local User is null");
                                localUser = new User(user.getUid(), user.getDisplayName(), 0, true, 80, getString(R.string.screen), gpsService.getMyLocation().getLatitude(), gpsService.getMyLocation().getLongitude());
                                setUserDB();
                            } else {
                                if (localUser.getName().isEmpty()) {
                                    startActivityForResult(new Intent(getApplicationContext(), PopUpNameActivity.class), 4);
                                } else {
                                    handleButtons(true);
                                }
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