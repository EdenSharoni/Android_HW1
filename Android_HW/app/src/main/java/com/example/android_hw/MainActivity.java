package com.example.android_hw;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.location.LocationManager.NETWORK_PROVIDER;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_GPS = 1;
    private static final int GAME_REQUEST_CODE = 1;
    private static final int SETTINGS_REQUEST_CODE = 2;
    private static final int POP_UP_NAME_REQUEST_CODE = 3;
    private static final int REQUEST_HIGHEST_SCORE = 4;

    private FirebaseUser user;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private User localUser;
    private Intent intent;
    private Location myLocation;
    private LocationManager locationManager;
    private boolean initUserOnCreate = true;
    private Service service;
    private AlertDialogs alertDialog;

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
    @BindView(R.id.exitBtn)
    ImageView exit;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        handleButtons(false);
        service = new Service(this);
        alertDialog = new AlertDialogs(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    return;
                locationManager.requestLocationUpdates(NETWORK_PROVIDER, 0, 0, this);
                findUser();
            } else {
                finish();
            }
        }
    }

    private void findUser() {
        if (user == null) {
            newUser();
        } else {
            getUserFromDB();
        }
    }

    private void newUser() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        if (myLocation == null) {
                            if (user != null) {
                                localUser = new User(user.getUid(), "", 0, true, 80, getString(R.string.screen), 0, 0);
                            }
                        } else {
                            if (user != null) {
                                localUser = new User(user.getUid(), "", 0, true, 80, getString(R.string.screen), myLocation.getLatitude(), myLocation.getLongitude());
                            }
                        }
                        highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, localUser.getScore())));
                        setUserDB();
                    }
                });
    }

    public void setUserDB() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection(getString(R.string.users))
                .document(user.getUid()).set(localUser).addOnSuccessListener(aVoid -> {
            progressBar.setVisibility(View.GONE);
            checkNameValid();
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Log.e(TAG, getString(R.string.error_saving_data));
        });
    }

    private void checkNameValid() {
        if (localUser.getName().isEmpty()) {
            intent = new Intent(getApplicationContext(), PopUpNameActivity.class);
            intent.putExtra(getString(R.string.localUser), localUser);
            startActivityForResult(intent, POP_UP_NAME_REQUEST_CODE);
        } else {
            handleButtons(true);
        }
    }

    private void getUserFromDB() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection(getString(R.string.users))
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                            progressBar.setVisibility(View.GONE);
                            localUser = documentSnapshot.toObject(User.class);
                            if (localUser == null) {
                                if (myLocation == null) {
                                    localUser = new User(user.getUid(), user.getDisplayName(), 0, true, 80, getString(R.string.screen), 0, 0);
                                } else {
                                    localUser = new User(user.getUid(), user.getDisplayName(), 0, true, 80, getString(R.string.screen), myLocation.getLatitude(), myLocation.getLongitude());
                                }
                                setUserDB();
                            } else {
                                if (myLocation != null) {
                                    localUser.setLatitude(myLocation.getLatitude());
                                    localUser.setLongitude((myLocation.getLongitude()));
                                }
                                checkNameValid();
                            }
                            highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, localUser.getScore())));
                        }
                ).addOnFailureListener(e -> progressBar.setVisibility(View.GONE));
    }

    @Override
    @OnClick({R.id.playBtn, R.id.helpBtn, R.id.exitBtn, R.id.highestScoreBtn, R.id.settingsBtn})
    public void onClick(View v) {
        handleButtons(false);
        switch (v.getId()) {
            case R.id.playBtn:
                startGame();
                break;
            case R.id.helpBtn:
                handleButtons(true);
                startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                break;
            case R.id.highestScoreBtn:
                showHighestScore();
                break;
            case R.id.settingsBtn:
                settingsActivity();
                break;
            case R.id.exitBtn:
                setUserDB();
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
        intent.putExtra(getString(R.string.location), myLocation);
        handleButtons(true);
        startActivityForResult(this.intent, REQUEST_HIGHEST_SCORE);
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
            if (requestCode == GAME_REQUEST_CODE || requestCode == SETTINGS_REQUEST_CODE || requestCode == POP_UP_NAME_REQUEST_CODE || requestCode == REQUEST_HIGHEST_SCORE) {
                localUser = (User) Objects.requireNonNull(data.getExtras()).get(getString(R.string.localUser));
                if (requestCode == GAME_REQUEST_CODE) {
                    gameOverTitle.setVisibility(View.VISIBLE);
                    highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, localUser.getScore())));
                }
            }
            if (user != null) {
                handleButtons(false);
                setUserDB();
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        if (localUser != null) {
            localUser.setLongitude(longitude);
            localUser.setLatitude(latitude);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        alertDialog.GPSProviderDialog();
    }

    private void handleButtons(boolean bool) {
        play.setEnabled(bool);
        help.setEnabled(bool);
        highestScore.setEnabled(bool);
        settings.setEnabled(bool);

        if (!bool) {
            setButtonAlpha(0.8f);
        } else {
            setButtonAlpha(1f);
        }
    }

    private void setButtonAlpha(float num) {
        play.setAlpha(num);
        help.setAlpha(num);
        highestScore.setAlpha(num);
        settings.setAlpha(num);
        exit.setAlpha(num);
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(this);
        setUserDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!service.isNetworkConnected()) {
            alertDialog.networkConnectionDialog(service);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
        } else {
            if (initUserOnCreate) {
                initUserOnCreate = false;
                locationManager.requestLocationUpdates(NETWORK_PROVIDER, 0, 0, this);
                findUser();
            } else {
                locationManager.requestLocationUpdates(NETWORK_PROVIDER, 0, 0, this);
            }
        }
    }
}