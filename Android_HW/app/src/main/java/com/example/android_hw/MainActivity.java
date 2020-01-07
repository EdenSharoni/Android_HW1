package com.example.android_hw;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import static android.location.LocationManager.GPS_PROVIDER;
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
    private User localUser;
    private Intent intent;
    private FirebaseAuth mAuth;
    private Location myLocation;
    private LocationManager locationManager;

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
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        handleButtons(false);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
        } else {
            locationManager.requestLocationUpdates(NETWORK_PROVIDER, 0, 0, this);
            findUser();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(NETWORK_PROVIDER, 0, 0, this);
                findUser();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissions[0]);
                if (!showRationale) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Changing Permission:\napp -> settings\ngrant there the permissions.")
                            .setCancelable(false)
                            .setNegativeButton("OK", (dialog, id) -> finish());
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Please Confirm Access to Location")
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS))
                            .setNegativeButton("No", (dialog, id) -> finish());
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }
    }

    private void findUser() {
        if (user == null) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            Log.e("GPS", "onComplete: ");
                            if (myLocation == null) {
                                localUser = new User(user.getUid(), "", 0, true, 80, getString(R.string.screen), 0, 0);
                            } else {
                                localUser = new User(user.getUid(), "", 0, true, 80, getString(R.string.screen), myLocation.getLatitude(), myLocation.getLongitude());
                            }
                            highestScoreText.setText(String.format("%s %s", getString(R.string.highest), getString(R.string.score, localUser.getScore())));
                            setUserDB();
                        }
                    });
        } else {
            getUserFromDB();
        }
    }

    public void setUserDB() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("Users")
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
        db.collection("Users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                            progressBar.setVisibility(View.GONE);
                            localUser = documentSnapshot.toObject(User.class);
                            if (localUser == null) {
                                Log.e("GPS", "getUserFromDB: ");
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
        Log.e(TAG, "onLocationChanged: ");
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
        Log.e(TAG, "onStatusChanged: ");
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void handleButtons(boolean bool) {
        play.setEnabled(bool);
        help.setEnabled(bool);
        highestScore.setEnabled(bool);
        settings.setEnabled(bool);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setUserDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isNetworkConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("No Network")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> {
                        if (isNetworkConnected()) {
                            dialog.dismiss();
                        } else {
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}