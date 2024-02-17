package com.example.taxi_full.view.home.user.core.maps;

import android.util.Log;

import com.example.taxi_full.view.home.user.HomeActivity;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;

import java.util.ArrayList;

public class YMaps implements Maps {

    @Override
    public void submitRequest(HomeActivity activity,
                              Point ROUTE_START_LOCATION, Point ROUTE_END_LOCATION,
                              DrivingRouter drivingRouter) {
        DrivingOptions drivingOptions = new DrivingOptions();
        VehicleOptions vehicleOptions = new VehicleOptions();
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        try {
            activity.runOnUiThread(() -> {
                if (ROUTE_START_LOCATION != null && ROUTE_END_LOCATION != null) {
                    requestPoints.add(new RequestPoint(ROUTE_START_LOCATION, RequestPointType.WAYPOINT, null));
                    requestPoints.add(new RequestPoint(ROUTE_END_LOCATION, RequestPointType.WAYPOINT, null));
                }
                drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, activity);
            });
        } catch (Exception e) {
            Log.d("Ex parse", e.getMessage());
        }
    }

}
