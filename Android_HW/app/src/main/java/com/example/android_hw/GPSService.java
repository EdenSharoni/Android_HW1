package com.example.android_hw;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSService {

    private static final String TAG = GPSService.class.getSimpleName();
    private static final int REQUEST_GPS = 1;
    private LocationManager locationManager;
    private List<Address> addresses;
    private Location myLocation;

    public GPSService(MainActivity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
        } else {
            getLocation(context);
        }
    }

    public void getLocation(MainActivity context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        myLocation = getLastKnownLocation(context);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps(context);
        }

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        addresses = null;
        try {
            if (myLocation != null) {
                addresses = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
            } else {
                Log.e(TAG, "MyLocation is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses == null) {
            Log.e(TAG, "Waiting for Location");
        } else {
            if (addresses.size() > 0) {
                Log.e(TAG, addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
            }
        }
    }

    private Location getLastKnownLocation(MainActivity context) {
        locationManager = (LocationManager) context.getApplicationContext().getSystemService(context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        Log.e(TAG, "Best Location: " + bestLocation);
        return bestLocation;
    }

    private void buildAlertMessageNoGps(MainActivity context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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

    public Location getMyLocation() {
        return myLocation;
    }
}
