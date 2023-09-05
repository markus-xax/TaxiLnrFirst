package com.example.taxi_full;

import static android.os.SystemClock.sleep;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.MyLocationListener;
import com.example.taxi_full.API.StyleCard;
import com.example.taxi_full.API.model.RootCars;
import com.example.taxi_full.API.model.RootHomeWork;
import com.example.taxi_full.API.model.RootOrderOne;
import com.example.taxi_full.API.model.RootUserGeolocation;
import com.example.taxi_full.API.model.RootUserOne;
import com.example.taxi_full.API.model.geocode.RootGeolocation;
import com.example.taxi_full.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectDragListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
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

import java.io.IOException;
import java.lang.reflect.Type;
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
import java.util.concurrent.atomic.AtomicInteger;


public class HomeActivity extends AppCompatActivity implements UserLocationObjectListener, DrivingSession.DrivingRouteListener {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final String GEOCODER_API_KEY = "94c7a826-02a9-4847-b560-1699c2b7d751";
    private MapView mapView;
    private static final int REQUEST_CODE_PERMISSION_INTERNET = 1;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private UserLocationLayer userLocationLayer;
    private Point ROUTE_START_LOCATION = null;
    private Point ROUTE_END_LOCATION = null;
    private MapObjectCollection mapObjects;
    private final String URL_API = "http://45.86.47.12/api/orders";
    private final String URL_API_DRAG = "http://45.86.47.12/api/drag";
    private final String URL_API_USERS = "http://45.86.47.12/api/users";
    private final String URL_API_USER = "http://45.86.47.12/api/user";
    private final String URL_CARS = "http://45.86.47.12/api/cars/";
    private final String URL_HOME_WORK = "http://45.86.47.12/api/homeWork/";
    private final String URL_API_ORDERS_TREE = "http://45.86.47.12/api/ordersThree/";
    private DBClass DBClass = new DBClass();
    private DBHelper dbHelper;
    private EditText start;
    private EditText finish;
    private String startString, start_string = null;
    private String finishString, finsh_string = null;
    private WebSocketClient mWebSocketClient;
    private WebSocketClient mWebSocketClientButton;
    java.util.Map<String, Object> geo = new HashMap<>();
    private final HashMap<String, HashMap<String, Object>> users = new HashMap<>();
    private HashMap<String, Integer> colorsCars = new HashMap<>();
    private int Class = 1;
    private String distance, price = null;
    private DrivingSession drivingSession;
    private DrivingRouter drivingRouter;
    private PlacemarkMapObject point1, point2;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private int countUserLocation = 0;
    private List<PolylineMapObject> routesCollection = new ArrayList<>();
    public List<RootUserGeolocation> posts = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        permissionInternet();
        requestLocationPermission();

        DirectionsFactory.initialize(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_home);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapView = findViewById(R.id.mapview);
        mapView.getMap().setRotateGesturesEnabled(true);
        mapView.getMap().move(new CameraPosition(new Point(0, 0), 16, 0, 0));

        //Стили!!!
        StyleCard.setMapStyle(mapView);

        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);

        userLocationLayer.setObjectListener(this);

        mapView.getMap().addInputListener(il);
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show());
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_profile, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_info, R.id.nav_exit).setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        connectToSocket();
        connectToSocketButton();
        dbHelper = new DBHelper(this);

        start = findViewById(R.id.start_bottom);
        finish = findViewById(R.id.finish_bottom);


        TextView eco = findViewById(R.id.eco);
        TextView middle = findViewById(R.id.middle);
        TextView business = findViewById(R.id.business);

        Button typeNal = findViewById(R.id.type_home_nal);
        Button typeOffNal = findViewById(R.id.type_home_offnal);

        typeNal.setOnClickListener(view-> new Thread(()-> {
            HttpApi.put(URL_API_ORDERS_TREE + DBClass.getHash(this), "type_pay=" + 1);
            typeNal.setBackgroundColor(Color.LTGRAY);
        }).start());
        typeOffNal.setOnClickListener(view-> new Thread(()-> {
            HttpApi.put(URL_API_ORDERS_TREE + DBClass.getHash(this), "type_pay=" + 2);
            typeOffNal.setBackgroundColor(Color.LTGRAY);
        }).start());

        //добавить в бд класс заказа
        eco.setOnClickListener(view -> {
            Class = 1;
            eco.setBackgroundColor(Color.GRAY);
        });
        middle.setOnClickListener(view -> {
            Class = 2;
            middle.setBackgroundColor(Color.GRAY);
        });
        business.setOnClickListener(view -> {
            Class = 3;
            business.setBackgroundColor(Color.GRAY);
        });

        StartPointGeolocation();
        dragPoints();
        go();
        echoTextMenu();
        startEdit();
        finishEdit();
        EditTextLocked();
        bottomEditStartFinishPoint();
        startCheck();
        addHomeWorkBottom();
        setPointsHomeWork();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();

        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
        try {
            RedirectToDriverFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        MapKitFactory.getInstance().onStop();
        mapView.onStop();
        super.onDestroy();
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

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {

        userLocationLayer.setAnchor(
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83)));

        userLocationView.getArrow().setIcon(ImageProvider.fromResource(this, R.drawable.user_location));

        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();

        pinIcon.setIcon("icon", ImageProvider.fromResource(this, R.drawable.user_location), new IconStyle().setAnchor(new PointF(0f, 0f)).setRotationType(RotationType.ROTATE).setZIndex(0f).setScale(1f));


        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);

    }

    @Override
    public void onObjectRemoved(UserLocationView view) {
    }

    @Override
    public void onObjectUpdated(UserLocationView view, ObjectEvent event) {
        ++countUserLocation;
        if(countUserLocation > 2) {
            if (userLocationLayer.isAnchorEnabled())
                userLocationLayer.resetAnchor();
        }
    }

    private final InputListener il = new InputListener() {
        @Override
        public void onMapTap(@NonNull Map map, @NonNull Point point) {
            DBClass = new DBClass();
            String hash = DBClass.getHash(HomeActivity.this);
            String url = URL_API + "/" + hash;

            new Thread(() -> {
                Gson parser = new Gson();
                try {
                    if (HttpApi.getId(url).equals("0")) {
                        RootGeolocation rootGeoStart = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + point.getLongitude() + "," + point.getLatitude() + "&apikey=" + GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                        try {
                            String start_stringUK = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription() + rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getName();
                            start_string = start_stringUK.replace("Украина","");
                        } catch (Exception e) {
                            start_string = null;
                        }

                        String arr = "hash_user=" + hash + "&hash_driver=" + "&start=" + point.getLongitude() + "," + point.getLatitude() + "&start_string=" + start_string;

                        if (start_string != null && HttpApi.post(URL_API, arr) == HttpURLConnection.HTTP_OK) {
                            ROUTE_START_LOCATION = point;

                            runOnUiThread(() -> {
                                point1 = mapObjects.addPlacemark(ROUTE_START_LOCATION, ImageProvider.fromResource(HomeActivity.this, R.drawable.pin));

                                //перемещение старта

                                point1.setDraggable(true);
                                point1.setDragListener(new MapObjectDragListener() {
                                    @Override
                                    public void onMapObjectDragStart(@NonNull MapObject mapObject) {

                                    }

                                    @Override
                                    public void onMapObjectDrag(@NonNull MapObject mapObject, @NonNull Point point) {

                                    }

                                    @Override
                                    public void onMapObjectDragEnd(@NonNull MapObject mapObject) {
                                        Point p1 = new Point(point1.getGeometry().getLatitude(), point1.getGeometry().getLongitude());
                                        ROUTE_START_LOCATION = p1;
                                        if (ROUTE_END_LOCATION != null)
                                            submitRequest();
                                        else
                                            showToast("Пожалуйста, поставте вторую точку!");

                                        new Thread(() -> {
                                            String start_str = null;
                                            RootGeolocation rootGeoStart = null;
                                            try {
                                                rootGeoStart = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + p1.getLongitude() + "," + p1.getLatitude() + "&apikey=" + GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                String start_strUK = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription() + rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getName();
                                                start_str = start_strUK.replace("Украина","");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            while (true) {
                                                if (distance != null & price != null) {
                                                    String arg = "start=" + p1.getLongitude() + "," + p1.getLatitude() + "&start_string=" + start_str + "&price=" + price + "&distance=" + distance;
                                                    if (start_str != null && HttpApi.put(URL_API_DRAG + "/" + hash, arg) == HttpURLConnection.HTTP_OK) {
                                                        final String str = start_str;
                                                        EditText st = findViewById(R.id.start_bottom);
                                                        runOnUiThread(() -> {
                                                            st.setText(str);
                                                        });
                                                    } else
                                                        showToast(getString(R.string.unknown_error_message));

                                                    price = null;
                                                    distance = null;

                                                    return;
                                                }
                                            }
                                        }).start();
                                    }
                                });

                                start.setText(start_string);
                            });

                        } else showToast(getString(R.string.unknown_error_message));

                    } else {
                        RootOrderOne root = parser.fromJson(HttpApi.getId(url), RootOrderOne.class);

                        if (root.getActive().equals("1")) {

                            if (root.getFinish().equals("")) {

                                RootGeolocation rootGeoStart = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + point.getLongitude() + "," + point.getLatitude() + "&apikey=" + GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                                try {
                                    String finish_stringUK = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription() + rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getName();
                                    finsh_string = finish_stringUK.replace("Украина","");
                                } catch (Exception e) {
                                    finsh_string = null;
                                }
                                ROUTE_END_LOCATION = point;
                                submitRequest();
                                while (true) {
                                    if (distance != null & price != null) {
                                        String arg = "finish=" + point.getLongitude() + "," + point.getLatitude() + "&finish_string=" + finsh_string + "&price=" + price + "&distance=" + distance;
                                        if (finsh_string != null && HttpApi.put(url, arg) == HttpURLConnection.HTTP_OK) {
                                            runOnUiThread(() -> {
                                                //перемещение финица
                                                point2 = mapObjects.addPlacemark(ROUTE_END_LOCATION, ImageProvider.fromResource(HomeActivity.this, R.drawable.finish));

                                                point2.setDraggable(true);
                                                point2.setDragListener(new MapObjectDragListener() {
                                                    @Override
                                                    public void onMapObjectDragStart(@NonNull MapObject mapObject) {

                                                    }

                                                    @Override
                                                    public void onMapObjectDrag(@NonNull MapObject mapObject, @NonNull Point point) {

                                                    }

                                                    @Override
                                                    public void onMapObjectDragEnd(@NonNull MapObject mapObject) {
                                                        Point p2 = new Point(point2.getGeometry().getLatitude(), point2.getGeometry().getLongitude());
                                                        ROUTE_END_LOCATION = p2;

                                                        if (ROUTE_START_LOCATION != null)
                                                            submitRequest();
                                                        else
                                                            showToast("Пожалуйста, поставте вторую точку");

                                                        new Thread(() -> {
                                                            String finish_str = null;
                                                            RootGeolocation rootGeoStart = null;
                                                            try {
                                                                rootGeoStart = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + p2.getLongitude() + "," + p2.getLatitude() + "&apikey=" + GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                            try {
                                                                String finish_strUK = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription() + rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getName();
                                                                finish_str = finish_strUK.replace("Украина","");
                                                            } catch (Exception e) {
                                                                finish_str = null;
                                                            }
                                                            while (true) {
                                                                if (distance != null & price != null) {
                                                                    String arg = "finish=" + p2.getLongitude() + "," + p2.getLatitude() + "&finish_string=" + finish_str + "&price=" + price + "&distance=" + distance;
                                                                    if (finish_str != null && HttpApi.put(URL_API + "/" + hash, arg) == HttpURLConnection.HTTP_OK) {
                                                                        final String str = finish_str;
                                                                        EditText ft = findViewById(R.id.finish_bottom);
                                                                        runOnUiThread(() -> {
                                                                            ft.setText(str);
                                                                        });
                                                                    } else
                                                                        showToast(getString(R.string.unknown_error_message));
                                                                    price = null;
                                                                    distance = null;
                                                                    return;
                                                                }
                                                            }
                                                        }).start();
                                                    }
                                                });

                                                finish.setText(finsh_string);
                                            });
                                        } else showToast(getString(R.string.unknown_error_message));
                                        return;
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }

        @Override
        public void onMapLongTap(@NonNull Map map, @NonNull Point point) {

        }
    };

    private final MapObjectDragListener dragFinish = new MapObjectDragListener() {
        @Override
        public void onMapObjectDragStart(@NonNull MapObject mapObject) {

        }

        @Override
        public void onMapObjectDrag(@NonNull MapObject mapObject, @NonNull Point point) {

        }

        @Override
        public void onMapObjectDragEnd(@NonNull MapObject mapObject) {
            DBClass db = new DBClass();
            String hash = db.getHash(HomeActivity.this);
            Point p2 = new Point(point2.getGeometry().getLatitude(), point2.getGeometry().getLongitude());
            ROUTE_END_LOCATION = p2;
            submitRequest();
            new Thread(() -> {
                String finish_str = null;
                RootGeolocation rootGeoStart = null;
                try {
                    rootGeoStart = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + p2.getLongitude() + "," + p2.getLatitude() + "&apikey=" + GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    String finish_strUK = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription() + rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getName();
                    finish_str = finish_strUK.replace("Украина","");
                } catch (Exception e) {
                    finish_str = null;
                }
                while (true) {
                    if (distance != null & price != null) {
                        String arg = "finish=" + p2.getLongitude() + "," + p2.getLatitude() + "&finish_string=" + finish_str + "&price=" + price + "&distance=" + distance;
                        if (finish_str != null && HttpApi.put(URL_API + "/" + hash, arg) == HttpURLConnection.HTTP_OK) {
                            final String str = finish_str;
                            EditText ft = findViewById(R.id.finish_bottom);
                            runOnUiThread(() -> {
                                ft.setText(str);
                            });
                        } else
                            showToast(getString(R.string.unknown_error_message));
                        price = null;
                        distance = null;
                        return;
                    }
                }
            }).start();
        }
    };

    private final MapObjectDragListener drag = new MapObjectDragListener() {
        @Override
        public void onMapObjectDragStart(@NonNull MapObject mapObject) {

        }

        @Override
        public void onMapObjectDrag(@NonNull MapObject mapObject, @NonNull Point point) {

        }

        @Override
        public void onMapObjectDragEnd(@NonNull MapObject mapObject) {
            DBClass db = new DBClass();
            String hash = db.getHash(HomeActivity.this);
            Point p1 = new Point(point1.getGeometry().getLatitude(), point1.getGeometry().getLongitude());
            ROUTE_START_LOCATION = p1;
            if(ROUTE_END_LOCATION != null)
                submitRequest();


            new Thread(() -> {
                String start_str = null;
                RootGeolocation rootGeoStart = null;
                try {
                    rootGeoStart = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + p1.getLongitude() + "," + p1.getLatitude() + "&apikey=" + GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    String start_strUK = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription() + rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getName();
                    start_str = start_strUK.replace("Украина","");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (true) {
                    if (distance != null & price != null) {
                        String arg = "start=" + p1.getLongitude() + "," + p1.getLatitude() + "&start_string=" + start_str + "&price=" + price + "&distance=" + distance;
                        if (start_str != null && HttpApi.put(URL_API_DRAG + "/" + hash, arg) == HttpURLConnection.HTTP_OK) {
                            final String str = start_str;
                            EditText st = findViewById(R.id.start_bottom);
                            runOnUiThread(() -> {
                                st.setText(str);
                            });
                        } else
                            showToast(getString(R.string.error_not_read_point));
                        price = null;
                        distance = null;
                        return;
                    }
                }
            }).start();
        }
    };


    public void showToast(final String toast) {
        runOnUiThread(() -> Toast.makeText(HomeActivity.this, toast, Toast.LENGTH_LONG).show());
    }

    private void connectToSocket() {
        URI uri;
        try {
            uri = new URI("ws" + "://" + "45.86.47.12:27800");
        } catch (URISyntaxException e) {
            Log.d("----uri------", e.getMessage());
            return;
        }
        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            final HashMap<String, PlacemarkMapObject> DC = new HashMap<>();

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("WebsocketGeoHome", "Opened");
            }

            @Override
            public void onMessage(String s) {
                try {
                    Runnable getCars = () -> {
                        DBClass = new DBClass();
                        Type listType = new TypeToken<List<RootUserGeolocation>>() {
                        }.getType();
                        posts = new Gson().fromJson(s, listType);
                        HashMap<String, Object> arr = new HashMap<>();
                        for (int i = 0; i < posts.size(); i++) {
                            arr.put("Latitude", posts.get(i).getLatitude());
                            arr.put("Longitude", posts.get(i).getLongitude());
                            arr.put("DC", posts.get(i).getDc());
                            arr.put("Duration", posts.get(i).getDuration());
                            users.put(posts.get(i).getHash(), arr);
                            if (colorsCars != null && colorsCars.get(posts.get(i).getHash()) == null) {
                                try {
                                    if (!HttpApi.getId(URL_CARS + posts.get(i).getHash()).equals("0")) {
                                        RootCars cars = new Gson().fromJson(HttpApi.getId(URL_CARS + posts.get(i).getHash()), RootCars.class);
                                        colorsCars.put(posts.get(i).getHash(), cars.getColor());
                                    }
                                } catch (IOException e) {
                                    colorsCars = null;
                                }
                            }
                        }
                    };
                    Thread thread = new Thread(getCars);
                    thread.start();
                    thread.join();
                } catch (Exception e){
                    e.printStackTrace();
                }

                    runOnUiThread(() -> {
                        for (int i = 0; i < posts.size(); i++) {
                            if (colorsCars != null) {
                                if (!DBClass.getHash(HomeActivity.this).equals(posts.get(i).getHash())) {
                                    if (DC.get(posts.get(i).getHash()) == null) {
                                        if (colorsCars.get(posts.get(i).getHash()) != null) {
                                            Point cars = new Point(Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Latitude"))), Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Longitude"))));
                                            switch (colorsCars.get(posts.get(i).getHash())) {
                                                case 1:
                                                    PlacemarkMapObject m = mapObjects.addPlacemark(cars, ImageProvider.fromResource(HomeActivity.this, R.drawable.car_white_small));
                                                    m.setIconStyle(new IconStyle().setRotationType(RotationType.ROTATE));
                                                    m.setDirection(Float.parseFloat(users.get(posts.get(i).getHash()).get("Duration").toString()));
                                                    DC.put(posts.get(i).getHash(), m);
                                                    break;
                                                case 2:
                                                    PlacemarkMapObject mB = mapObjects.addPlacemark(cars, ImageProvider.fromResource(HomeActivity.this, R.drawable.car_black_small));
                                                    mB.setIconStyle(new IconStyle().setRotationType(RotationType.ROTATE));
                                                    mB.setDirection(Float.parseFloat(users.get(posts.get(i).getHash()).get("Duration").toString()));
                                                    DC.put(posts.get(i).getHash(), mB);
                                                    break;
                                                case 3:
                                                    PlacemarkMapObject mBl = mapObjects.addPlacemark(cars, ImageProvider.fromResource(HomeActivity.this, R.drawable.car_blue_small));
                                                    mBl.setIconStyle(new IconStyle().setRotationType(RotationType.ROTATE));
                                                    mBl.setDirection(Float.parseFloat(users.get(posts.get(i).getHash()).get("Duration").toString()));
                                                    DC.put(posts.get(i).getHash(), mBl);
                                                    break;
                                                case 4:
                                                    PlacemarkMapObject mBG = mapObjects.addPlacemark(cars, ImageProvider.fromResource(HomeActivity.this, R.drawable.car_green_small));
                                                    mBG.setIconStyle(new IconStyle().setRotationType(RotationType.ROTATE));
                                                    mBG.setDirection(Float.parseFloat(users.get(posts.get(i).getHash()).get("Duration").toString()));
                                                    DC.put(posts.get(i).getHash(), mBG);
                                                    break;
                                                case 5:
                                                    PlacemarkMapObject mR = mapObjects.addPlacemark(cars, ImageProvider.fromResource(HomeActivity.this, R.drawable.car_red_small));
                                                    mR.setIconStyle(new IconStyle().setRotationType(RotationType.ROTATE));
                                                    mR.setDirection(Float.parseFloat(users.get(posts.get(i).getHash()).get("Duration").toString()));
                                                    DC.put(posts.get(i).getHash(), mR);
                                                    break;
                                                case 6:
                                                    PlacemarkMapObject mY = mapObjects.addPlacemark(cars, ImageProvider.fromResource(HomeActivity.this, R.drawable.car_yellow_small));
                                                    mY.setIconStyle(new IconStyle().setRotationType(RotationType.ROTATE));
                                                    mY.setDirection(Float.parseFloat(users.get(posts.get(i).getHash()).get("Duration").toString()));
                                                    DC.put(posts.get(i).getHash(), mY);
                                                    break;
                                            }
                                        }
                                    } else {
                                        DC.get(posts.get(i).getHash()).setGeometry(new Point(Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Latitude"))), Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Longitude")))));
                                        DC.get(posts.get(i).getHash()).setDirection(Float.parseFloat(users.get(posts.get(i).getHash()).get("Duration").toString()));
                                    }
                                }
                            }
                        }
                    });
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

    private void connectToSocketButton() {
        URI uri;
        try {
            uri = new URI("ws" + "://" + "45.86.47.12:27810");
        } catch (URISyntaxException e) {
            Log.d("----uri------", e.getMessage());
            return;
        }
        mWebSocketClientButton = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("WebsocketH", "Opened");
            }

            @Override
            public void onMessage(String s) {
                Log.d("--mes--", s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.d("WebsocketH", "Closed " + s);
            }
            @Override
            public void onError(Exception e) {
                Log.d("WebsocketH", "Error " + e.getMessage());
            }
        };
        mWebSocketClientButton.connect();
    }



    private void RedirectToDriver() {
        String url_order = URL_API + "/" + DBClass.getHash(this);
        Runnable geoListener = () -> {
            try {
                RootOrderOne r = new Gson().fromJson(HttpApi.getId(url_order), RootOrderOne.class);
                if (r.getActive().equals("3") || r.getActive().equals("4") || r.getActive().equals("5")) {
                    startActivity(new Intent("com.example.taxi_full.GoUser"));
                    executor.shutdown();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };


        executor.scheduleAtFixedRate(geoListener, 0, 5, TimeUnit.SECONDS);
    }


    private void RedirectToDriverFirst() {
        String url_order = URL_API + "/" + DBClass.getHash(this);
        new Thread(() -> {
            RootOrderOne r = null;
            try {if(!HttpApi.getId(url_order).equals("0")) {r = new Gson().fromJson(HttpApi.getId(url_order), RootOrderOne.class);}} catch (Exception e) {e.printStackTrace();}
            if (r != null && (r.getActive().equals("3") || r.getActive().equals("4") || r.getActive().equals("5"))) {
                try {TimeUnit.SECONDS.sleep(2);} catch (InterruptedException e) {e.printStackTrace();}
                startActivity(new Intent("com.example.taxi_full.GoUser"));
            }
        }).start();

    }


    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> list) {
        double smallRouteDistance = 0;
        Polyline smallRoute = new Polyline();
        for (DrivingRoute route : list) {
            if (smallRouteDistance == 0) {
                smallRouteDistance = route.getMetadata().getWeight().getDistance().getValue();
                distance = route.getMetadata().getWeight().getDistance().getText();
                smallRoute = route.getGeometry();
            } else {
                if (smallRouteDistance > route.getMetadata().getWeight().getDistance().getValue()) {
                    smallRouteDistance = route.getMetadata().getWeight().getDistance().getValue();
                    distance = route.getMetadata().getWeight().getDistance().getText();
                    smallRoute = route.getGeometry();
                }
            }
        }
        int mathPrice = (int) Math.round(((smallRouteDistance * 40) / 1000));
        price = String.valueOf(mathPrice);
        if(routesCollection != null && !routesCollection.isEmpty())
            routesCollection.get(0).setGeometry(smallRoute);
        else
            routesCollection.add(mapObjects.addPolyline(smallRoute));
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
        try {
            runOnUiThread(() -> {
                if (ROUTE_START_LOCATION != null && ROUTE_END_LOCATION != null) {
                    requestPoints.add(new RequestPoint(ROUTE_START_LOCATION, RequestPointType.WAYPOINT, null));
                    requestPoints.add(new RequestPoint(ROUTE_END_LOCATION, RequestPointType.WAYPOINT, null));
                }
                drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, HomeActivity.this);
            });
        } catch (Exception e) {
            Log.d("Ex parse", e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void bottomEditStartFinishPoint(){
        MyLocationListener.SetUpLocationListener(this);
        DBClass DBClass = new DBClass();
        new Thread(()->{
            try {
                String url_order = URL_API + "/" + DBClass.getHash(this);
                RootGeolocation rootGeolocation = null;
                String startFinishString = null;
                RootOrderOne rootOrderOne = null;

                if (!HttpApi.getId(url_order).equals("0"))
                    rootOrderOne = new Gson().fromJson(HttpApi.getId(url_order), RootOrderOne.class);

                if(rootOrderOne == null || rootOrderOne.getStart_string().equals("") || rootOrderOne.getFinish_string().equals("")) {
                    rootGeolocation = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + MyLocationListener.imHere.getLongitude() + "," + MyLocationListener.imHere.getLatitude() + "&apikey=" + GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                    try {
                        String startFinishStringUK = rootGeolocation.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription();
                        startFinishString = startFinishStringUK.replace("Украина", "");
                    } catch (Exception ignored) {}
                }

                String finalStartFinishString = startFinishString;
                RootOrderOne finalRootOrderOne = rootOrderOne;
                runOnUiThread(() -> {
                    if(finalRootOrderOne == null || finalRootOrderOne.getStart_string().equals(""))
                        if (finalStartFinishString != null)
                            start.setText(finalStartFinishString);
                    if(finalRootOrderOne == null || finalRootOrderOne.getFinish_string().equals(""))
                        if (finalStartFinishString != null)
                            finish.setText(finalStartFinishString);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startEdit(){
        start.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                new Thread(() -> {
                    DBClass db = new DBClass();
                    try {
                        String hash = db.getHash(HomeActivity.this);
                        RootGeolocation rootGeolocation = null;
                        rootGeolocation = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + start.getText() + "&apikey=" + GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                        ArrayList<String> pos = new ArrayList<>(Arrays.asList(rootGeolocation.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getPoint().getPos().split(" ")));
                        Point p1 = new Point(Double.parseDouble(pos.get(1)), Double.parseDouble(pos.get(0)));
                        ROUTE_START_LOCATION = p1;
                        if(ROUTE_END_LOCATION != null)
                            submitRequest();
                        else {
                            String arr = "hash_user=" + hash + "&hash_driver=" + "&start=" + p1.getLongitude() + "," + p1.getLatitude() + "&start_string=" + start.getText();
                            if (HttpApi.post(URL_API, arr) == HttpURLConnection.HTTP_OK) {
                                runOnUiThread(()->{
                                    point1 = mapObjects.addPlacemark(p1,  ImageProvider.fromResource(HomeActivity.this, R.drawable.pin));
                                });
                                showToast("Успех");
                                price = null;
                                distance = null;
                                return;
                            }
                        }
                        while(true){
                            if(distance != null && price != null) {
                                String arg = "start=" + p1.getLongitude() + "," + p1.getLatitude() + "&start_string=" + start.getText() + "&price=" + price + "&distance=" + distance;
                                if (HttpApi.put(URL_API_DRAG + "/" + hash, arg) == HttpURLConnection.HTTP_OK) {
                                    runOnUiThread(()->{
                                        if(point1 != null)
                                            point1.setGeometry(p1);
                                        else
                                            point1 = mapObjects.addPlacemark(p1,  ImageProvider.fromResource(HomeActivity.this, R.drawable.pin));
                                    });
                                    showToast("Успех");
                                }
                                else
                                    showToast(getString(R.string.error_not_read_point));

                                price = null;
                                distance = null;
                                return;
                            }
                            sleep(100);
                        }
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }).start();
                handled = true;
            }
            return handled;
        });
    }

    private void finishEdit(){
        finish.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                new Thread(() -> {
                    DBClass db = new DBClass();
                    try {
                        String hash = db.getHash(HomeActivity.this);
                        RootGeolocation rootGeolocation = null;
                        rootGeolocation = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + finish.getText() + "&apikey=" + GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                        ArrayList<String> pos = new ArrayList<>(Arrays.asList(rootGeolocation.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getPoint().getPos().split(" ")));
                        Point p2 = new Point(Double.parseDouble(pos.get(1)), Double.parseDouble(pos.get(0)));
                        ROUTE_END_LOCATION = p2;
                        submitRequest();

                        while (true) {
                            if (distance != null && price != null) {
                                String arg = "finish=" + p2.getLongitude() + "," + p2.getLatitude() + "&finish_string=" + finish.getText() + "&price=" + price + "&distance=" + distance;
                                if (HttpApi.put(URL_API + "/" + hash, arg) == HttpURLConnection.HTTP_OK) {
                                    runOnUiThread(()->{
                                        if(point2 != null)
                                            point2.setGeometry(p2);
                                        else
                                            point2 = mapObjects.addPlacemark(p2, ImageProvider.fromResource(HomeActivity.this, R.drawable.finish));
                                    });
                                    showToast("Успех");
                                    price = null;
                                    distance = null;
                                    return;
                                } else
                                    showToast(getString(R.string.error_not_read_point));
                            }
                            sleep(100);
                        }
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }).start();
                InputMethodManager imm = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                handled = true;
            }
            return handled;
        });
    }

    private void echoTextMenu(){
        new Thread(() -> {
            DBClass dbClass = new DBClass();
            String hash = dbClass.getHash(this);
            String url = URL_API_USER + "/" + hash + "/" + dbClass.getDC(this);
            try {
                RootUserOne rootUserOne = new Gson().fromJson(HttpApi.getId(url), RootUserOne.class);
                runOnUiThread(() -> {
                    if (rootUserOne.getError() != null && rootUserOne.getError().equals("")) {
                        TextView name_surname_menu = findViewById(R.id.menuNameSurname);
                        TextView rate_menu = findViewById(R.id.menuRate);
                        name_surname_menu.setText(rootUserOne.getNameSurname() + "");
                        if (rootUserOne.getRate() == null || rootUserOne.getRate().equals(""))
                            rate_menu.setText("5");
                        else rate_menu.setText(rootUserOne.getRate());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void go(){
        Button go = findViewById(R.id.buttonGoSheet);

        go.setOnClickListener(view -> {
            DBClass db = new DBClass();
            String hash = db.getHash(HomeActivity.this);
            String url = URL_API_USERS + "/" + hash;
            String arg = "active=2" + "&class=" + Class;
            new Thread(() -> {
                if (HttpApi.put(url, arg) == HttpURLConnection.HTTP_OK) {
                    showToast("Ожидайте пока приймут ваш заказ");
                    RedirectToDriver();
                    mWebSocketClientButton.send("buttonOn");
                    runOnUiThread(()->{
                        point1.setDraggable(false);
                        point2.setDraggable(false);
                    });
                    //вывод всплыувуающего окна
                }
            }).start();

        });
    }

    private void dragPoints(){
        new Thread(() -> {
            DBClass db = new DBClass();
            String hash = db.getHash(HomeActivity.this);
            try {
                if (!HttpApi.getId(URL_API + "/" + hash).equals("0")) {
                    RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(URL_API + "/" + hash), RootOrderOne.class);
                    runOnUiThread(() -> {
                        if (rootOrderOne.getStart() != null) {
                            start.setText(rootOrderOne.getStart_string());
                            ArrayList<String> data = new ArrayList<>(Arrays.asList(rootOrderOne.getStart().split(",")));
                            Point start = new Point(Double.parseDouble(data.get(1)), Double.parseDouble(data.get(0)));
                            ROUTE_START_LOCATION = start;
                            point1 = mapObjects.addPlacemark(start, ImageProvider.fromResource(HomeActivity.this, R.drawable.pin));

                            if(rootOrderOne.getActive().equals("1")) {
                                point1.setDraggable(true);
                                point1.setDragListener(drag);
                            }
                        }
                        if (!rootOrderOne.getFinish().equals("")) {
                            finish.setText(rootOrderOne.getFinish_string());
                            ArrayList<String> dataEnd = new ArrayList<>(Arrays.asList(rootOrderOne.getFinish().split(",")));
                            Point finish = new Point(Double.parseDouble(dataEnd.get(1)), Double.parseDouble(dataEnd.get(0)));
                            ROUTE_END_LOCATION = finish;
                            point2 = mapObjects.addPlacemark(finish, ImageProvider.fromResource(HomeActivity.this, R.drawable.finish));
                            submitRequest();
                            if(rootOrderOne.getActive().equals("1")) {
                                point2.setDraggable(true);
                                point2.setDragListener(dragFinish);
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void EditTextLocked(){
        new Thread(() -> {
            DBClass db = new DBClass();
            String hash = db.getHash(HomeActivity.this);
            try {
                if (!HttpApi.getId(URL_API + "/" + hash).equals("0")) {
                    RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(URL_API + "/" + hash), RootOrderOne.class);
                    if (rootOrderOne.getActive().equals("2")) {
                        start.setEnabled(false);
                        finish.setEnabled(false);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    private void StartPointGeolocation(){
        MyLocationListener.SetUpLocationListener(this);
        new Thread(() -> {
            DBClass db = new DBClass();
            String hash = db.getHash(HomeActivity.this);
            String start;
            RootGeolocation rootGeocoder = null;
            try {
                if (HttpApi.getId(URL_API + "/" + hash).equals("0")) {
                    Point p = new Point(MyLocationListener.imHere.getLongitude(), MyLocationListener.imHere.getLatitude());
                    ROUTE_START_LOCATION = p;
                    rootGeocoder = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + ROUTE_START_LOCATION.getLatitude() + "," + ROUTE_START_LOCATION.getLongitude() + "&apikey=" + GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                    try {
                        String startUK = rootGeocoder.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription() + rootGeocoder.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getName();
                        start = startUK.replace("Украина", "");
                    } catch (Exception e) {
                        start = null;
                    }
                    String arr = "hash_user=" + hash + "&hash_driver=" + "&start=" + ROUTE_START_LOCATION.getLatitude() + "," + ROUTE_START_LOCATION.getLongitude() + "&start_string=" + start;
                    if (start != null) {
                        HttpApi.post(URL_API, arr);
                        runOnUiThread(() -> point1 = mapObjects.addPlacemark(new Point(ROUTE_START_LOCATION.getLongitude(), ROUTE_START_LOCATION.getLatitude()), ImageProvider.fromResource(HomeActivity.this, R.drawable.pin)));
                        this.start.setText(start);
                    }
                    Point p1 = new Point(MyLocationListener.imHere.getLatitude(), MyLocationListener.imHere.getLongitude());
                    ROUTE_START_LOCATION = p1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {
                InputMethodManager imm = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    private void finishLoad(){
        new Thread(()->{
            DBClass db = new DBClass();
            try {
                RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(URL_API + "/" + db.getHash(this)), RootOrderOne.class);
                runOnUiThread(()->finish.setText(rootOrderOne.getFinish_string()));
            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    private void addHomeWorkBottom(){
        EditText home = findViewById(R.id.home);
        EditText work = findViewById(R.id.work);

        home.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                new Thread(() -> {
                    DBClass db = new DBClass();
                    try {
                        String hash = db.getHash(HomeActivity.this);
                        RootGeolocation rootGeolocation = null;
                        rootGeolocation = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + home.getText() + "&apikey=" + GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                        ArrayList<String> pos = new ArrayList<>(Arrays.asList(rootGeolocation.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getPoint().getPos().split(" ")));
                        Point p2 = new Point(Double.parseDouble(pos.get(1)), Double.parseDouble(pos.get(0)));

                        String arg = "home=" + home.getText() + "&pointHome=" + p2.getLongitude() + "," + p2.getLatitude() + "&hw=1";
                        if (HttpApi.put(URL_HOME_WORK + hash, arg) == HttpURLConnection.HTTP_OK) {
                            showToast("Успех");
                        }
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }).start();
                InputMethodManager imm = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                handled = true;
            }
            return handled;
        });

        work.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                new Thread(() -> {
                    DBClass db = new DBClass();
                    try {
                        String hash = db.getHash(HomeActivity.this);
                        RootGeolocation rootGeolocation = null;
                        rootGeolocation = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + work.getText() + "&apikey=" + GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                        ArrayList<String> pos = new ArrayList<>(Arrays.asList(rootGeolocation.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getPoint().getPos().split(" ")));
                        Point p2 = new Point(Double.parseDouble(pos.get(1)), Double.parseDouble(pos.get(0)));

                        String arg = "work=" + work.getText() + "&pointWork=" + p2.getLongitude() + "," + p2.getLatitude() + "&hw=2";
                        if (HttpApi.put(URL_HOME_WORK + hash, arg) == HttpURLConnection.HTTP_OK) {
                            showToast("Успех");
                        }
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }).start();
                InputMethodManager imm = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                handled = true;
            }
            return handled;
        });
    }

    private void loadHomeWorkText(){
        EditText home = findViewById(R.id.home);
        EditText work = findViewById(R.id.work);
        DBClass db = new DBClass();
        new Thread(() -> {
            if(checkHomeWork() != 0) {
                try {
                    RootHomeWork rootHomeWork = new Gson().fromJson(HttpApi.getId(URL_HOME_WORK + db.getHash(this)), RootHomeWork.class);
                    runOnUiThread(() -> {
                        home.setText(rootHomeWork.getHome());
                        work.setText(rootHomeWork.getWork());
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startCheck(){
        new Thread(() -> {
            DBClass db = new DBClass();
            String arr = "hash=" + db.getHash(this);
            HttpApi.post(URL_HOME_WORK, arr);
            loadHomeWorkText();
        }).start();
    }

    private int checkHomeWork(){
        DBClass db = new DBClass();
        String url = URL_HOME_WORK + db.getHash(this);
        AtomicInteger check = new AtomicInteger();
        check.set(1);
        new Thread(()->{
            try {
                if(HttpApi.getId(url).equals("0")) {
                    check.set(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        return check.get();
    }

    private void setPointsHomeWork(){
        ImageView home = findViewById(R.id.homeButt);
        ImageView work = findViewById(R.id.workButt);
        EditText homeEdit = findViewById(R.id.home);
        EditText workEdit = findViewById(R.id.work);

        home.setOnClickListener(view -> new Thread(()->{
            DBClass db = new DBClass();
            try {
                if(!homeEdit.getText().toString().equals("")) {
                    RootHomeWork rootHomeWork = new Gson().fromJson(HttpApi.getId(URL_HOME_WORK + db.getHash(this)), RootHomeWork.class);
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(rootHomeWork.getPointHome().split(",")));
                    Point homeP = new Point(Double.parseDouble(data.get(1)), Double.parseDouble(data.get(0)));
                    ROUTE_END_LOCATION = homeP;
                    submitRequest();

                    while (true) {
                        if (distance != null && price != null) {
                            String arg = "finish=" + homeP.getLongitude() + "," + homeP.getLatitude() + "&finish_string=" + homeEdit.getText() + "&price=" + price + "&distance=" + distance;
                            if (HttpApi.put(URL_API + "/" + db.getHash(this), arg) == HttpURLConnection.HTTP_OK) {
                                runOnUiThread(()->{
                                    if(point2 != null)
                                        point2.setGeometry(homeP);
                                    else
                                        point2 = mapObjects.addPlacemark(homeP, ImageProvider.fromResource(HomeActivity.this, R.drawable.finish));
                                });
                                showToast("Успех");
                                price = null;
                                distance = null;
                                finishLoad();
                                return;
                            } else
                                showToast(getString(R.string.error_not_read_point));
                        }
                        sleep(100);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start());

        work.setOnClickListener(view -> new Thread(()->{
            DBClass db = new DBClass();
            try {
                if(!workEdit.getText().toString().equals("")) {
                    RootHomeWork rootHomeWork = new Gson().fromJson(HttpApi.getId(URL_HOME_WORK + db.getHash(this)), RootHomeWork.class);
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(rootHomeWork.getPointWork().split(",")));
                    Point workP = new Point(Double.parseDouble(data.get(1)), Double.parseDouble(data.get(0)));
                    ROUTE_END_LOCATION = workP;
                    submitRequest();

                    while (true) {
                        if (distance != null && price != null) {
                            String arg = "finish=" + workP.getLongitude() + "," + workP.getLatitude() + "&finish_string=" + workEdit.getText() + "&price=" + price + "&distance=" + distance;
                            if (HttpApi.put(URL_API + "/" + db.getHash(this), arg) == HttpURLConnection.HTTP_OK) {
                                runOnUiThread(()->{
                                    if(point2 != null)
                                        point2.setGeometry(workP);
                                    else
                                        point2 = mapObjects.addPlacemark(workP, ImageProvider.fromResource(HomeActivity.this, R.drawable.finish));
                                });
                                showToast("Успех");
                                price = null;
                                distance = null;
                                finishLoad();
                                return;
                            } else
                                showToast(getString(R.string.error_not_read_point));
                        }
                        sleep(100);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start());
    }
}

