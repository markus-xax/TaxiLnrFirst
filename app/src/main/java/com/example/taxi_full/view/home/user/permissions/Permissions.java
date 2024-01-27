package com.example.taxi_full.view.home.user.permissions;

import android.app.Activity;
import android.content.Context;

public interface Permissions {

    static final int REQUEST_CODE_PERMISSION_INTERNET = 1;

    static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    /**
     * @param context
     * @param activity
     *
     * Проверяет на наличие разрешений на интернет
     * Если их нет - запрашивает
     */
    void permissionInternet(Context context, Activity activity);

    /**
     * @param context
     * @param activity
     *
     * Проверяет на наличие разрешений на геолокацию
     * Если их нет - запрашивает
     */
    void requestLocationPermission(Context context, Activity activity);
}
