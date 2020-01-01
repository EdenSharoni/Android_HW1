package com.example.android_hw;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_GPS = 1;
    private final int RC_SIGN_IN = 3;
    private final String TAG = "MainActivity";
    private SharedPreferences pref;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private User localUser;
    private Intent intent;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private String address;
    private List<Address> addresses;
    private Location myLocation;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        db = FirebaseFirestore.getInstance();

        user = getInstance().getCurrentUser();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocation = getLastKnownLocation();

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        addresses = null;
        try {
            if (myLocation != null) {
                addresses = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses == null) {
            Log.e(TAG, "Waiting for Location");
        } else {
            if (addresses.size() > 0) {
                address = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                Log.e(TAG, addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
            }
        }

        if (user == null) {
            localUser = new User(null, null, 0, true, 80, getString(R.string.screen), addresses);
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

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void requestGPSPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "PERMISSION GRANTED");
            } else {
                Log.e(TAG, "PERMISSION GRANTED");
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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
                startGame();
                break;
            case R.id.googleBtn:
                signIn();
                break;
            case R.id.signOutBtn:
                signOut();
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
        HighestScoreFragment highestScoreFragment = new HighestScoreFragment();
        highestScoreFragment.show(getSupportFragmentManager(), TAG);
    }

    private void settingsActivity() {
        intent = new Intent(getApplicationContext(), SettingsActivity.class);
        intent.putExtra(getString(R.string.localUser), localUser);
        startActivityForResult(intent, 2);
    }

    private void startGame() {
        intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra(getString(R.string.localUser), localUser);
        startActivityForResult(intent, 1);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        googleSignIn.setVisibility(View.VISIBLE);
        googleSignOut.setVisibility(View.GONE);
        user = getInstance().getCurrentUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Google Request
        if (requestCode == RC_SIGN_IN) {
            user = getInstance().getCurrentUser();
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
    }

    public void setUserDB() {
        db.collection("Users")
                .document(user.getUid())
                .set(localUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                handleButtons(true);
            }
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), getString(R.string.error_saving_data), Toast.LENGTH_LONG));
    }


    private void getUserFromDB() {
        db.collection("Users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                            localUser = documentSnapshot.toObject(User.class);
                            if (localUser == null) {
                                localUser = new User(user.getUid(), user.getDisplayName(), 0, true, 80, getString(R.string.screen), addresses);
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