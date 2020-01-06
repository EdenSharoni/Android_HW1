package com.example.android_hw;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            localUser = (User) bundle.get(getString(R.string.localUser));
        } else {
            localUser = new User();
            localUser.setLatitude(0);
            localUser.setLongitude(0);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        else{
            Log.e(TAG, "mapFragment is null");
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
            Toast.makeText(getApplicationContext(), "Failed to build map", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void setGoogleMaps(User user) throws IOException {
        addresses = geocoder.getFromLocation(user.getLatitude(), user.getLongitude(), 1);
        if (addresses == null) {
            Log.e(TAG, "Waiting for Location");
        } else if (addresses.size() > 0) {
            address = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
            Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
        }
        latLng = new LatLng(user.getLatitude(), user.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
    }
}
