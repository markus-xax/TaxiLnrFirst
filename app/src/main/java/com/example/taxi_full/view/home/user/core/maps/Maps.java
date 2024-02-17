package com.example.taxi_full.view.home.user.core.maps;

import com.example.taxi_full.view.home.user.HomeActivity;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.geometry.Point;

public interface Maps {

    void submitRequest(HomeActivity activity,
                       Point ROUTE_START_LOCATION, Point ROUTE_END_LOCATION,
                       DrivingRouter drivingRouter);
}
