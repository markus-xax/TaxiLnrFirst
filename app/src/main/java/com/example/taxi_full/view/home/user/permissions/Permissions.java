package com.example.taxi_full.view.home.user.permissions;

import android.content.Context;

import com.example.taxi_full.view.home.user.HomeActivity;

public interface Permissions {

    int REQUEST_CODE_PERMISSION_INTERNET = 1;

    int PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    /**
     * @param context
     * @param activity Проверяет на наличие разрешений на интернет
     *                 Если их нет - запрашивает
     */
    void permissionInternet(Context context, HomeActivity activity);

    /**
     * @param context
     * @param activity Проверяет на наличие разрешений на геолокацию
     *                 Если их нет - запрашивает
     */
    void requestLocationPermission(Context context, HomeActivity activity);

    void start(Context context, HomeActivity activity);
}
