package com.example.taxi_full;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.MyLocationListener;
import com.example.taxi_full.API.StyleCard;
import com.example.taxi_full.API.TCPSocket;
import com.example.taxi_full.API.model.RootGeolocationRoom;
import com.example.taxi_full.API.model.RootOrderOne;
import com.example.taxi_full.API.model.RootUserOne;
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

public class GoActivityDriver extends AppCompatActivity implements UserLocationObjectListener, DrivingSession.DrivingRouteListener {

    private WebSocketClient mWebSocketClient;
    private WebSocketClient mWebSocketClientNotifications;
    private String hash;
    private DBClass dbClass = new DBClass();
    private final String URL_API_USERS = "http://45.86.47.12/api/users";
    private final String URL_API_ORDERS_TREE = "http://45.86.47.12/api/ordersThree";
    private RootOrderOne r;
    private UserLocationLayer userLocationLayer;
    private MapView mapView;
    private HashMap<String, Object> geolocation = new HashMap<>();
    private DBClass DBClass = new DBClass();
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;
    private MapObjectCollection mapObjects;
    private Point ROUTE_START_LOCATION = new Point();
    private Point ROUTE_END_LOCATION = new Point();
    private Point ROUTE_POINT_LOCATION = new Point();
    private Point user = new Point();
    private PlacemarkMapObject placemarkMapObject;
    private final String URL_API = "http://45.86.47.12/api/orders";
    private final String URL_API_TIME = "http://45.86.47.12/api/time";
    private String DistanceRoute,TimeRoute;
    private TextView time;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private boolean isPong, isPongNot;
    private boolean flagNew = false;
    private int c2 = Color.rgb(100, 200, 233);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        DirectionsFactory.initialize(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.go_activity_driver);

        connectToSocket();
        connectToSocketNotifications();

        mapView = findViewById(R.id.mapview);
        mapView.getMap().setRotateGesturesEnabled(false);
        mapView.getMap().move(new CameraPosition(new Point(0, 0), 19, 0, 0));
        MapKit mapKit = MapKitFactory.getInstance();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        StyleCard.setMapStyle(mapView);

        hash = dbClass.getHash(this);

//        ImageButton msg = findViewById(R.id.msgD);
//        msg.setOnClickListener(view -> startActivity(new Intent("com.example.taxi_full.Chat")));

        TextView cord = findViewById(R.id.text_bottom);
        time = findViewById(R.id.time);
        TextView price = findViewById(R.id.price);
        TextView distance = findViewById(R.id.distance);
//        new Thread(() -> {
//            try {
//                tcpSocket.TCP_Conn(19800);
//            } catch (IOException e) {Log.d("stringS", e.getMessage());}
//        }).start();

        new Thread(()->{
            try {
                String hash = DBClass.getHash(GoActivityDriver.this);
                String url = URL_API +"/"+hash;
                RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(url), RootOrderOne.class);
                runOnUiThread(()->{
                    if(rootOrderOne.getStart_string() != null)
                        cord.setText(rootOrderOne.getStart_string());
                    if(rootOrderOne.getPrice() != null && rootOrderOne.getDistance() != null){
                        price.setText(rootOrderOne.getPrice());
                        distance.setText(rootOrderOne.getDistance());
                    }

                });
                telephone(rootOrderOne);
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();

        Button land = findViewById(R.id.land);
        land.setOnClickListener(view -> {
            DBClass = new DBClass();
            String hash = DBClass.getHash(this);
            String url = URL_API_USERS +"/"+hash;
            String arg = "active=4";
            new Thread(()->{
                if(HttpApi.put(url, arg) == HttpURLConnection.HTTP_OK) {
                    flagNew = true;
                    startActivity(new Intent("com.example.taxi_full.RequestDriver"));
                    //вывод всплыувуающего окна
                }
            }).start();
        });

        Button place = findViewById(R.id.place);
        place.setOnClickListener(view -> {
            place.setVisibility(View.GONE);
            //Сенд месседжа юзеру
            String hash = dbClass.getHash(GoActivityDriver.this);
            String url = URL_API +"/"+hash;

                new Thread(()->{
                    try {
                    RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(url), RootOrderOne.class);
                    int inc = 0;
                    while(true) {
                        inc++;
                        if (mWebSocketClientNotifications.getConnection().isOpen()) {
                            send("Водитель приехал к месту посадки", rootOrderOne.getHash_user());
                            break;
                        }
                        sleep(1000);
                        if (inc>3)
                            break;
                    }
                    } catch (JSONException | IOException | InterruptedException e) {e.printStackTrace();}
                }).start();

            land.setVisibility(View.VISIBLE);
        });

        submitRequest();

        RedirectToHome();
    }

    @Override
    protected void onStop() {
        if(mWebSocketClient.getConnection().isOpen()) {
            mWebSocketClient.close();
        }
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mWebSocketClient.getConnection().isOpen())
            mWebSocketClient.close();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
        connectToSocket();
        submitRequest();
        pingPong("ping");
    }

    private void connectToSocket() {
        URI uri;
        try {
            DBClass db = new DBClass();
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
                Log.d("stringS", s);
                if(s.equals("pong")){
                    isPong = true;
                } else {
                    RootGeolocationRoom geo = new Gson().fromJson(s, RootGeolocationRoom.class);
                    if (geolocation != null && !geolocation.isEmpty()) {
                        geolocation.clear();
                    }
                    geolocation.put("Longitude", geo.getLongitude());
                    geolocation.put("Latitude", geo.getLatitude());
                    user = new Point(Double.parseDouble((String) geolocation.get("Latitude")), Double.parseDouble((String) geolocation.get("Longitude")));
                    if (count == 0) {
                        runOnUiThread(() -> {
                            placemarkMapObject = mapObjects.addPlacemark(user,
                                    ImageProvider.fromResource(GoActivityDriver.this, R.drawable.user_location));
                            count++;
                        });
                    } else {
                        runOnUiThread(() -> {
                            placemarkMapObject.setGeometry(user);
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
        DBClass db = new DBClass();
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
                mWebSocketClientNotifications.send("{\"newUser\" : \"" + db.getHash(getBaseContext()) + "\"}");
            }
            @Override
            public void onMessage(String s) {
                Log.d("stringS", s);
                if(s.equals("pong"))
                    isPongNot = true;
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

    private void send(String k, String v) throws JSONException {
        JSONObject j = new JSONObject();
        j.put("notifications", k);
        j.put("hash", v);
        mWebSocketClientNotifications.send(j.toString());
    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {

        userLocationLayer.setAnchor(
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83)));

        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();

        pinIcon.setIcon(
                "icon",
                ImageProvider.fromResource(this, R.drawable.user_location),
                new IconStyle().setAnchor(new PointF(0f, 0f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(0f)
                        .setScale(1f)
        );

        userLocationView.getAccuracyCircle().setFillColor(c2);

        new Thread(() -> {
            try {
                r = new Gson().fromJson(HttpApi.getId(URL_API_ORDERS_TREE+"/"+hash), RootOrderOne.class);
                JSONObject jsonGeometry = new JSONObject();
                runOnUiThread(() -> {
                    try {
                        jsonGeometry.put("Longitude", userLocationView.getArrow().getGeometry().getLongitude());
                        jsonGeometry.put("Latitude", userLocationView.getArrow().getGeometry().getLatitude());
                        jsonGeometry.put("user", r.getHash_user());
                        //new Thread(()->{tcpSocket.send(jsonGeometry);}).start();
                        if(mWebSocketClient.getConnection().isOpen())
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
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {
        new Thread(() -> {
            try {
                r = new Gson().fromJson(HttpApi.getId(URL_API_ORDERS_TREE+"/"+hash), RootOrderOne.class);
                JSONObject jsonGeometry = new JSONObject();
                runOnUiThread(() -> {
                    try {
                        jsonGeometry.put("Longitude", userLocationView.getArrow().getGeometry().getLongitude());
                        jsonGeometry.put("Latitude", userLocationView.getArrow().getGeometry().getLatitude());
                        jsonGeometry.put("user", r.getHash_user());
                        //new Thread(()->{tcpSocket.send(jsonGeometry);}).start();
                        if(mWebSocketClient.getConnection().isOpen())
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
        new Thread(()-> HttpApi.post(URL_API_TIME, "hash=" + hash + "&time=" + TimeRoute)).start();
        timeRoute();
    }

    private void timeRoute()  {
        new Thread(()-> {
            while(true) {
                runOnUiThread(()-> time.setText(TimeRoute));
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String[] tm = TimeRoute.split("\\D+");
                if(Integer.parseInt(String.join("", tm)) == 0)
                    break;
                else {
                    int tmi = Integer.parseInt(String.join("", tm));
                    tmi--;
                    TimeRoute = String.valueOf(tmi)+" мин";
                }
            }
        }).start();
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

    private void submitRequest() {
        DrivingOptions drivingOptions = new DrivingOptions();
        VehicleOptions vehicleOptions = new VehicleOptions();
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        MyLocationListener myLocationListener = new MyLocationListener();

        new Thread(() -> {
            try{
                RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(URL_API_ORDERS_TREE+"/"+hash), RootOrderOne.class);
                ArrayList<String> data = new ArrayList<>(Arrays.asList(rootOrderOne.getStart().split(",")));
                ArrayList<String> dataEnd = new ArrayList<>(Arrays.asList(rootOrderOne.getFinish().split(",")));
                ROUTE_POINT_LOCATION = new Point(Double.parseDouble(data.get(1)), Double.parseDouble(data.get(0)));
                ROUTE_END_LOCATION = new Point(Double.parseDouble(dataEnd.get(1)), Double.parseDouble(dataEnd.get(0)));
                ROUTE_START_LOCATION = new Point(MyLocationListener.imHere.getLatitude(), MyLocationListener.imHere.getLongitude());
                runOnUiThread(() -> {
                    requestPoints.add(new RequestPoint(
                            ROUTE_START_LOCATION,
                            RequestPointType.WAYPOINT,
                            null));
                    requestPoints.add(new RequestPoint(
                            ROUTE_POINT_LOCATION,
                            RequestPointType.WAYPOINT,
                            null));
                    requestPoints.add(new RequestPoint(
                            ROUTE_END_LOCATION,
                            RequestPointType.WAYPOINT,
                            null));
                    mapObjects.addPlacemark(ROUTE_POINT_LOCATION,
                            ImageProvider.fromResource(GoActivityDriver.this, R.drawable.pin)).setDraggable(true);
                    mapObjects.addPlacemark(ROUTE_END_LOCATION,
                            ImageProvider.fromResource(GoActivityDriver.this, R.drawable.finish)).setDraggable(true);
                    drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, GoActivityDriver.this);
                });
            } catch (Exception e){
                Log.d("Ex parse", e.getMessage());
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void RedirectToHome() {
        DBClass db = new DBClass();
        String url_order = URL_API + "/" + db.getHash(this);
        Runnable geoListener = () -> {
            if(!flagNew) {
                try {
                    RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(url_order), RootOrderOne.class);
                    if (rootOrderOne.getActive().equals("0") || rootOrderOne.getActive().equals("4")) {
                        executor.shutdown();
                        startActivity(new Intent("com.example.taxi_full.RequestDriver"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                if(mWebSocketClientNotifications != null) {
                    if (mWebSocketClientNotifications.getConnection().isOpen())
                        mWebSocketClientNotifications.send("{\"ping\" : \"" + hash + "\"}");
                }
                isPong = false;
                isPongNot = false;
                try {
                    Thread.sleep(2000);
                    if(!isPong) {
                        if(mWebSocketClient.getConnection().isOpen())
                            mWebSocketClient.close();
                        connectToSocket();
                    }
                    if(!isPongNot) {
                        if(mWebSocketClientNotifications.getConnection().isOpen())
                            mWebSocketClientNotifications.close();
                        connectToSocketNotifications();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void telephone(RootOrderOne rootOrderOne){
        ImageButton tel = findViewById(R.id.tel);
        new Thread(()->{
            try {
                RootUserOne root = new Gson().fromJson(HttpApi.getId("http://45.86.47.12/api/user/"+ rootOrderOne.getHash_driver() +"/1"), RootUserOne.class);
                runOnUiThread(()->{
                    tel.setOnClickListener(view->{
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + root.getPhone()));
                        startActivity(intent);
                    });
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }
}
