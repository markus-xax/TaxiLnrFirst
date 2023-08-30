package com.example.taxi_full.ui.home;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.taxi_full.API.AdaptorOrders;
import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.MyLocationListener;
import com.example.taxi_full.API.model.RootAllOrders;
import com.example.taxi_full.API.model.RootCars;
import com.example.taxi_full.API.model.RootOrderOne;
import com.example.taxi_full.R;
import com.example.taxi_full.databinding.FragmantHomeDriverBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeFragmentDriver extends Fragment {

    private FragmantHomeDriverBinding binding;
    public ListView list;
    public String[][] data;
    public int[] dataImg;
    public final String URL_API = "http://45.86.47.12/api/orders";
    public final String URL_API_CAR = "http://45.86.47.12/api/cars/";
    public DBClass DBClass = new DBClass();
    private WebSocketClient mWebSocketClient;
    private WebSocketClient mWebSocketClientGeo;
    java.util.Map <String,Object> geo = new HashMap<>();
    int iter = 0;
    TextView countOrders;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double mX, mY, mZ;
    private final double mOffset = 0.1;
    Sensor defaultGyroscope;
    private float gyroscope = 0f;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmantHomeDriverBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        list = root.findViewById(R.id.listView);

        connectToSocket();
        connectToSocketGeolocation();
        MyLocationListener.SetUpLocationListener(getContext());

        DBClass = new DBClass();
        String hash = DBClass.getHash(root.getContext());
        String url = URL_API + "/";
        countOrders = root.findViewById(R.id.count);


        new Thread(() -> {
            try {
                if(!HttpApi.getId(url).equals("0")) {
                    if (!HttpApi.getId(URL_API_CAR + hash).equals("0")) {
                        Type listType = new TypeToken<List<RootAllOrders>>() {
                        }.getType();
                        List<RootAllOrders> orders = new Gson().fromJson(HttpApi.getId(url), listType);
                        RootCars rootCars = new Gson().fromJson(HttpApi.getId(URL_API_CAR + hash), RootCars.class);
                        int count = 0;
                        for (int j = 0; j < orders.size(); j++) {
                            if (orders.get(j).getClassOrder().equals(rootCars.getClassCar()))
                                count++;
                        }
                        String orC = "У вас " + count + " новых запросов";
                        getActivity().runOnUiThread(() -> countOrders.setText(orC));
                        data = new String[count][6];
                        dataImg = new int[count];
                        for (int i = 0; i < count; i++) {
                            if (orders.get(i).getClassOrder().equals(rootCars.getClassCar())) {
                                data[i][0] = orders.get(i).getNameUser();
                                data[i][1] = orders.get(i).getDistance();
                                data[i][2] = orders.get(i).getPrice();
                                data[i][3] = orders.get(i).getStart_string();
                                data[i][4] = orders.get(i).getFinish_string();
                                data[i][5] = orders.get(i).getType_pay();
                                dataImg[i] = R.drawable.profile_man;
                            }
                        }
                    }
                }
                getActivity().runOnUiThread(() -> list.setAdapter(new AdaptorOrders(root.getContext(), data, dataImg)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        FirstRedirectDriver();

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        defaultGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        getGeolocation();
    }

    public void getGeolocation(){
        DBClass = new DBClass();
        String hash = DBClass.getHash(getContext());
        String DC = DBClass.getDC(getContext());
        Runnable geoListener = new Runnable() {
            double Long = 0;
            double Latitude = 0;
            @Override
            public void run() {
                if(iter == 0){
                    iter++;
                    double lat = MyLocationListener.imHere.getLatitude();  // широта
                    double lon = MyLocationListener.imHere.getLongitude(); // долгота
                    geo.put("Longitude", lon);
                    geo.put("Latitude", lat);
                    geo.put("duration", gyroscope);
                    geo.put("Hash", hash);
                    geo.put("DC", DC);
                    Long = lon;
                    Latitude = lat;
                    JSONObject jsonGeometry = new JSONObject(geo);
                    SendGeolocation(jsonGeometry);
                } else {
                    if(MyLocationListener.imHere.getLongitude() != Long && MyLocationListener.imHere.getLatitude() != Latitude){
                        double lat = MyLocationListener.imHere.getLatitude();  // широта
                        double lon = MyLocationListener.imHere.getLongitude(); // долгота
                        geo.put("Longitude", lon);
                        geo.put("Latitude", lat);
                        geo.put("duration", gyroscope);
                        geo.put("Hash", hash);
                        geo.put("DC", DC);
                        Long = lon;
                        Latitude = lat;
                        JSONObject jsonGeometry = new JSONObject(geo);
                        SendGeolocation(jsonGeometry);
                    }
                }
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(geoListener, 0, 1, TimeUnit.SECONDS);


    }

    @Override
    public void onStop() {
        DBClass = new DBClass();
        String hash = DBClass.getHash(getContext());
        java.util.Map <String, String> hashMap = new HashMap<>();
        hashMap.put("Hash", hash);
        hashMap.put("OC", "Close");
        JSONObject jsonGeometry = new JSONObject(hashMap);
        if(mWebSocketClient != null)
            SendHash(jsonGeometry);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        DBClass = new DBClass();
        String hash = DBClass.getHash(getContext());
        java.util.Map <String, String> hashMap = new HashMap<>();
        hashMap.put("Hash", hash);
        hashMap.put("OC", "Close");
        JSONObject jsonGeometry = new JSONObject(hashMap);
        if(mWebSocketClient != null)
            SendHash(jsonGeometry);
        super.onDestroy();
    }

    private void connectToSocket() {
        URI uri;
        try {
            uri = new URI("ws"+"://"+"45.86.47.12:27810");
        } catch (URISyntaxException e) {
            Log.d("----uri------",e.getMessage());
            return;
        }
        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("Websocket", "Opened");
            }
            @Override
            public void onMessage(String s) {
                Log.d("--mes--", s);
                try {
                    socketCars(s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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

    private void connectToSocketGeolocation(){
        URI uri;
        try {
            uri = new URI("ws"+"://"+"45.86.47.12:27800");
        } catch (URISyntaxException e) {
            Log.d("----uri------",e.getMessage());
            return;
        }
        mWebSocketClientGeo = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("WebsocketGeo", "Opened");
                DBClass = new DBClass();
                String hash = DBClass.getHash(getContext());
                java.util.Map <String, String> hashMap = new HashMap<>();
                hashMap.put("Hash", hash);
                hashMap.put("OC", "Open");
                JSONObject jsonGeometry = new JSONObject(hashMap);
                SendHash(jsonGeometry);

            }
            @Override
            public void onMessage(String s) {}
            @Override
            public void onClose(int i, String s, boolean b) {
                Log.d("WebsocketGeo", "Closed " + s);

            }
            @Override
            public void onError(Exception e) {
                Log.d("WebsocketGeo", "Error " + e.getMessage());
            }
        };
        mWebSocketClientGeo.connect();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void SendHash(JSONObject hash){
        try{
            mWebSocketClientGeo.send(String.valueOf(hash));
        } catch (NotYetConnectedException e){
            Log.d("---send---", e.getMessage());
        }
    }

    private void SendGeolocation(JSONObject geolocation){
        try{
            mWebSocketClientGeo.send(String.valueOf(geolocation));
        } catch (NotYetConnectedException e){
            Log.d("---send---", e.getMessage());
        }
    }

    private void FirstRedirectDriver(){
        String url_order = URL_API+"/"+DBClass.getHash(getContext());
        new Thread(() -> {
            RootOrderOne r = null;
            try {
                if(!HttpApi.getId(url_order).equals("0"))
                    r = new Gson().fromJson(HttpApi.getId(url_order), RootOrderOne.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(r != null && r.getActive().equals("3")) {
                startActivity(new Intent("com.example.taxi_full.GoDriver"));
            }
        }).start();

    }

    private void socketCars(String s) throws InterruptedException {
        Runnable getOrders = () -> {
            try {
                if(data != null & dataImg != null) {
                    Arrays.fill(data, null);
                    Arrays.fill(dataImg, 0);
                }
                DBClass db = new DBClass();
                String hash = db.getHash(getContext());
                if (!HttpApi.getId(URL_API_CAR + hash).equals("0")) {
                    Type listType = new TypeToken<List<RootAllOrders>>() {
                    }.getType();
                    List<RootAllOrders> orders = new Gson().fromJson(s, listType);
                    RootCars rootCars = new Gson().fromJson(HttpApi.getId(URL_API_CAR + hash), RootCars.class);
                    int count = 0;
                    for (int j = 0; j < orders.size(); j++) {
                        if (orders.get(j).getClassOrder().equals(rootCars.getClassCar()))
                            count++;
                    }
                    String orC = "У вас " + count + " новых запросов";
                    data = new String[count][6];
                    dataImg = new int[count];
                    getActivity().runOnUiThread(() -> countOrders.setText(orC));
                    for (int i = 0; i < count; i++) {
                        if (orders.get(i).getClassOrder().equals(rootCars.getClassCar())) {
                            data[i][0] = orders.get(i).getNameUser();
                            data[i][1] = orders.get(i).getDistance();
                            data[i][2] = orders.get(i).getPrice();
                            data[i][3] = orders.get(i).getStart_string();
                            data[i][4] = orders.get(i).getFinish_string();
                            data[i][5] = orders.get(i).getType_pay();
                            dataImg[i] = R.drawable.profile_man;
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("error", e.getMessage());
            }
        };
        Thread thread = new Thread(getOrders);
        thread.start();
        thread.join();

        list.setAdapter(new AdaptorOrders(binding.getRoot().getContext(), data, dataImg));
    }

    private SensorEventListener gyroscopeSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            gyroscope = sensorEvent.values[0];
            float[] rotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(
                    rotationMatrix, sensorEvent.values);
            // Remap coordinate system
            float[] remappedRotationMatrix = new float[16];
            SensorManager.remapCoordinateSystem(rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    remappedRotationMatrix);
            // Convert to orientations
            float[] orientations = new float[3];
            SensorManager.getOrientation(remappedRotationMatrix, orientations);
            for(int i = 0; i < 3; i++) {
                orientations[i] = (float)(Math.toDegrees(orientations[i]));
            }
            gyroscope = orientations[1];
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(gyroscopeSensorListener, defaultGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(gyroscopeSensorListener);
    }
}
