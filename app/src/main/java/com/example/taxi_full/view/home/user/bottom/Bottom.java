package com.example.taxi_full.view.home.user.bottom;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.MyLocationListener;
import com.example.taxi_full.API.env.Env;
import com.example.taxi_full.API.model.RootHomeWork;
import com.example.taxi_full.API.model.RootOrderOne;
import com.example.taxi_full.API.model.geocode.RootGeolocation;
import com.example.taxi_full.R;
import com.example.taxi_full.view.fetures.Toast.CustomToast;
import com.example.taxi_full.view.home.user.HomeActivity;
import com.google.gson.Gson;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.PlacemarkMapObject;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Bottom implements BottomSheet {

    @Override
    public void EditTextLocked(EditText start, EditText finish, Context context) {
        new Thread(() -> {
            DBClass db = new DBClass();
            String hash = db.getHash(context);
            try {
                if (!HttpApi.getId(Env.URL_API_ORDERS + "/" + hash).equals("0")) {
                    RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(Env.URL_API_ORDERS + "/" + hash), RootOrderOne.class);
                    if (rootOrderOne.getActive().equals("2")) {
                        start.setEnabled(false);
                        finish.setEnabled(false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void loadHomeWorkText(EditText home, EditText work, Context context, HomeActivity activity) {
        DBClass db = new DBClass();
        new Thread(() -> {
            if (checkHomeWork(context) != 0) {
                try {
                    RootHomeWork rootHomeWork = new Gson().fromJson(HttpApi.getId(Env.URL_HOME_WORK + db.getHash(context)), RootHomeWork.class);
                    activity.runOnUiThread(() -> {
                        home.setText(rootHomeWork.getHome());
                        work.setText(rootHomeWork.getWork());
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void addHomeWorkBottom(Context context, HomeActivity activity, EditText home, EditText work) {
        CustomToast customToast = new CustomToast();
        home.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                new Thread(() -> {
                    DBClass db = new DBClass();
                    try {
                        String hash = db.getHash(context);
                        RootGeolocation rootGeolocation = null;
                        rootGeolocation = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + home.getText() + "&apikey=" + Env.GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                        ArrayList<String> pos = new ArrayList<>(Arrays.asList(rootGeolocation.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getPoint().getPos().split(" ")));
                        Point p2 = new Point(Double.parseDouble(pos.get(1)), Double.parseDouble(pos.get(0)));

                        String arg = "home=" + home.getText() + "&pointHome=" + p2.getLongitude() + "," + p2.getLatitude() + "&hw=1";
                        if (HttpApi.put(Env.URL_HOME_WORK + hash, arg) == HttpURLConnection.HTTP_OK) {
                            customToast.showToast(activity, context, "Успех");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                InputMethodManager imm = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                if (imm != null)
                    imm.hideSoftInputFromWindow(activity.getWindow().getCurrentFocus().getWindowToken(), 0);
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
                        String hash = db.getHash(context);
                        RootGeolocation rootGeolocation = null;
                        rootGeolocation = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + work.getText() + "&apikey=" + Env.GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                        ArrayList<String> pos = new ArrayList<>(Arrays.asList(rootGeolocation.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getPoint().getPos().split(" ")));
                        Point p2 = new Point(Double.parseDouble(pos.get(1)), Double.parseDouble(pos.get(0)));

                        String arg = "work=" + work.getText() + "&pointWork=" + p2.getLongitude() + "," + p2.getLatitude() + "&hw=2";
                        if (HttpApi.put(Env.URL_HOME_WORK + hash, arg) == HttpURLConnection.HTTP_OK) {
                            customToast.showToast(activity, context, "Успех");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                InputMethodManager imm = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                if (imm != null)
                    imm.hideSoftInputFromWindow(activity.getWindow().getCurrentFocus().getWindowToken(), 0);
                handled = true;
            }
            return handled;
        });
    }

    @Override
    public int checkHomeWork(Context context) {
        DBClass db = new DBClass();
        String url = Env.URL_HOME_WORK + db.getHash(context);
        AtomicInteger check = new AtomicInteger();
        check.set(1);
        new Thread(() -> {
            try {
                if (HttpApi.getId(url).equals("0")) {
                    check.set(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        return check.get();
    }

    @Override
    public void setImageDollarClass(RootOrderOne rootOrderOne, HomeActivity activity,
                                    ImageView dollar_eco, ImageView dollar_middle, ImageView dollar_business) {
        if (rootOrderOne.get_class() != null) {
            switch (rootOrderOne.get_class()) {
                case "1": {
                    dollar_eco.setImageResource(R.drawable.dollar_black);
                    TextView tv = (TextView) activity.findViewById(R.id.eco);
                    String s = (String) tv.getText();
                    SpannableString ss = new SpannableString(s);
                    ss.setSpan(new UnderlineSpan(), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv.setText(ss);
                    TextView mid = activity.findViewById(R.id.middle);
                    TextView bus = activity.findViewById(R.id.business);
                    mid.setText("Стандарт");
                    bus.setText("Бизнес");
                    break;
                }
                case "2": {
                    dollar_middle.setImageResource(R.drawable.dollar_black);
                    TextView tv = (TextView) activity.findViewById(R.id.middle);
                    String s = (String) tv.getText();
                    SpannableString ss = new SpannableString(s);
                    ss.setSpan(new UnderlineSpan(), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv.setText(ss);
                    TextView eco = activity.findViewById(R.id.eco);
                    TextView bus = activity.findViewById(R.id.business);
                    eco.setText("Эконом");
                    bus.setText("Бизнес");
                    break;
                }
                case "3": {
                    dollar_business.setImageResource(R.drawable.dollar_black);
                    TextView tv = (TextView) activity.findViewById(R.id.business);
                    String s = (String) tv.getText();
                    SpannableString ss = new SpannableString(s);
                    ss.setSpan(new UnderlineSpan(), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv.setText(ss);
                    TextView eco = activity.findViewById(R.id.eco);
                    TextView mid = activity.findViewById(R.id.middle);
                    eco.setText("Эконом");
                    mid.setText("Стандарт");
                    break;
                }
            }
        } else
            dollar_eco.setImageResource(R.drawable.dollar_black);
    }

    @Override
    public void blockEditOrder(HomeActivity activity,
                               EditText start, EditText finish,
                               TextView eco, TextView middle, TextView business,
                               Button typeNal, Button typeOffNal) {
        Button go = activity.findViewById(R.id.buttonGoSheet);
        start.setEnabled(false);
        finish.setEnabled(false);
        eco.setEnabled(false);
        middle.setEnabled(false);
        business.setEnabled(false);
        typeNal.setEnabled(false);
        typeOffNal.setEnabled(false);
        typeNal.setEnabled(false);
        typeOffNal.setEnabled(false);
        go.setEnabled(false);
    }

    @Override
    public void unBlockEditOrder(HomeActivity activity,
                                 EditText start, EditText finish,
                                 TextView eco, TextView middle, TextView business,
                                 Button typeNal, Button typeOffNal) {
        Button go = activity.findViewById(R.id.buttonGoSheet);
        start.setEnabled(true);
        finish.setEnabled(true);
        eco.setEnabled(true);
        middle.setEnabled(true);
        business.setEnabled(true);
        typeNal.setEnabled(true);
        typeOffNal.setEnabled(true);
        typeNal.setEnabled(true);
        typeOffNal.setEnabled(true);
        go.setEnabled(true);
    }

    @Override
    public void isStartOrder(HomeActivity activity, Context context,
                             PlacemarkMapObject point1, PlacemarkMapObject point2,
                             int Class,
                             EditText start, EditText finish,
                             TextView eco, TextView middle, TextView business,
                             Button typeNal, Button typeOffNal,
                             WebSocketClient mWebSocketClientButton) {
        Button goOff = activity.findViewById(R.id.buttonGoSheetOff);
        new Thread(() -> {
            DBClass db = new DBClass();
            String hash = db.getHash(context);
            try {
                if (!HttpApi.getId(Env.URL_API_ORDERS + "/" + hash).equals("0")) {
                    RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(Env.URL_API_ORDERS + "/" + hash), RootOrderOne.class);
                    if (rootOrderOne.getActive().equals("2")) {
                        activity.runOnUiThread(() -> goOff.setVisibility(View.VISIBLE));
                        core.unGo(activity, context,
                                point1, point2,
                                Class,
                                start, finish,
                                eco, middle, business,
                                typeNal, typeOffNal,
                                mWebSocketClientButton);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void bottomEditStartFinishPoint(HomeActivity activity, Context context, EditText start, EditText finish) {
        MyLocationListener.SetUpLocationListener(context);
        DBClass DBClass = new DBClass();
        new Thread(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                String url_order = Env.URL_API_ORDERS + "/" + DBClass.getHash(context);
                RootGeolocation rootGeolocation = null;
                String startFinishString = null;
                RootOrderOne rootOrderOne = null;

                if (!HttpApi.getId(url_order).equals("0"))
                    rootOrderOne = new Gson().fromJson(HttpApi.getId(url_order), RootOrderOne.class);

                if(rootOrderOne == null || rootOrderOne.getStart_string().equals("") || rootOrderOne.getFinish_string().equals("")) {
                    rootGeolocation = new Gson().fromJson(HttpApi.getId("https://geocode-maps.yandex.ru/1.x/?geocode=" + MyLocationListener.imHere.getLongitude() + "," + MyLocationListener.imHere.getLatitude() + "&apikey=" + Env.GEOCODER_API_KEY + "&format=json&results=1&kind=house"), RootGeolocation.class);
                    try {
                        String startFinishStringUK = rootGeolocation.getResponse().getGeoObjectCollection().getFeatureMember().get(0).getGeoObject().getDescription();
                        startFinishString = startFinishStringUK.replace("Украина", "");
                        startFinishString = startFinishString.replace("Россия", "");
                    } catch (Exception ignored) {}
                }

                String finalStartFinishString = startFinishString;
                RootOrderOne finalRootOrderOne = rootOrderOne;
                activity.runOnUiThread(() -> {
                    if(finalRootOrderOne == null || finalRootOrderOne.getStart_string().equals(""))
                        if (finalStartFinishString != null)
                            activity.runOnUiThread(() -> start.setText(finalStartFinishString));
                    if(finalRootOrderOne == null || finalRootOrderOne.getFinish_string().equals(""))
                        if (finalStartFinishString != null)
                            activity.runOnUiThread(()->finish.setText(finalStartFinishString));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void typeNalOffnal(HomeActivity activity, Context context,
                              Button typeNal, Button typeOffNal, int lightBlue) {
        typeNal.setOnClickListener(view-> new Thread(()-> {
            HttpApi.put(Env.URL_API_ORDERS_TREE + DBClass.getHash(context), "type_pay=" + 1);
            activity.runOnUiThread(()->{
                typeNal.setBackgroundColor(lightBlue);
                typeOffNal.setBackgroundColor(Color.LTGRAY);
            });
        }).start());
        typeOffNal.setOnClickListener(view-> new Thread(()-> {
            HttpApi.put(Env.URL_API_ORDERS_TREE + DBClass.getHash(context), "type_pay=" + 2);
            activity.runOnUiThread(()->{
                typeOffNal.setBackgroundColor(lightBlue);
                typeNal.setBackgroundColor(Color.LTGRAY);
            });
        }).start());
    }


    @Override
    public void start(EditText start, EditText finish, Context context,
                      HomeActivity activity, EditText home, EditText work, PlacemarkMapObject point1, PlacemarkMapObject point2,
                      int Class, TextView eco, TextView middle, TextView business,
                      Button typeNal, Button typeOffNal,
                      WebSocketClient mWebSocketClientButton,
                      int lightBlue) {
        EditTextLocked(start, finish, context);
        bottomEditStartFinishPoint(activity, context, start, finish);
        addHomeWorkBottom(context, activity, home, work);
        isStartOrder(activity, context, point1, point2,
                Class, start, finish, eco, middle, business,
                typeNal, typeOffNal, mWebSocketClientButton);
        typeNalOffnal(activity, context, typeNal, typeOffNal, lightBlue);
    }
}
