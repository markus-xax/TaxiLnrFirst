package com.example.taxi_full.view.home.driver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
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
import com.example.taxi_full.API.model.RootAllOrders;
import com.example.taxi_full.API.model.RootCars;
import com.example.taxi_full.API.model.RootUserGeolocation;
import com.example.taxi_full.API.model.RootUserOne;
import com.example.taxi_full.R;
import com.example.taxi_full.databinding.ActivityHomeDriverBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
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
import com.yandex.runtime.image.ImageProvider;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeActivityDriver extends AppCompatActivity implements UserLocationObjectListener {

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeDriverBinding binding;
    private int finalCount_orders = 0;
    private MapView mapView;
    private UserLocationLayer userLocationLayer;
    private DBClass DBClass;
    private WebSocketClient mWebSocketClient;
    private final HashMap<String, HashMap<String, Object>> users = new HashMap<>();
    private MapObjectCollection mapObjects;
    private int countUserLocation = 0;
    private HashMap<String, Integer> colorsCars = new HashMap<>();
    private List<RootUserGeolocation> posts = null;
    private int cc = 0;
    private String formatter = "";
    private int incomingDriver;
    private int debtInd = 0;
    Dialog dialog;
    final int DIALOG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragmant_home_driver);

        binding = ActivityHomeDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestLocationPermission();
        MyLocationListener.SetUpLocationListener(this);

        mapView = findViewById(R.id.mapviewHomeDriver);
        mapView.getMap().setRotateGesturesEnabled(false);
        mapView.getMap().move(new CameraPosition(new Point(MyLocationListener.imHere.getLatitude(), MyLocationListener.imHere.getLongitude()), 14, 0, 0));

        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);


        userLocationLayer.setObjectListener(this);
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        //Стили!!!
        StyleCard.setMapStyle(mapView);

        setSupportActionBar(binding.appBarMainDriver.toolbar);
        DrawerLayout drawer = binding.drawerLayout2;
        NavigationView navigationView = binding.navView2;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home_driver, R.id.nav_profile_driver, R.id.nav_wallet ,R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_info, R.id.nav_exit)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_driver);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        ImageView menu_op = findViewById(R.id.op_menu);
        menu_op.setOnClickListener(v -> {
            drawer.openDrawer(GravityCompat.START);
        });

        connectToSocket();
        echoUserOrderText();
        try {
            Bundle b = getIntent().getExtras();
            if(b != null)
                debtInd = b.getInt("debtInd");
        } catch (Exception e){
            e.printStackTrace();
        }
        if (debtInd == 0) {
            redirectDebt();

        }
        new Thread(()->{
            DBClass db = new DBClass();
            try {
                if (HttpApi.getId(Env.URL_CARS + db.getHash(this)).equals("0")) {
                        runOnUiThread(()-> showDialog(DIALOG));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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

    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocationLayer.setAnchor(new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.5)), new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.83)));

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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        this.finish();
    }

    @SuppressLint("SetTextI18n")
    private void echoUserOrderText(){
        new Thread(()-> {
            String url = Env.URL_API_ORDER + "/";
            DBClass = new DBClass();
            String hash = DBClass.getHash(this);
            String url_user = Env.URL_API_USER +"/"+hash+"/"+DBClass.getDC(this);
            try {
                RootUserOne rootUserOne = new Gson().fromJson(HttpApi.getId(url_user), RootUserOne.class);
                if(!HttpApi.getId(url).equals("0")) {
                    Type listType = new TypeToken<List<RootAllOrders>>() {
                    }.getType();
                    List<RootAllOrders> orders = new Gson().fromJson(HttpApi.getId(url), listType);
                    int count_orders = 0;
                    LocalDateTime ldt = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        ldt = LocalDateTime.now();
                        DateTimeFormatter formmat1 = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
                        formatter = formmat1.format(ldt);
                    }
                    for (int j = 0; j < orders.size(); j++) {
                        if (orders.get(j).getHashDriver().equals(hash)) {
                            if(formatter.equals(orders.get(j).getDate()))
                                incomingDriver += Integer.parseInt(orders.get(j).getPrice());
                            count_orders++;
                        }
                    }
                    finalCount_orders = count_orders;
                }
                runOnUiThread(()->{
                    TextView name_surname_nav_header = findViewById(R.id.nameSurnameNHD);
                    TextView rate = findViewById(R.id.RateMenuDriver);
//                    TextView orders_count = findViewById(R.id.count_orders_header_driver);
//                    TextView incoming_day = findViewById(R.id.income_day_header_driver);
                    if(rootUserOne.getError().equals("") && rootUserOne.getNameSurname() != null) {
                        if(name_surname_nav_header != null)
                            name_surname_nav_header.setText(rootUserOne.getNameSurname());
                        if(rate != null) {
                            if (rootUserOne.getRate() == null || rootUserOne.getRate().equals(""))
                                rate.setText("5");
                            else
                                rate.setText(rootUserOne.getRate() + "+");
                        }
//                        orders_count.setText(String.valueOf(finalCount_orders));
//                        incoming_day.setText(String.valueOf(incomingDriver));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void connectToSocket() {
        URI uri;
        try {
            uri = new URI("ws://45.86.47.12:27800");
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
                        if(!s.equals("[[]]")) {
                            Thread thread = new Thread(getCars);
                            thread.start();
                            thread.join();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (posts != null) {
                        runOnUiThread(() -> {
                            for (int i = 0; i < posts.size(); i++) {
                                if (colorsCars != null) {
                                    if (!DBClass.getHash(HomeActivityDriver.this).equals(posts.get(i).getHash())) {
                                        if (DC.get(posts.get(i).getHash()) == null) {
                                            if (colorsCars.get(posts.get(i).getHash()) != null) {
                                                switch (colorsCars.get(posts.get(i).getHash())) {
                                                    case 1:
                                                        PlacemarkMapObject m = mapObjects.addPlacemark(new Point(Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Latitude"))), Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Longitude")))), ImageProvider.fromResource(HomeActivityDriver.this, R.drawable.car_white_small));
                                                        m.setIconStyle(new IconStyle().setRotationType(RotationType.ROTATE));
                                                        if(users.get(posts.get(i).getHash()).get("Duration") != null)
                                                            m.setDirection(Float.parseFloat(users.get(posts.get(i).getHash()).get("Duration").toString()));
                                                        else
                                                            m.setDirection(Float.parseFloat("0.00"));
                                                        DC.put(posts.get(i).getHash(), m);
                                                        break;
                                                    case 2:
                                                        PlacemarkMapObject mB = mapObjects.addPlacemark(new Point(Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Latitude"))), Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Longitude")))), ImageProvider.fromResource(HomeActivityDriver.this, R.drawable.car_black_small));
                                                        mB.setIconStyle(new IconStyle().setRotationType(RotationType.ROTATE));
                                                        if(users.get(posts.get(i).getHash()).get("Duration") != null)
                                                            mB.setDirection(Float.parseFloat(users.get(posts.get(i).getHash()).get("Duration").toString()));
                                                        else
                                                            mB.setDirection(Float.parseFloat("0.00"));
                                                        DC.put(posts.get(i).getHash(), mB);
                                                        break;
                                                    case 3:
                                                        PlacemarkMapObject mBl = mapObjects.addPlacemark(new Point(Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Latitude"))), Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Longitude")))), ImageProvider.fromResource(HomeActivityDriver.this, R.drawable.car_blue_small));
                                                        mBl.setIconStyle(new IconStyle().setRotationType(RotationType.ROTATE));
                                                        if(users.get(posts.get(i).getHash()).get("Duration") != null)
                                                            mBl.setDirection(Float.parseFloat(users.get(posts.get(i).getHash()).get("Duration").toString()));
                                                        else
                                                            mBl.setDirection(Float.parseFloat("0.00"));
                                                        DC.put(posts.get(i).getHash(), mBl);
                                                        break;
                                                    case 4:
                                                        PlacemarkMapObject mBG = mapObjects.addPlacemark(new Point(Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Latitude"))), Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Longitude")))), ImageProvider.fromResource(HomeActivityDriver.this, R.drawable.car_green_small));
                                                        mBG.setIconStyle(new IconStyle().setRotationType(RotationType.ROTATE));
                                                        if(users.get(posts.get(i).getHash()).get("Duration") != null)
                                                            mBG.setDirection(Float.parseFloat(users.get(posts.get(i).getHash()).get("Duration").toString()));
                                                        else
                                                            mBG.setDirection(Float.parseFloat("0.00"));
                                                        DC.put(posts.get(i).getHash(), mBG);
                                                        break;
                                                    case 5:
                                                        PlacemarkMapObject mR = mapObjects.addPlacemark(new Point(Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Latitude"))), Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Longitude")))), ImageProvider.fromResource(HomeActivityDriver.this, R.drawable.car_red_small));
                                                        mR.setIconStyle(new IconStyle().setRotationType(RotationType.ROTATE));
                                                        if(users.get(posts.get(i).getHash()).get("Duration") != null)
                                                            mR.setDirection(Float.parseFloat(users.get(posts.get(i).getHash()).get("Duration").toString()));
                                                        else
                                                            mR.setDirection(Float.parseFloat("0.00"));
                                                        DC.put(posts.get(i).getHash(), mR);
                                                        break;
                                                    case 6:
                                                        PlacemarkMapObject mY = mapObjects.addPlacemark(new Point(Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Latitude"))), Double.parseDouble(String.valueOf(users.get(posts.get(i).getHash()).get("Longitude")))), ImageProvider.fromResource(HomeActivityDriver.this, R.drawable.car_yellow_small));
                                                        mY.setIconStyle(new IconStyle().setRotationType(RotationType.ROTATE));
                                                        if(users.get(posts.get(i).getHash()).get("Duration") != null)
                                                            mY.setDirection(Float.parseFloat(users.get(posts.get(i).getHash()).get("Duration").toString()));
                                                        else
                                                            mY.setDirection(Float.parseFloat("0.00"));
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

    private void redirectDebt(){
        new Thread(()->{
            DBClass db = new DBClass();
            try{
                if(!HttpApi.getId(Env.URL_DEBT + "/" + db.getHash(this)).equals("[]")) {
                    startActivity(new Intent("com.example.taxi_full.Debt"));
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Добавьте автомобиль");
            adb.setMessage("Для этого перейдите в раздел \"профиль\" ->" +
                    " \"нажать на изображение автомобиля\"");
            adb.setPositiveButton("OK", null);
            dialog = adb.create();
            dialog.setOnShowListener(dialogInterface -> {
                Button positiveButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);
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
}
