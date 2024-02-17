package com.example.taxi_full.view.home.user.core.assign;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taxi_full.view.home.user.HomeActivity;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;

public interface Assign {
    void init(HomeActivity activity,
              MapView mapView, UserLocationLayer userLocationLayer,
              MapObjectCollection mapObjects, DrivingRouter drivingRouter, final InputListener il,
              EditText start, EditText finish,
              TextView eco, TextView middle, TextView business,
              ImageView dollar_eco, ImageView dollar_middle, ImageView dollar_business,
              Button typeNal, Button typeOffNal);
}
