package com.example.taxi_full.view;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class TaxiApp extends Application {
    private static final String MAPKIT_API_KEY = "3b4ac084-8b03-4fef-b944-152dbc8c7f39";
    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
    }
}
