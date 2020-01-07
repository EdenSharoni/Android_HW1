package com.example.android_hw;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

class AlertDialogs {
    private Context context;

    AlertDialogs(Context context) {
        this.context = context;
    }

    void networkConnectionDialog(Service service) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.no_network))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
                    if (service.isNetworkConnected()) {
                        dialog.dismiss();
                    } else {
                        ((MainActivity) context).finish();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    void GPSProviderDialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.enable_gps_dialog))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes), (dialog, id) -> context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(context.getString(R.string.no), (dialog, id) -> dialog.cancel());
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }
}
