package com.example.taxi_full.API;

import android.content.Context;

import com.yandex.mapkit.MapKitFactory;

import org.jetbrains.annotations.NotNull;

public class MapKitInit {
    private boolean init = false;

    public void init(@NotNull String apiKey, @NotNull Context context){
        if (init) {
            return;
        }
            MapKitFactory.setApiKey(apiKey);
            MapKitFactory.initialize(context);
            init = true;
    }
}
