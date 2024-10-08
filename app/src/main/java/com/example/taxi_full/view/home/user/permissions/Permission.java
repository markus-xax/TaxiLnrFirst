package com.example.taxi_full.view.home.user.permissions;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.taxi_full.view.home.user.HomeActivity;

public class Permission implements Permissions{

    @Override
    public void permissionInternet(Context context, HomeActivity activity) {
        if (ContextCompat.checkSelfPermission(context, "android.permission.INTERNET") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{"android.permission.INTERNET"}, REQUEST_CODE_PERMISSION_INTERNET);
        }
    }

    @Override
    public void requestLocationPermission(Context context, HomeActivity activity) {
        if (ContextCompat.checkSelfPermission(context, "android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void start(Context context, HomeActivity activity) {
        permissionInternet(context, activity);
        requestLocationPermission(context, activity);
    }


}
