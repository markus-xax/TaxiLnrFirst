package com.example.taxi_full.view.home.user;

import static android.os.SystemClock.sleep;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.MyLocationListener;
import com.example.taxi_full.API.env.Env;
import com.example.taxi_full.API.env.StyleCard;
import com.example.taxi_full.API.model.AdminDataPojo;
import com.example.taxi_full.API.model.RootCars;
import com.example.taxi_full.API.model.RootHomeWork;
import com.example.taxi_full.API.model.RootOrderOne;
import com.example.taxi_full.API.model.RootUserGeolocation;
import com.example.taxi_full.API.model.geocode.RootGeolocation;
import com.example.taxi_full.API.model.weather.RootWeather;
import com.example.taxi_full.R;
import com.example.taxi_full.databinding.ActivityMainBinding;
import com.example.taxi_full.view.fetures.Price.MathPrice;
import com.example.taxi_full.view.fetures.Toast.CustomToast;
import com.example.taxi_full.view.home.user.anim.AnimHome;
import com.example.taxi_full.view.home.user.bottom.Bottom;
import com.example.taxi_full.view.home.user.core.API.CoreAPI;
import com.example.taxi_full.view.home.user.core.maps.YMaps;
import com.example.taxi_full.view.home.user.permissions.Permission;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
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


public class HomeActivity extends AppCompatActivity implements UserLocationObjectListener, DrivingSession.DrivingRouteListener {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private MapView mapView;
    private UserLocationLayer userLocationLayer;
    private Point ROUTE_START_LOCATION, ROUTE_END_LOCATION = null;
    private MapObjectCollection mapObjects;
    public static final String CYRILLIC_TO_LATIN = "Cyrillic-Latin";
    private DBClass DBClass = new DBClass();
    private EditText start, finish, homeEdit, workEdit;
    private String finsh_string, start_string, distance, price, region = null;
    private WebSocketClient mWebSocketClient, mWebSocketClientButton;
    private final HashMap<String, HashMap<String, Object>> users = new HashMap<>();
    private HashMap<String, Integer> colorsCars = new HashMap<>();
    private DrivingSession drivingSession;
    private DrivingRouter drivingRouter;
    private PlacemarkMapObject point1, point2;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService executor2 = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService executor3 = Executors.newScheduledThreadPool(1);
    private int countUserLocation = 0;
    private final List<PolylineMapObject> routesCollection = new ArrayList<>();
    public List<RootUserGeolocation> posts = null;
    private Dialog dialog;
    final int DIALOG = 1;
    private boolean dialogOrder = false;
    private TextView eco, middle, business;
    private Button typeNal, typeOffNal;
    ImageView home, work, dollar_eco, dollar_middle, dollar_business;
    private boolean isPong;
    private String pr = "";
    private int Class, clasOrder = 1;
    private double smallRouteDistance;
    private final YMaps yMaps = new YMaps();
    private final CustomToast customToast = new CustomToast();
    private final AnimHome ah = new AnimHome();
    private FrameLayout loadFrame;
    private final CoreAPI core = new CoreAPI();
    private final Bottom bottom = new Bottom();
    private String weather, delivery, day;
    private MathPrice math = new MathPrice();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Permission permission = new Permission();
        permission.start(this, this);

        DirectionsFactory.initialize(this);

        Runnable getRegion = () -> {
            DBClass = new DBClass();
            String url = Env.URL_API_ORDERS + "/" + DBClass.getHash(this);
            try {
                if (!HttpApi.getId(url).equals("0")) {
                    RootOrderOne r = new Gson().fromJson(HttpApi.getId(url), RootOrderOne.class);
                    if(!r.getRegion().equals(""))
                        region = r.getRegion();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Thread reg = new Thread(getRegion);
        reg.start();
        try {
            reg.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_home);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapView = findViewById(R.id.mapview);
        mapView.getMap().setRotateGesturesEnabled(true);
        mapView.getMap().move(new CameraPosition(new Point(0, 0), 14, 0, 0));

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

        assign();

        int lightBlue = ContextCompat.getColor(this, R.color.lightBlue);
        typeOffNal.setBackgroundColor(Color.LTGRAY);

        core.start(this, this,
                dialogOrder, point1, point2, Class,
                executor, executor2, start, finish,
                eco, middle, business, typeNal, typeOffNal,
                mWebSocketClientButton, dollar_eco, dollar_middle, dollar_business);

        go();
        unGo();

        homeEdit = findViewById(R.id.home);
        workEdit = findViewById(R.id.work);

        bottom.start(start, finish, this, this,
                homeEdit, workEdit,
                point1, point2, Class, eco, middle, business,
                typeNal, typeOffNal, mWebSocketClientButton, lightBlue);

        ah.hide(HomeActivity.this, loadFrame);

        StartPointGeolocation();
        dragPoints();
        startEdit();
        finishEdit();
        startCheck();
        setPointsHomeWork();
        getTypePay();

    }

    private void getTypePay() {
        new Thread(() -> {
            DBClass = new DBClass();
            String hash = DBClass.getHash(HomeActivity.this);
            String url = Env.URL_API_ORDERS + "/" + hash;
            int lightBlue = ContextCompat.getColor(this, R.color.lightBlue);
            try {
                if (!HttpApi.getId(url).equals("0")) {
                    RootOrderOne r = new Gson().fromJson(HttpApi.getId(url), RootOrderOne.class);
                    if (r.getType_pay() != null) {
                        if (!r.getType_pay().equals(""))
                            if (r.getType_pay().equals("1")) {
                                runOnUiThread(() -> {
                                    typeNal.setBackgroundColor(lightBlue);
                                    typeOffNal.setBackgroundColor(Color.LTGRAY);
                                });
                            } else {
                                runOnUiThread(() -> {
                                    typeOffNal.setBackgroundColor(lightBlue);
                                    typeNal.setBackgroundColor(Color.LTGRAY);
                                });
                            }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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
        CoreAPI core = new CoreAPI();
        try {
            core.RedirectToDriverFirst(this, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pingPong("ping");
    }

    @Override
    protected void onDestroy() {
        MapKitFactory.getInstance().onStop();
        mapView.onStop();
        super.onDestroy();
    }

    private int getClasOrder() {
        String url_order = Env.URL_API_ORDERS + "/" + DBClass.getHash(this);
        Runnable run = () -> {
            try {
                if (!HttpApi.getId(url_order).equals("")) {
                    RootOrderOne r = new Gson().fromJson(HttpApi.getId(url_order), RootOrderOne.class);
                    if (r.get_class() != null) {
                        if (!r.get_class().equals(""))
                            clasOrder = Integer.parseInt(r.get_class());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Thread t = new Thread(run);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return clasOrder;
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {

        userLocationLayer.setAnchor(
                new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.5)),
                new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.83)));

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
        if (countUserLocation > 2) {
            if (userLocationLayer.isAnchorEnabled())
                userLocationLayer.resetAnchor();
        }
    }

    public final InputListener il = new InputListener() {
        @Override
        public void onMapTap(@NonNull Map map, @NonNull Point point) {
            DBClass = new DBClass();
            String hash = DBClass.getHash(HomeActivity.this);
            String url = Env.URL_API_ORDERS + "/" + hash;

            new Thread(() -> {
                Gson parser = new Gson();
                try {
                    if (HttpApi.getId(url).equals("0")) {
                        RootGeolocation rootGeoStart = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + point.getLongitude() + "," + point.getLatitude() + "&apikey=" + Env.GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                        try {
                            String start_stringUK = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription() + rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getName();
                            start_string = start_stringUK.replace("Украина", "");
                            region = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getMetaDataProperty().getGeocoderMetaData().getAddress().getComponents().get(2).getName();
                        } catch (Exception e) {
                            start_string = null;
                        }

                        String arr = "hash_user=" + hash + "&hash_driver=" + "&start=" + point.getLongitude() + "," + point.getLatitude() + "&start_string=" + start_string + "&region=" + region;

                        if (start_string != null && HttpApi.post(Env.URL_API_ORDERS, arr) == HttpURLConnection.HTTP_OK) {
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
                                            yMaps.submitRequest(HomeActivity.this, ROUTE_START_LOCATION, ROUTE_END_LOCATION, drivingRouter);
                                        else
                                            customToast.showToast(HomeActivity.this, HomeActivity.this, "Пожалуйста, поставте вторую точку!");

                                        new Thread(() -> {
                                            String start_str = null;
                                            RootGeolocation rootGeoStart = null;
                                            try {
                                                rootGeoStart = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + p1.getLongitude() + "," + p1.getLatitude() + "&apikey=" + Env.GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                String start_strUK = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription() + rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getName();
                                                start_str = start_strUK.replace("Украина", "");
                                                region = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getMetaDataProperty().getGeocoderMetaData().getAddress().getComponents().get(2).getName();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            while (true) {
                                                if (distance != null & price != null) {
                                                    String arg = "start=" + p1.getLongitude() + "," + p1.getLatitude() + "&start_string=" + start_str + "&price=" + price + "&distance=" + distance + "&region=" + region ;
                                                    if (start_str != null && HttpApi.put(Env.URL_API_DRAG + "/" + hash, arg) == HttpURLConnection.HTTP_OK) {
                                                        final String str = start_str;
                                                        EditText st = findViewById(R.id.start_bottom);
                                                        runOnUiThread(() -> {
                                                            st.setText(str);
                                                        });
                                                    } else
                                                        customToast.showToast(HomeActivity.this, HomeActivity.this, getString(R.string.unknown_error_message));

                                                    price = null;
                                                    distance = null;

                                                    return;
                                                }
                                            }
                                        }).start();
                                    }
                                });
                                String startSTR = start_string.replace("Россия", "");
                                startSTR = startSTR.replace("Украина", "");
                                String finalStartSTR = startSTR;
                                runOnUiThread(() -> start.setText(finalStartSTR));
                            });

                        } else
                            customToast.showToast(HomeActivity.this, HomeActivity.this, getString(R.string.unknown_error_message));

                    } else {
                        RootOrderOne root = parser.fromJson(HttpApi.getId(url), RootOrderOne.class);

                        if (root.getActive().equals("1")) {

                            if (root.getFinish().equals("")) {

                                runOnUiThread(() -> ah.start(HomeActivity.this, loadFrame));

                                RootGeolocation rootGeoStart = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + point.getLongitude() + "," + point.getLatitude() + "&apikey=" + Env.GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                                try {
                                    String finish_stringUK = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription() + rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getName();
                                    finsh_string = finish_stringUK.replace("Украина", "");
                                } catch (Exception e) {
                                    finsh_string = null;
                                }
                                ROUTE_END_LOCATION = point;
                                yMaps.submitRequest(HomeActivity.this, ROUTE_START_LOCATION, ROUTE_END_LOCATION, drivingRouter);
                                while (true) {
                                    if (distance != null & price != null) {
                                        String arg = "finish=" + point.getLongitude() + "," + point.getLatitude() + "&finish_string=" + finsh_string + "&price=" + price + "&distance=" + distance;
                                        if (finsh_string != null && HttpApi.put(url, arg) == HttpURLConnection.HTTP_OK) {
                                            runOnUiThread(() -> {
                                                runOnUiThread(() -> ah.hide(HomeActivity.this, loadFrame));
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
                                                            yMaps.submitRequest(HomeActivity.this, ROUTE_START_LOCATION, ROUTE_END_LOCATION, drivingRouter);
                                                        else
                                                            customToast.showToast(HomeActivity.this, HomeActivity.this, "Пожалуйста, поставте вторую точку");

                                                        new Thread(() -> {
                                                            String finish_str = null;
                                                            RootGeolocation rootGeoStart = null;
                                                            try {
                                                                rootGeoStart = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + p2.getLongitude() + "," + p2.getLatitude() + "&apikey=" + Env.GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                            try {
                                                                String finish_strUK = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription() + rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getName();
                                                                finish_str = finish_strUK.replace("Украина", "");
                                                            } catch (Exception e) {
                                                                finish_str = null;
                                                            }
                                                            while (true) {
                                                                if (distance != null & price != null) {
                                                                    String arg = "finish=" + p2.getLongitude() + "," + p2.getLatitude() + "&finish_string=" + finish_str + "&price=" + price + "&distance=" + distance;
                                                                    if (finish_str != null && HttpApi.put(Env.URL_API_ORDERS + "/" + hash, arg) == HttpURLConnection.HTTP_OK) {
                                                                        final String str = finish_str;
                                                                        EditText ft = findViewById(R.id.finish_bottom);
                                                                        runOnUiThread(() -> {
                                                                            ft.setText(str);
                                                                        });
                                                                    } else
                                                                        customToast.showToast(HomeActivity.this, HomeActivity.this, getString(R.string.unknown_error_message));
                                                                    price = null;
                                                                    distance = null;
                                                                    return;
                                                                }
                                                            }
                                                        }).start();
                                                    }
                                                });
                                                String startSTR = finsh_string.replace("Россия", "");
                                                startSTR = startSTR.replace("Украина", "");
                                                String finalStartSTR = startSTR;
                                                runOnUiThread(() -> finish.setText(finalStartSTR));
                                            });
                                        } else {
                                            runOnUiThread(() -> ah.hide(HomeActivity.this, loadFrame));
                                            customToast.showToast(HomeActivity.this, HomeActivity.this, getString(R.string.unknown_error_message));
                                        }
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

    public final MapObjectDragListener dragFinish = new MapObjectDragListener() {
        @Override
        public void onMapObjectDragStart(@NonNull MapObject mapObject) {

        }

        @Override
        public void onMapObjectDrag(@NonNull MapObject mapObject, @NonNull Point point) {

        }

        @Override
        public void onMapObjectDragEnd(@NonNull MapObject mapObject) {
            runOnUiThread(() -> ah.start(HomeActivity.this, loadFrame));
            DBClass db = new DBClass();
            String hash = db.getHash(HomeActivity.this);
            Point p2 = new Point(point2.getGeometry().getLatitude(), point2.getGeometry().getLongitude());
            ROUTE_END_LOCATION = p2;
            yMaps.submitRequest(HomeActivity.this, ROUTE_START_LOCATION, ROUTE_END_LOCATION, drivingRouter);
            new Thread(() -> {
                String finish_str = null;
                RootGeolocation rootGeoStart = null;
                try {
                    rootGeoStart = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + p2.getLongitude() + "," + p2.getLatitude() + "&apikey=" + Env.GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    String finish_strUK = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription() + rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getName();
                    finish_str = finish_strUK.replace("Украина", "");
                } catch (Exception e) {
                    finish_str = null;
                }
                while (true) {
                    if (distance != null & price != null) {
                        String arg = "finish=" + p2.getLongitude() + "," + p2.getLatitude() + "&finish_string=" + finish_str + "&price=" + price + "&distance=" + distance;
                        if (finish_str != null && HttpApi.put(Env.URL_API_ORDERS + "/" + hash, arg) == HttpURLConnection.HTTP_OK) {
                            final String str = finish_str;
                            EditText ft = findViewById(R.id.finish_bottom);
                            runOnUiThread(() -> {
                                ft.setText(str);
                                ah.hide(HomeActivity.this, loadFrame);

                            });
                        } else {
                            runOnUiThread(() -> {
                                ah.hide(HomeActivity.this, loadFrame);
                                customToast.showToast(HomeActivity.this, HomeActivity.this, getString(R.string.unknown_error_message));
                            });
                        }
                        price = null;
                        distance = null;
                        return;
                    }
                }
            }).start();
        }
    };

    public final MapObjectDragListener drag = new MapObjectDragListener() {
        @Override
        public void onMapObjectDragStart(@NonNull MapObject mapObject) {

        }

        @Override
        public void onMapObjectDrag(@NonNull MapObject mapObject, @NonNull Point point) {

        }

        @Override
        public void onMapObjectDragEnd(@NonNull MapObject mapObject) {
            runOnUiThread(() -> ah.start(HomeActivity.this, loadFrame));
            DBClass db = new DBClass();
            String hash = db.getHash(HomeActivity.this);
            Point p1 = new Point(point1.getGeometry().getLatitude(), point1.getGeometry().getLongitude());
            ROUTE_START_LOCATION = p1;
            if (ROUTE_END_LOCATION != null)
                yMaps.submitRequest(HomeActivity.this, ROUTE_START_LOCATION, ROUTE_END_LOCATION, drivingRouter);

            new Thread(() -> {
                String start_str = null;
                RootGeolocation rootGeoStart = null;
                try {
                    rootGeoStart = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + p1.getLongitude() + "," + p1.getLatitude() + "&apikey=" + Env.GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    String start_strUK = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription() + rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getName();
                    start_str = start_strUK.replace("Украина", "");
                    region = rootGeoStart.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getMetaDataProperty().getGeocoderMetaData().getAddress().getComponents().get(2).getName();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (true) {
                    if (distance != null & price != null) {
                        String arg = "start=" + p1.getLongitude() + "," + p1.getLatitude() + "&start_string=" + start_str + "&price=" + price + "&distance=" + distance + "&region=" + region;
                        if (start_str != null && HttpApi.put(Env.URL_API_DRAG + "/" + hash, arg) == HttpURLConnection.HTTP_OK) {
                            final String str = start_str;
                            EditText st = findViewById(R.id.start_bottom);
                            runOnUiThread(() -> {
                                st.setText(str);
                                ah.hide(HomeActivity.this, loadFrame);
                            });
                        } else {
                            runOnUiThread(() -> {
                                ah.hide(HomeActivity.this, loadFrame);
                                customToast.showToast(HomeActivity.this, HomeActivity.this, (getString(R.string.error_not_read_point)));
                            });
                        }
                        price = null;
                        distance = null;
                        return;
                    }
                }
            }).start();
        }
    };

    private void pingPong(String hash) {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mWebSocketClient != null) {
                    if (mWebSocketClient.getConnection().isOpen())
                        mWebSocketClient.send("{\"ping\" : \"" + hash + "\"}");
                }
                isPong = false;
                try {
                    Thread.sleep(2000);
                    if (!isPong) {
                        if (mWebSocketClient.getConnection().isOpen())
                            mWebSocketClient.close();
                        connectToSocket();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

            private boolean flag = false;

            @Override
            public void onMessage(String s) {
                if (s.equals("ponggeo")) {
                    isPong = true;
                } else {
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
                                        if (!HttpApi.getId(Env.URL_CARS + posts.get(i).getHash()).equals("0")) {
                                            RootCars cars = new Gson().fromJson(HttpApi.getId(Env.URL_CARS + posts.get(i).getHash()), RootCars.class);
                                            colorsCars.put(posts.get(i).getHash(), cars.getColor());
                                        }
                                    } catch (IOException e) {
                                        colorsCars = null;
                                    }
                                }
                            }
                        };
                        if (!s.equals("[[]]")) {
                            flag = true;
                            Thread thread = new Thread(getCars);
                            thread.start();
                            thread.join();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (flag) {
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

    public void connectToSocketButton() {
        URI uri;
        try {
            uri = new URI("ws" + "://" + "45.86.47.12:32000");
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
        try {
            mWebSocketClientButton.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> list) {
        smallRouteDistance = 0;
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

        Runnable getD = () -> {
            try {
                if (!HttpApi.getId(Env.URL_API_ADMIN + region + "_" + "0" + "_" + "0").equals("0")) {
                    AdminDataPojo data = new Gson().fromJson(HttpApi.getId(Env.URL_API_ADMIN + region + "_" + "0" + "_" + "0"), AdminDataPojo.class);
                    RootWeather rootWeather = new Gson().fromJson(HttpApi.getId("https://api.weatherapi.com/v1/current.json?key=" + Env.WEATHER_API_KEY + "&q=" + ROUTE_START_LOCATION.getLatitude() + "," + ROUTE_START_LOCATION.getLongitude() + "&aqi=no"), RootWeather.class);
                    pr = data.getPrice();
                    weather = data.getWeather();
                    delivery = data.getDelivery();
                    day = data.getDay();
                    if(rootWeather.getCurrent().getIsDay() == 1)
                        day = "0";
                    if(rootWeather.getCurrent().getPrecipMm() < 1.5)
                        weather = "0";
                } else {
                    pr = "50";
                    weather = "0";
                    delivery = "0";
                    day = "0";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(getD);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int mathPrice = 0;
        if (getClasOrder() == 1) {
            mathPrice = (int) Math.round(((smallRouteDistance * Integer.parseInt(pr)) / 1000));
            mathPrice = math.mathPrice(mathPrice, weather, delivery, day);
        } else if (getClasOrder() == 2) {
            mathPrice = (int) Math.round(((smallRouteDistance * Integer.parseInt(pr)) / 1000) * 1.5);
            mathPrice = math.mathPrice(mathPrice, weather, delivery, day);
        } else {
            mathPrice = (int) Math.round(((smallRouteDistance * Integer.parseInt(pr)) / 1000) * 2.5);
            mathPrice = math.mathPrice(mathPrice, weather, delivery, day);
        }
        price = String.valueOf(mathPrice);
        TextView e = findViewById(R.id.text_home);
        e.setText("Стоимость : " + price + "p");
        if (routesCollection != null && !routesCollection.isEmpty())
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

    }

    private void startEdit() {
        start.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                new Thread(() -> {
                    DBClass db = new DBClass();
                    try {
                        String hash = db.getHash(HomeActivity.this);
                        RootGeolocation rootGeolocation = null;
                        rootGeolocation = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + start.getText() + "&apikey=" + Env.GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                        region = rootGeolocation.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getMetaDataProperty().getGeocoderMetaData().getAddress().getComponents().get(2).getName();
                        ArrayList<String> pos = new ArrayList<>(Arrays.asList(rootGeolocation.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getPoint().getPos().split(" ")));
                        Point p1 = new Point(Double.parseDouble(pos.get(1)), Double.parseDouble(pos.get(0)));
                        ROUTE_START_LOCATION = p1;
                        if (ROUTE_END_LOCATION != null)
                            yMaps.submitRequest(HomeActivity.this, ROUTE_START_LOCATION, ROUTE_END_LOCATION, drivingRouter);
                        else {
                            String arr = "hash_user=" + hash + "&hash_driver=" + "&start=" + p1.getLongitude() + "," + p1.getLatitude() + "&start_string=" + start.getText() + "&region=" + region;
                            if (HttpApi.post(Env.URL_API_ORDERS, arr) == HttpURLConnection.HTTP_OK) {
                                runOnUiThread(() -> {
                                    point1 = mapObjects.addPlacemark(p1, ImageProvider.fromResource(HomeActivity.this, R.drawable.pin));
                                });
                                customToast.showToast(HomeActivity.this, HomeActivity.this, "Успех");
                                price = null;
                                distance = null;
                                return;
                            }
                        }
                        while (true) {
                            sleep(100);
                            if (distance != null && price != null && region != null) {
                                String arg = "start=" + p1.getLongitude() + "," + p1.getLatitude() + "&start_string=" + start.getText() + "&price=" + price + "&distance=" + distance + "&region=" + region;
                                if (HttpApi.put(Env.URL_API_DRAG + "/" + hash, arg) == HttpURLConnection.HTTP_OK) {
                                    runOnUiThread(() -> {
                                        if (point1 != null)
                                            point1.setGeometry(p1);
                                        else
                                            point1 = mapObjects.addPlacemark(p1, ImageProvider.fromResource(HomeActivity.this, R.drawable.pin));
                                    });
                                    customToast.showToast(HomeActivity.this, HomeActivity.this, "Успех");
                                } else
                                    customToast.showToast(HomeActivity.this, HomeActivity.this, getString(R.string.error_not_read_point));

                                price = null;
                                distance = null;
                                return;
                            }
                            sleep(100);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                handled = true;
            }
            return handled;
        });
    }

    private void finishEdit() {
        finish.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                new Thread(() -> {
                    DBClass db = new DBClass();
                    try {
                        String hash = db.getHash(HomeActivity.this);
                        RootGeolocation rootGeolocation = null;
                        rootGeolocation = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + finish.getText() + "&apikey=" + Env.GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                        ArrayList<String> pos = new ArrayList<>(Arrays.asList(rootGeolocation.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getPoint().getPos().split(" ")));
                        Point p2 = new Point(Double.parseDouble(pos.get(1)), Double.parseDouble(pos.get(0)));
                        ROUTE_END_LOCATION = p2;
                        yMaps.submitRequest(HomeActivity.this, ROUTE_START_LOCATION, ROUTE_END_LOCATION, drivingRouter);

                        while (true) {
                            sleep(100);
                            if (distance != null && price != null) {
                                String arg = "finish=" + p2.getLongitude() + "," + p2.getLatitude() + "&finish_string=" + finish.getText() + "&price=" + price + "&distance=" + distance;
                                if (HttpApi.put(Env.URL_API_ORDERS + "/" + hash, arg) == HttpURLConnection.HTTP_OK) {
                                    runOnUiThread(() -> {
                                        if (point2 != null)
                                            point2.setGeometry(p2);
                                        else
                                            point2 = mapObjects.addPlacemark(p2, ImageProvider.fromResource(HomeActivity.this, R.drawable.finish));
                                    });
                                    customToast.showToast(HomeActivity.this, HomeActivity.this, "Успех");
                                    price = null;
                                    distance = null;
                                    return;
                                } else
                                    customToast.showToast(HomeActivity.this, HomeActivity.this, getString(R.string.error_not_read_point));
                            }
                            sleep(100);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                InputMethodManager imm = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                handled = true;
            }
            return handled;
        });
    }

    private void dragPoints() {
        new Thread(() -> {
            DBClass db = new DBClass();
            String hash = db.getHash(HomeActivity.this);
            try {
                if (!HttpApi.getId(Env.URL_API_ORDERS + "/" + hash).equals("0")) {
                    RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(Env.URL_API_ORDERS + "/" + hash), RootOrderOne.class);
                    runOnUiThread(() -> {
                        if (rootOrderOne.getStart() != null) {
                            String startSTR = rootOrderOne.getStart_string().replace("Россия", "");
                            startSTR = startSTR.replace("Украина", "");
                            String finalStartSTR = startSTR;
                            runOnUiThread(() -> start.setText(finalStartSTR));
                            ArrayList<String> data = new ArrayList<>(Arrays.asList(rootOrderOne.getStart().split(",")));
                            Point start = new Point(Double.parseDouble(data.get(1)), Double.parseDouble(data.get(0)));
                            ROUTE_START_LOCATION = start;
                            point1 = mapObjects.addPlacemark(start, ImageProvider.fromResource(HomeActivity.this, R.drawable.pin));

                            if (rootOrderOne.getActive().equals("1")) {
                                point1.setDraggable(true);
                                point1.setDragListener(drag);
                            }
                        }
                        if (!rootOrderOne.getFinish().equals("")) {
                            String startSTR = rootOrderOne.getFinish_string().replace("Россия", "");
                            startSTR = startSTR.replace("Украина", "");
                            String finalStartSTR = startSTR;
                            runOnUiThread(() -> finish.setText(finalStartSTR));
                            ArrayList<String> dataEnd = new ArrayList<>(Arrays.asList(rootOrderOne.getFinish().split(",")));
                            Point finish = new Point(Double.parseDouble(dataEnd.get(1)), Double.parseDouble(dataEnd.get(0)));
                            ROUTE_END_LOCATION = finish;
                            point2 = mapObjects.addPlacemark(finish, ImageProvider.fromResource(HomeActivity.this, R.drawable.finish));
                            yMaps.submitRequest(HomeActivity.this, ROUTE_START_LOCATION, ROUTE_END_LOCATION, drivingRouter);
                            if (rootOrderOne.getActive().equals("1")) {
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


    private void StartPointGeolocation() {
        MyLocationListener.SetUpLocationListener(this);
        new Thread(() -> {
            DBClass db = new DBClass();
            String hash = db.getHash(HomeActivity.this);
            String start;
            RootGeolocation rootGeocoder = null;
            try {
                if (HttpApi.getId(Env.URL_API_ORDERS + "/" + hash).equals("0")) {
                    Point p = new Point(MyLocationListener.imHere.getLongitude(), MyLocationListener.imHere.getLatitude());
                    ROUTE_START_LOCATION = p;
                    rootGeocoder = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + ROUTE_START_LOCATION.getLatitude() + "," + ROUTE_START_LOCATION.getLongitude() + "&apikey=" + Env.GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                    try {
                        String startUK = rootGeocoder.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription() + rootGeocoder.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getName();
                        start = startUK.replace("Украина", "");
                        region = rootGeocoder.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getMetaDataProperty().getGeocoderMetaData().getAddress().getComponents().get(2).getName();
                    } catch (Exception e) {
                        start = null;
                    }
                    String arr = "hash_user=" + hash + "&hash_driver=" + "&start=" + ROUTE_START_LOCATION.getLatitude() + "," + ROUTE_START_LOCATION.getLongitude() + "&start_string=" + start + "&region=" + region;
                    if (start != null) {
                        HttpApi.post(Env.URL_API_ORDERS, arr);
                        runOnUiThread(() -> point1 = mapObjects.addPlacemark(new Point(ROUTE_START_LOCATION.getLongitude(), ROUTE_START_LOCATION.getLatitude()), ImageProvider.fromResource(HomeActivity.this, R.drawable.pin)));
                        String startSS = start.replace("Россия", "");
                        startSS = startSS.replace("Украина", "");
                        String finalStartSTR = startSS;
                        runOnUiThread(() -> this.start.setText(finalStartSTR));
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
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }


    private void startCheck() {
        new Thread(() -> {
            Bottom bottom = new Bottom();
            DBClass db = new DBClass();
            String arr = "hash=" + db.getHash(this);
            HttpApi.post(Env.URL_HOME_WORK, arr);
            bottom.loadHomeWorkText(homeEdit, workEdit, this, this);
        }).start();
    }

    private void setPointsHomeWork() {
        home = findViewById(R.id.homeButt);
        work = findViewById(R.id.workButt);
        EditText homeEdit = findViewById(R.id.home);
        EditText workEdit = findViewById(R.id.work);

        home.setOnClickListener(view -> new Thread(() -> {
            DBClass db = new DBClass();
            CoreAPI core = new CoreAPI();
            try {
                if (!homeEdit.getText().toString().equals("")) {
                    RootHomeWork rootHomeWork = new Gson().fromJson(HttpApi.getId(Env.URL_HOME_WORK + db.getHash(this)), RootHomeWork.class);
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(rootHomeWork.getPointHome().split(",")));
                    Point homeP = new Point(Double.parseDouble(data.get(1)), Double.parseDouble(data.get(0)));
                    ROUTE_END_LOCATION = homeP;
                    yMaps.submitRequest(HomeActivity.this, ROUTE_START_LOCATION, ROUTE_END_LOCATION, drivingRouter);

                    while (true) {
                        sleep(100);
                        if (distance != null && price != null) {
                            String arg = "finish=" + homeP.getLongitude() + "," + homeP.getLatitude() + "&finish_string=" + homeEdit.getText() + "&price=" + price + "&distance=" + distance;
                            if (HttpApi.put(Env.URL_API_ORDERS + "/" + db.getHash(this), arg) == HttpURLConnection.HTTP_OK) {
                                runOnUiThread(() -> {
                                    if (point2 != null)
                                        point2.setGeometry(homeP);
                                    else
                                        point2 = mapObjects.addPlacemark(homeP, ImageProvider.fromResource(HomeActivity.this, R.drawable.finish));
                                });
                                customToast.showToast(HomeActivity.this, HomeActivity.this, "Успех");
                                price = null;
                                distance = null;
                                core.finishLoad(this, this, finish);
                                return;
                            } else
                                customToast.showToast(HomeActivity.this, HomeActivity.this, getString(R.string.error_not_read_point));
                        }
                        sleep(100);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start());

        work.setOnClickListener(view -> new Thread(() -> {
            DBClass db = new DBClass();
            CoreAPI core = new CoreAPI();
            try {
                if (!workEdit.getText().toString().equals("")) {
                    RootHomeWork rootHomeWork = new Gson().fromJson(HttpApi.getId(Env.URL_HOME_WORK + db.getHash(this)), RootHomeWork.class);
                    ArrayList<String> data = new ArrayList<>(Arrays.asList(rootHomeWork.getPointWork().split(",")));
                    Point workP = new Point(Double.parseDouble(data.get(1)), Double.parseDouble(data.get(0)));
                    ROUTE_END_LOCATION = workP;
                    yMaps.submitRequest(HomeActivity.this, ROUTE_START_LOCATION, ROUTE_END_LOCATION, drivingRouter);

                    while (true) {
                        sleep(100);
                        if (distance != null && price != null) {
                            String arg = "finish=" + workP.getLongitude() + "," + workP.getLatitude() + "&finish_string=" + workEdit.getText() + "&price=" + price + "&distance=" + distance;
                            if (HttpApi.put(Env.URL_API_ORDERS + "/" + db.getHash(this), arg) == HttpURLConnection.HTTP_OK) {
                                runOnUiThread(() -> {
                                    if (point2 != null)
                                        point2.setGeometry(workP);
                                    else
                                        point2 = mapObjects.addPlacemark(workP, ImageProvider.fromResource(HomeActivity.this, R.drawable.finish));
                                });
                                customToast.showToast(HomeActivity.this, HomeActivity.this, "Успех");
                                price = null;
                                distance = null;
                                core.finishLoad(this, this, finish);
                                return;
                            } else
                                customToast.showToast(HomeActivity.this, HomeActivity.this, getString(R.string.error_not_read_point));
                        }
                        sleep(100);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start());
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG) {
            dialogOrder = true;
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Проверьте правильность заказа");
            adb.setMessage("Если все верно, нажмите \"Ок\" и снова на кнопку \"Заказать\"");
            adb.setPositiveButton("OK", null);
            dialog = adb.create();
            dialog.setOnShowListener(dialogInterface -> {
                Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(getApplicationContext().getResources().getColor(R.color.dark_gray));
                positiveButton.setTypeface(Typeface.DEFAULT_BOLD);
                positiveButton.invalidate();
            });

            // обработчик отмены
            dialog.setOnCancelListener(dialog -> {
            });

            // обработчик закрытия
            dialog.setOnDismissListener(dialog -> {
            });

            return dialog;
        }
        return super.onCreateDialog(id);
    }


    private void assign() {
        start = findViewById(R.id.start_bottom);
        finish = findViewById(R.id.finish_bottom);

        eco = findViewById(R.id.eco);
        middle = findViewById(R.id.middle);
        business = findViewById(R.id.business);
        dollar_eco = findViewById(R.id.dollar_eco);
        dollar_middle = findViewById(R.id.dollar_middle);
        dollar_business = findViewById(R.id.dollar_business);
        loadFrame = findViewById(R.id.loadId);

        typeNal = findViewById(R.id.type_home_nal);
        typeOffNal = findViewById(R.id.type_home_offnal);

        eco.setOnClickListener(view -> {
            Class = 1;
            TextView tv = (TextView) findViewById(R.id.eco);
            String s = String.valueOf(tv.getText());
            SpannableString ss = new SpannableString(s);
            ss.setSpan(new UnderlineSpan(), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(ss);
            TextView bus = findViewById(R.id.business);
            TextView mid = findViewById(R.id.middle);
            bus.setText("Бизнес");
            mid.setText("Стандарт");
            dollar_eco.setImageResource(R.drawable.dollar_black);
            dollar_middle.setImageResource(R.drawable.dollar);
            dollar_business.setImageResource(R.drawable.dollar);
            EditText cityEdit = findViewById(R.id.start_bottom);
            Runnable getD = () -> {
                try {
                    if (!HttpApi.getId(Env.URL_API_ADMIN + region + "_" + "0" + "_" + "0").equals("0")) {
                        AdminDataPojo data = new Gson().fromJson(HttpApi.getId(Env.URL_API_ADMIN + region + "_" + "0" + "_" + "0"), AdminDataPojo.class);
                        RootWeather rootWeather = new Gson().fromJson(HttpApi.getId("https://api.weatherapi.com/v1/current.json?key=" + Env.WEATHER_API_KEY + "&q=" + ROUTE_START_LOCATION.getLatitude() + "," + ROUTE_START_LOCATION.getLongitude() + "&aqi=no"), RootWeather.class);
                        pr = data.getPrice();
                        weather = data.getWeather();
                        delivery = data.getDelivery();
                        day = data.getDay();
                        if(rootWeather.getCurrent().getIsDay() == 1)
                            day = "0";
                        if(rootWeather.getCurrent().getPrecipMm() < 1.5)
                            weather = "0";
                    } else {
                        pr = "50";
                        weather = "0";
                        delivery = "0";
                        day = "0";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            Thread thread = new Thread(getD);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int mathPrice = (int) Math.round(((smallRouteDistance * Integer.parseInt(pr)) / 1000));
            mathPrice = math.mathPrice(mathPrice, weather, delivery, day);
            price = String.valueOf(mathPrice);
            TextView e = findViewById(R.id.text_home);
            e.setText("Стоимость : " + price + "p");
            new Thread(() -> HttpApi.put(Env.URL_API_HISTORY, "hash=" + DBClass.getHash(this) + "&class=1" + "&price=" + price)).start();
        });
        middle.setOnClickListener(view -> {
            Class = 2;
            dollar_middle.setImageResource(R.drawable.dollar_black);
            dollar_eco.setImageResource(R.drawable.dollar);
            dollar_business.setImageResource(R.drawable.dollar);
            TextView tv = (TextView) findViewById(R.id.middle);
            String s = (String) tv.getText();
            SpannableString ss = new SpannableString(s);
            ss.setSpan(new UnderlineSpan(), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(ss);
            TextView eco = findViewById(R.id.eco);
            TextView bus = findViewById(R.id.business);
            eco.setText("Эконом");
            bus.setText("Бизнес");
            EditText cityEdit = findViewById(R.id.start_bottom);
            String city = cityEdit.getText().toString().split(",", 2)[0];
            Runnable getD = () -> {
                try {
                    if (!HttpApi.getId(Env.URL_API_ADMIN + region + "_" + "0" + "_" + "0").equals("0")) {
                        AdminDataPojo data = new Gson().fromJson(HttpApi.getId(Env.URL_API_ADMIN + region + "_" + "0" + "_" + "0"), AdminDataPojo.class);
                        RootWeather rootWeather = new Gson().fromJson(HttpApi.getId("https://api.weatherapi.com/v1/current.json?key=" + Env.WEATHER_API_KEY + "&q=" + ROUTE_START_LOCATION.getLatitude() + "," + ROUTE_START_LOCATION.getLongitude() + "&aqi=no"), RootWeather.class);
                        pr = data.getPrice();
                        weather = data.getWeather();
                        delivery = data.getDelivery();
                        day = data.getDay();
                        if(rootWeather.getCurrent().getIsDay() == 1)
                            day = "0";
                        if(rootWeather.getCurrent().getPrecipMm() < 1.5)
                            weather = "0";
                    } else {
                        pr = "50";
                        weather = "0";
                        delivery = "0";
                        day = "0";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            Thread thread = new Thread(getD);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int mathPrice = (int) Math.round(((smallRouteDistance * Integer.parseInt(pr)) / 1000) * 1.5);
            mathPrice = math.mathPrice(mathPrice, weather, delivery, day);
            price = String.valueOf(mathPrice);
            TextView e = findViewById(R.id.text_home);
            e.setText("Стоимость : " + price + "p");
            new Thread(() -> HttpApi.put(Env.URL_API_HISTORY, "hash=" + DBClass.getHash(this) + "&class=2" + "&price=" + price)).start();

        });
        business.setOnClickListener(view -> {
            Class = 3;
            dollar_business.setImageResource(R.drawable.dollar_black);
            dollar_middle.setImageResource(R.drawable.dollar);
            dollar_eco.setImageResource(R.drawable.dollar);
            EditText cityEdit = findViewById(R.id.start_bottom);
            TextView tv = (TextView) findViewById(R.id.business);
            String s = tv.getText().toString();
            SpannableString ss = new SpannableString(s);
            ss.setSpan(new UnderlineSpan(), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(ss);
            TextView eco = findViewById(R.id.eco);
            TextView mid = findViewById(R.id.middle);
            eco.setText("Эконом");
            mid.setText("Стандарт");
            String city = cityEdit.getText().toString().split(",", 2)[0];
            Runnable getD = () -> {
                try {
                    if (!HttpApi.getId(Env.URL_API_ADMIN + region + "_" + "0" + "_" + "0").equals("0")) {
                        AdminDataPojo data = new Gson().fromJson(HttpApi.getId(Env.URL_API_ADMIN + region + "_" + "0" + "_" + "0"), AdminDataPojo.class);
                        RootWeather rootWeather = new Gson().fromJson(HttpApi.getId("https://api.weatherapi.com/v1/current.json?key=" + Env.WEATHER_API_KEY + "&q=" + ROUTE_START_LOCATION.getLatitude() + "," + ROUTE_START_LOCATION.getLongitude() + "&aqi=no"), RootWeather.class);
                        pr = data.getPrice();
                        weather = data.getWeather();
                        delivery = data.getDelivery();
                        day = data.getDay();
                        if(rootWeather.getCurrent().getIsDay() == 1)
                            day = "0";
                        if(rootWeather.getCurrent().getPrecipMm() < 1.5)
                            weather = "0";
                    } else {
                        pr = "50";
                        weather = "0";
                        delivery = "0";
                        day = "0";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            Thread thread = new Thread(getD);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int mathPrice = (int) Math.round(((smallRouteDistance * Integer.parseInt(pr)) / 1000) * 2.5);
            mathPrice = math.mathPrice(mathPrice, weather, delivery, day);
            price = String.valueOf(mathPrice);
            TextView e = findViewById(R.id.text_home);
            e.setText("Стоимость : " + price + "p");
            new Thread(() -> HttpApi.put(Env.URL_API_HISTORY, "hash=" + DBClass.getHash(this) + "&class=3" + "&price=" + price)).start();
        });
    }

    private void go() {
        Button go = findViewById(R.id.buttonGoSheet);
        Button goOff = findViewById(R.id.buttonGoSheetOff);
        Runnable flag = () -> {
            if (point2 != null) {
                runOnUiThread(() -> go.setOnClickListener(view -> {
                    if (dialogOrder) {
                        DBClass db = new DBClass();
                        String hash = db.getHash(HomeActivity.this);
                        String url = Env.URL_API_USERS + "/" + hash;
                        String arg = "active=2" + "&class=" + Class;
                        new Thread(() -> {
                            if (HttpApi.put(url, arg) == HttpURLConnection.HTTP_OK) {
                                customToast.showToast(this, this, "Ожидайте пока примут ваш заказ");
                                core.RedirectToDriver(this, this, executor);
                                connectToSocketButton();
                                mWebSocketClientButton.send("buttonOn");
                                mWebSocketClientButton.close();
                                runOnUiThread(() -> {
                                    if (point1 != null && point2 != null) {
                                        point1.setDraggable(false);
                                        point2.setDraggable(false);
                                    }
                                    go.setEnabled(false);
                                    bottom.blockEditOrder(this,
                                            start, finish,
                                            eco, middle, business,
                                            typeNal, typeOffNal);
                                    go.setVisibility(View.GONE);
                                    goOff.setVisibility(View.VISIBLE);
                                });
                            }
                        }).start();
                    } else
                        runOnUiThread(() -> showDialog(DIALOG));
                }));
                executor2.shutdown();
            }

        };


        executor.scheduleAtFixedRate(flag, 500, 500, TimeUnit.MILLISECONDS);
    }

    private void unGo() {
        Button go = findViewById(R.id.buttonGoSheet);
        Button goOff = findViewById(R.id.buttonGoSheetOff);
        goOff.setOnClickListener(view -> new Thread(() -> {
            DBClass db = new DBClass();
            String hash = db.getHash(HomeActivity.this);
            String url = Env.URL_API_USERS + "/" + hash;
            String arg = "active=1" + "&class=" + Class;
            if (HttpApi.put(url, arg) == HttpURLConnection.HTTP_OK) {
                connectToSocketButton();
                mWebSocketClientButton.send("buttonOn");
                mWebSocketClientButton.close();
                runOnUiThread(() -> {
                    if (point1 != null && point2 != null) {
                        point1.setDraggable(true);
                        point2.setDraggable(true);
                    }
                    go.setEnabled(true);
                    bottom.unBlockEditOrder(this,
                            start, finish,
                            eco, middle, business,
                            typeNal, typeOffNal);
                    go.setVisibility(View.VISIBLE);
                    goOff.setVisibility(View.GONE);
                });
            }
        }).start());
    }


}

