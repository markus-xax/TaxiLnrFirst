package com.example.taxi_full.view;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class TaxiApp extends Application {
    private static final String MAPKIT_API_KEY = "770c81aa-812b-47b8-a909-2ab1f69a500f";
    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
    }
}
