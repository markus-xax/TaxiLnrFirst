package com.example.taxi_full.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.taxi_full.API.HashGenerator;
import com.example.taxi_full.R;

public class Driver_ClientActivity extends AppCompatActivity {

    DBHelper dbHelper;
    LocationManager locationManager;
    private boolean GPS_STATUS;
    private static final int REQUEST_CODE_PERMISSION_INTERNET = 1;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_byer);
        requestLocationPermission();

        dbHelper = new DBHelper(this);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_USER_VALUES, null, null, null, null, null, null);

        if (!cursor.moveToFirst()){
            ContentValues contentValues = new ContentValues();

            contentValues.put(DBHelper.KEY_DC, 0);
            contentValues.put(DBHelper.KEY_TOKEN, HashGenerator.hash());
            contentValues.put(DBHelper.KEY_ACTIVE, 0);
            database.insert(DBHelper.TABLE_USER_VALUES, null, contentValues);

            contentValues.put(DBHelper.KEY_DC, 1);
            contentValues.put(DBHelper.KEY_TOKEN, HashGenerator.hash());
            contentValues.put(DBHelper.KEY_ACTIVE, 0);
            database.insert(DBHelper.TABLE_USER_VALUES, null, contentValues);

            cursor.close();
            database.close();
        }

        GPSStatus();

        if(GPS_STATUS == true)
        {
            runOnUiThread(()-> Toast.makeText(this, "Геолокация включена", Toast.LENGTH_LONG));
        } else {
            runOnUiThread(()-> Toast.makeText(this, "Геолокация включена", Toast.LENGTH_LONG));
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void driver(View view){
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(DBHelper.TABLE_USER_VALUES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int dcIndex = cursor.getColumnIndex(DBHelper.KEY_DC);
            do {
                int dc = cursor.getInt(dcIndex);
                if(dc == 1){
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DBHelper.KEY_ACTIVE, 1);
                    database.update(DBHelper.TABLE_USER_VALUES, contentValues,DBHelper.KEY_DC+" = ?", new String[]{"1"});

                    contentValues.put(DBHelper.KEY_ACTIVE, 0);
                    database.update(DBHelper.TABLE_USER_VALUES, contentValues,DBHelper.KEY_DC+" = ?", new String[]{"0"});

                    startActivity(new Intent("com.example.taxi_full.Login"));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

    }

    public void client(View view){
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(DBHelper.TABLE_USER_VALUES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int dcIndex = cursor.getColumnIndex(DBHelper.KEY_DC);
            do {
                int dc = cursor.getInt(dcIndex);
                if(dc == 0){
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DBHelper.KEY_ACTIVE, 1);
                    database.update(DBHelper.TABLE_USER_VALUES, contentValues,DBHelper.KEY_DC+" = ?", new String[]{"0"});

                    contentValues.put(DBHelper.KEY_ACTIVE, 0);
                    database.update(DBHelper.TABLE_USER_VALUES, contentValues,DBHelper.KEY_DC+" = ?", new String[]{"1"});

                    startActivity(new Intent("com.example.taxi_full.Login"));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
    }

    public void GPSStatus() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        GPS_STATUS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    private void permissionInternet() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.INTERNET") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.INTERNET"}, REQUEST_CODE_PERMISSION_INTERNET);
        }
    }

}
