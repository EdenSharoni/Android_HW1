package com.example.android_hw;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private User localUser;
    private List<Address> addresses;
    private String address;
    private Geocoder geocoder;
    private LatLng latLng;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            localUser = (User) bundle.get(getString(R.string.localUser));
            location = (Location) bundle.get(getString(R.string.location));
        } else {
            localUser = new User();
            localUser.setLatitude(0);
            localUser.setLongitude(0);
        }
        if ((localUser.getLatitude() == 0 && localUser.getLongitude() == 0) || location == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.could_not_find_gps_location), Toast.LENGTH_LONG).show();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = null;
        mMap = googleMap;
        try {
            setGoogleMaps(localUser);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.failed_to_build_map), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void setGoogleMaps(User user) throws IOException {
        addresses = geocoder.getFromLocation(user.getLatitude(), user.getLongitude(), 1);
        if (addresses == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.waiting_for_location), Toast.LENGTH_LONG).show();
        } else if (addresses.size() > 0) {
            address = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
            Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
        }
        latLng = new LatLng(user.getLatitude(), user.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(getString(R.string.localUser), localUser);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
