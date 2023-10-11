package com.example.taxi_full;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.TCPSocket;
import com.example.taxi_full.API.model.RootCars;
import com.example.taxi_full.API.model.RootGeolocationRoom;
import com.example.taxi_full.API.model.RootNotifications;
import com.example.taxi_full.API.model.RootOrderOne;
import com.google.gson.Gson;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GoActivityUser extends AppCompatActivity implements UserLocationObjectListener, DrivingSession.DrivingRouteListener {

    private WebSocketClient mWebSocketClient;
    private WebSocketClient mWebSocketClientNotifications;
    //private final TCPSocket tcpSocket = new TCPSocket();
    private String hash;
    private final DBClass dbClass = new DBClass();
    private final String URL_API_ORDERS_TREE = "http://45.86.47.12/api/ordersThree";
    private final String URL_API_USERS = "http://45.86.47.12/api/users";
    private RootOrderOne r;
    private UserLocationLayer userLocationLayer;
    private MapView mapView;
    private HashMap<String, Object> geolocation = new HashMap<>();
    private HashMap<String, Object> user = new HashMap<>();
    private Point ROUTE_START_LOCATION = null;
    private Point ROUTE_END_LOCATION = null;
    private DrivingRouter drivingRouter;
    private MapObjectCollection mapObjects;
    private DrivingSession drivingSession;
    private static final int REQUEST_CODE_PERMISSION_INTERNET = 1;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private Point driver = new Point();
    private PlacemarkMapObject placemarkMapObject;
    private final String URL_API = "http://45.86.47.12/api/orders";
    private String DistanceRoute, TimeRoute;
    private TextView time, time2, price;
    private final String URL_CARS = "http://45.86.47.12/api/cars";
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private DBClass DBClass = new DBClass();
    private boolean isPong;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        DirectionsFactory.initialize(this);

        peremissionInternet();
        requestLocationPermission();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.go_activity_user);

        connectToSocket();
        connectToSocketNotifications();
        cancel();

        mapView = findViewById(R.id.mapviewUserGo);
        mapView.getMap().setRotateGesturesEnabled(true);
        mapView.getMap().move(new CameraPosition(new Point(0, 0), 14, 0, 0));
        MapKit mapKit = MapKitFactory.getInstance();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        TextView start = findViewById(R.id.start);
        TextView finish = findViewById(R.id.finish);
        time = findViewById(R.id.time);
        time2 = findViewById(R.id.time2);
        price = findViewById(R.id.price);
        TextView name1 = findViewById(R.id.name1);
        TextView name = findViewById(R.id.name);
        ImageButton msg = findViewById(R.id.msg);
        TextView mark = findViewById(R.id.mark);
        TextView mark2 = findViewById(R.id.mark2);
        TextView number = findViewById(R.id.numbers);

        msg.setOnClickListener(view -> startActivity(new Intent("com.example.taxi_full.Chat")));

        try {
            hash = dbClass.getHash(this);
        } catch (Exception e){
            Log.d("hash", e.getMessage());
        }

//        new Thread(() -> { try { tcpSocket.TCP_Conn(19800);
//            try {
//                tcpSocket.send(new JSONObject().put("ping", "ping"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }} catch (IOException e) { e.printStackTrace();} }).start();


        new Thread(()->{
            try {
                String hash = dbClass.getHash(GoActivityUser.this);
                String url = URL_API +"/"+hash;
                RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(url), RootOrderOne.class);
                RootCars rootCars = new Gson().fromJson(HttpApi.getId(URL_CARS+"/"+rootOrderOne.getHash_driver()), RootCars.class);
              runOnUiThread(()->{
                if(rootOrderOne.getStart_string() != null)
                    start.setText(rootOrderOne.getStart_string());
                if(rootOrderOne.getFinish_string() != null)
                    finish.setText(rootOrderOne.getFinish_string());
                name.setText(rootOrderOne.getNameDriver());
                name1.setText(rootOrderOne.getNameDriver());
                price.setText(rootOrderOne.getPrice());
                if(rootCars.getModel() != null && rootCars.getNumber() != null) {
                    mark.setText(rootCars.getModel());
                    mark2.setText(rootCars.getModel());
                    number.setText(rootCars.getNumber());
                }
              });
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();

        submitRequest();

        RedirectToHome();

    }

    private void cancel(){
        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> {
            DBClass = new DBClass();
            String hash = DBClass.getHash(this);
            String url = URL_API_USERS +"/"+hash;
            String arg = "active=4";
            new Thread(()->{
                if(HttpApi.put(url, arg) == HttpURLConnection.HTTP_OK) {
                    try {
                        String urlO = URL_API +"/"+hash;
                        RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(urlO), RootOrderOne.class);
                        if(rootOrderOne.getType_pay().equals("2")){
                            startActivity(new Intent("com.example.taxi_full.PayOffNal"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent("com.example.taxi_full.RequestUser"));
                    //вывод всплыувуающего окна
                }
            }).start();
        });
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        new Thread(() -> {
//            if(!tcpSocket.is_close()) {
//                try {
//                    tcpSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        if(mWebSocketClient.getConnection().isOpen())
            mWebSocketClient.close();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
        connectToSocket();
        pingPong("ping");
    }

    private void connectToSocket() {
        URI uri;
        try {
            uri = new URI("ws"+"://"+"45.86.47.12:28720");
        } catch (URISyntaxException e) {
            Log.d("----uri------",e.getMessage());
            return;
        }
        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            private int count = 0;
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("Websocket", "Opened");
                mWebSocketClient.send("{\"newUser\" : \""+ DBClass.getHash(getBaseContext()) +"\"}");
            }
            @Override
            public void onMessage(String s) {
                if(s.equals("pong")){
                    isPong = true;
                } else {
                    RootGeolocationRoom geo = new Gson().fromJson(s, RootGeolocationRoom.class);
                    if (geolocation != null && !geolocation.isEmpty()) {
                        geolocation.clear();
                    }
                    geolocation.put("Longitude", geo.getLongitude());
                    geolocation.put("Latitude", geo.getLatitude());
                    driver = new Point(Double.parseDouble((String) geolocation.get("Latitude")), Double.parseDouble((String) geolocation.get("Longitude")));
                    if (count == 0) {
                        runOnUiThread(() -> {
                            placemarkMapObject = mapObjects.addPlacemark(driver,
                                    ImageProvider.fromResource(GoActivityUser.this, R.drawable.car_driver));
                            count++;
                        });
                    } else {
                        runOnUiThread(() -> {
                            placemarkMapObject.setGeometry(driver);
                        });
                    }
                }
            }
            @Override
            public void onClose(int i, String s, boolean b) {
                Log.d("Websocket", "Closed " + s);
            }
            @Override
            public void onError(Exception e) {
                Log.d("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    private void connectToSocketNotifications() {
        URI uri;
        try {
            uri = new URI("ws"+"://"+"45.86.47.12:27888");
        } catch (URISyntaxException e) {
            Log.d("----uri------",e.getMessage());
            return;
        }
        mWebSocketClientNotifications = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("Websocket", "Opened");
            }
            @Override
            public void onMessage(String s) {
                DBClass db = new DBClass();
                Log.d("stringS", s);
                RootNotifications rootNotifications = new Gson().fromJson(s, RootNotifications.class);
                //вывести уведомление
                if (rootNotifications.getHash().equals(db.getHash(GoActivityUser.this))) {
                    runOnUiThread(() -> {
                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(GoActivityUser.this)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle("Taxi")
                                        .setContentText(rootNotifications.getNotifications());

                        Notification notification = builder.build();

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(1, notification);
                    });
                }
            }
            @Override
            public void onClose(int i, String s, boolean b) {
                Log.d("Websocket", "Closed " + s);
            }
            @Override
            public void onError(Exception e) {
                Log.d("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClientNotifications.connect();
    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {
        userLocationLayer.setAnchor(
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83)));

        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                this, R.drawable.user_location));

        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();

        pinIcon.setIcon(
                "icon",
                ImageProvider.fromResource(this, R.drawable.user_location),
                new IconStyle().setAnchor(new PointF(0f, 0f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(0f)
                        .setScale(1f)
        );

        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);

        new Thread(() -> {
            try {
                r = new Gson().fromJson(HttpApi.getId(URL_API_ORDERS_TREE+"/"+hash), RootOrderOne.class);
                JSONObject jsonGeometry = new JSONObject();
                runOnUiThread(() -> {
                    try {
                        jsonGeometry.put("Longitude", userLocationView.getArrow().getGeometry().getLongitude());
                        jsonGeometry.put("Latitude", userLocationView.getArrow().getGeometry().getLatitude());
                        jsonGeometry.put("user", r.getHash_driver());
                        //new Thread(()->{tcpSocket.send(jsonGeometry);}).start();
                        mWebSocketClient.send(jsonGeometry.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(UserLocationView userLocationView, ObjectEvent objectEvent) {
        new Thread(() -> {
            try {
                r = new Gson().fromJson(HttpApi.getId(URL_API_ORDERS_TREE+"/"+hash), RootOrderOne.class);
                JSONObject jsonGeometry = new JSONObject();
                runOnUiThread(() -> {
                    try {
                        jsonGeometry.put("Longitude", userLocationView.getArrow().getGeometry().getLongitude());
                        jsonGeometry.put("Latitude", userLocationView.getArrow().getGeometry().getLatitude());
                        jsonGeometry.put("user", r.getHash_driver());
                        mWebSocketClient.send(jsonGeometry.toString());
                        //new Thread(()->{tcpSocket.send(jsonGeometry);}).start();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void submitRequest() {
        DrivingOptions drivingOptions = new DrivingOptions();
        VehicleOptions vehicleOptions = new VehicleOptions();
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        new Thread(() -> {
            try{
                RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(URL_API_ORDERS_TREE+"/"+hash), RootOrderOne.class);
                ArrayList<String> data = new ArrayList<>(Arrays.asList(rootOrderOne.getStart().split(",")));
                ArrayList<String> dataEnd = new ArrayList<>(Arrays.asList(rootOrderOne.getFinish().split(",")));
                ROUTE_START_LOCATION = new Point(Double.parseDouble(data.get(1)), Double.parseDouble(data.get(0)));
                ROUTE_END_LOCATION = new Point(Double.parseDouble(dataEnd.get(1)), Double.parseDouble(dataEnd.get(0)));
                runOnUiThread(() -> {
                    requestPoints.add(new RequestPoint(
                            ROUTE_START_LOCATION,
                            RequestPointType.WAYPOINT,
                            null));
                    requestPoints.add(new RequestPoint(
                            ROUTE_END_LOCATION,
                            RequestPointType.WAYPOINT,
                            null));
                    mapObjects.addPlacemark(ROUTE_START_LOCATION,
                            ImageProvider.fromResource(GoActivityUser.this, R.drawable.pin)).setDraggable(true);
                    mapObjects.addPlacemark(ROUTE_END_LOCATION,
                            ImageProvider.fromResource(GoActivityUser.this, R.drawable.finish)).setDraggable(true);
                    drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, GoActivityUser.this);
                });
            } catch (Exception e){
                Log.d("Ex parse", e.getMessage());
            }
        }).start();
    }

    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> list) {
        double smallRouteDistance = 0;
        Polyline smallRoute = new Polyline();
        for (DrivingRoute route : list) {
            if(smallRouteDistance == 0) {
                smallRouteDistance = route.getMetadata().getWeight().getDistance().getValue();
                smallRoute = route.getGeometry();
                DistanceRoute = route.getMetadata().getWeight().getDistance().getText();
                TimeRoute = route.getMetadata().getWeight().getTime().getText();
            } else {
                if (smallRouteDistance > route.getMetadata().getWeight().getDistance().getValue()) {
                    smallRouteDistance = route.getMetadata().getWeight().getDistance().getValue();
                    smallRoute = route.getGeometry();
                    DistanceRoute = route.getMetadata().getWeight().getDistance().getText();
                    TimeRoute = route.getMetadata().getWeight().getTime().getText();
                }
            }
        }
        mapObjects.addPolyline(smallRoute);

        time.setText(TimeRoute);
        time2.setText(TimeRoute);
    }

    @Override
    public void onDrivingRoutesError(@NonNull Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }

    private void peremissionInternet(){
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.INTERNET")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.INTERNET"},
                    REQUEST_CODE_PERMISSION_INTERNET);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void RedirectToHome() {
        DBClass db = new DBClass();
        String url_order = URL_API + "/" + db.getHash(this);
        Runnable geoListener = () -> {
            try {
                RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(url_order), RootOrderOne.class);
                if(rootOrderOne.getActive().equals("0") || rootOrderOne.getActive().equals("4")) {
                    if(rootOrderOne.getType_pay().equals("2")){
                        startActivity(new Intent("com.example.taxi_full.PayOffNal"));
                        executor.shutdown();
                        return;
                    }
                    startActivity(new Intent("com.example.taxi_full.RequestUser"));
                    executor.shutdown();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };


        executor.scheduleAtFixedRate(geoListener, 0, 5, TimeUnit.SECONDS);
    }
    private void pingPong(String hash) {
        new Thread(()->{
            while(true){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(mWebSocketClient != null) {
                    if (mWebSocketClient.getConnection().isOpen())
                        mWebSocketClient.send("{\"ping\" : \"" + hash + "\"}");
                }
                isPong = false;
                try {
                    Thread.sleep(2000);
                    if(!isPong) {
                        if(mWebSocketClient.getConnection().isOpen())
                            mWebSocketClient.close();
                        connectToSocket();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
