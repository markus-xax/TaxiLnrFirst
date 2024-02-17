package com.example.taxi_full.view.home.user.core.assign;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taxi_full.API.env.StyleCard;
import com.example.taxi_full.R;
import com.example.taxi_full.view.home.user.HomeActivity;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;

public class AssignVar implements Assign {

    @Override
    public void init(HomeActivity activity,
                     MapView mapView, UserLocationLayer userLocationLayer,
                     MapObjectCollection mapObjects, DrivingRouter drivingRouter, final InputListener il,
                     EditText start, EditText finish,
                     TextView eco, TextView middle, TextView business,
                     ImageView dollar_eco, ImageView dollar_middle, ImageView dollar_business,
                     Button typeNal, Button typeOffNal) {

        start = activity.findViewById(R.id.start_bottom);
        finish = activity.findViewById(R.id.finish_bottom);

        eco = activity.findViewById(R.id.eco);
        middle = activity.findViewById(R.id.middle);
        business = activity.findViewById(R.id.business);
        dollar_eco = activity.findViewById(R.id.dollar_eco);
        dollar_middle = activity.findViewById(R.id.dollar_middle);
        dollar_business = activity.findViewById(R.id.dollar_business);

        typeNal = activity.findViewById(R.id.type_home_nal);
        typeOffNal = activity.findViewById(R.id.type_home_offnal);

        mapView = activity.findViewById(R.id.mapview);
        mapView.getMap().setRotateGesturesEnabled(true);
        mapView.getMap().move(new CameraPosition(new Point(0, 0), 16, 0, 0));

        StyleCard.setMapStyle(mapView);

        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);

        userLocationLayer.setObjectListener(activity);

        mapView.getMap().addInputListener(il);
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
    }
}
