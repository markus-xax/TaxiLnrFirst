package com.example.taxi_full.view.home.user.core.API;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.env.Env;
import com.example.taxi_full.API.model.RootOrderOne;
import com.example.taxi_full.API.model.RootUserOne;
import com.example.taxi_full.R;
import com.example.taxi_full.view.home.user.HomeActivity;
import com.google.gson.Gson;
import com.yandex.mapkit.map.PlacemarkMapObject;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CoreAPI implements Core {

    @Override
    public void go(HomeActivity activity, Context context,
                   boolean dialogOrder,
                   PlacemarkMapObject point1, PlacemarkMapObject point2,
                   int Class,
                   final ScheduledExecutorService executor, final ScheduledExecutorService executor2,
                   EditText start, EditText finish,
                   TextView eco, TextView middle, TextView business,
                   Button typeNal, Button typeOffNal,
                   WebSocketClient mWebSocketClientButton) {

        Button go = activity.findViewById(R.id.buttonGoSheet);
        Button goOff = activity.findViewById(R.id.buttonGoSheetOff);
        Runnable flag = () -> {
            if (point2 != null) {
                activity.runOnUiThread(() -> go.setOnClickListener(view -> {
                    if (dialogOrder) {
                        DBClass db = new DBClass();
                        String hash = db.getHash(context);
                        String url = Env.URL_API_USERS + "/" + hash;
                        String arg = "active=2" + "&class=" + Class;
                        new Thread(() -> {
                            if (HttpApi.put(url, arg) == HttpURLConnection.HTTP_OK) {
                                toast.showToast(activity, context, "Ожидайте пока примут ваш заказ");
                                RedirectToDriver(activity, context, executor);

                                // сокеты решение
                                activity.connectToSocketButton();
                                HomeActivity.mWebSocketClientButton.send("buttonOn");
                                HomeActivity.mWebSocketClientButton.close();

                                activity.runOnUiThread(() -> {
                                    if (point1 != null) {
                                        point1.setDraggable(false);
                                        point2.setDraggable(false);
                                    }
                                    go.setEnabled(false);

                                    bottom.blockEditOrder(activity,
                                            start, finish,
                                            eco, middle, business,
                                            typeNal, typeOffNal);

                                    go.setVisibility(View.GONE);
                                    goOff.setVisibility(View.VISIBLE);
                                });
                            }
                        }).start();
                    } else
                        activity.runOnUiThread(() -> activity.showDialog(DIALOG));
                }));
                executor2.shutdown();
            }

        };


        executor.scheduleAtFixedRate(flag, 0, 500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void unGo(HomeActivity activity, Context context,
                     PlacemarkMapObject point1, PlacemarkMapObject point2,
                     int Class,
                     EditText start, EditText finish,
                     TextView eco, TextView middle, TextView business,
                     Button typeNal, Button typeOffNal,
                     WebSocketClient mWebSocketClientButton) {
        Button go = activity.findViewById(R.id.buttonGoSheet);
        Button goOff = activity.findViewById(R.id.buttonGoSheetOff);
        goOff.setOnClickListener(view -> new Thread(() -> {
            DBClass db = new DBClass();
            String hash = db.getHash(context);
            String url = Env.URL_API_USERS + "/" + hash;
            String arg = "active=1" + "&class=" + Class;
            if (HttpApi.put(url, arg) == HttpURLConnection.HTTP_OK) {

                activity.connectToSocketButton();
                HomeActivity.mWebSocketClientButton.send("buttonOn");
                HomeActivity.mWebSocketClientButton.close();

                activity.runOnUiThread(() -> {
                    if (point1 != null && point2 != null) {
                        point1.setDraggable(true);
                        point2.setDraggable(true);
                    }
                    go.setEnabled(true);
                    bottom.unBlockEditOrder(activity,
                            start, finish,
                            eco, middle, business,
                            typeNal, typeOffNal);
                    go.setVisibility(View.VISIBLE);
                    goOff.setVisibility(View.GONE);
                });
            }
        }).start());
    }

    @Override
    public void finishLoad(HomeActivity activity, Context context, EditText finish) {
        new Thread(() -> {
            DBClass db = new DBClass();
            try {
                RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(Env.URL_API_ORDERS + "/" + db.getHash(context)), RootOrderOne.class);
                String startSTR = rootOrderOne.getFinish_string().replace("Россия", "");
                startSTR = startSTR.replace("Украина", "");
                String finalStartSTR = startSTR;
                activity.runOnUiThread(() -> finish.setText(finalStartSTR));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void getClassOrder(HomeActivity activity, Context context,
                              EditText start, EditText finish,
                              TextView eco, TextView middle, TextView business,
                              Button typeNal, Button typeOffNal,
                              ImageView dollar_eco, ImageView dollar_middle, ImageView dollar_business) {
        new Thread(() -> {
            DBClass db = new DBClass();
            String hash = db.getHash(context);
            try {
                if (!HttpApi.getId(Env.URL_API_ORDERS + "/" + hash).equals("0")) {
                    RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(Env.URL_API_ORDERS + "/" + hash), RootOrderOne.class);
                    if (rootOrderOne.getActive().equals("2")) {
                        activity.runOnUiThread(() -> {
                            bottom.blockEditOrder(activity,
                                    start, finish,
                                    eco, middle, business,
                                    typeNal, typeOffNal);
                            bottom.setImageDollarClass(rootOrderOne, activity,
                                    dollar_eco, dollar_middle, dollar_business);
                        });
                    }
                    if (rootOrderOne.getActive().equals("1")) {
                        if (rootOrderOne.get_class() != null)
                            activity.runOnUiThread(() -> bottom.setImageDollarClass(rootOrderOne, activity,
                                    dollar_eco, dollar_middle, dollar_business));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void echoTextMenu(HomeActivity activity, Context context) {
        new Thread(() -> {
            DBClass dbClass = new DBClass();
            String hash = dbClass.getHash(context);
            String url = Env.URL_API_USER + "/" + hash + "/" + dbClass.getDC(context);
            try {
                RootUserOne rootUserOne = new Gson().fromJson(HttpApi.getId(url), RootUserOne.class);
                activity.runOnUiThread(() -> {
                    if (rootUserOne.getError() != null && rootUserOne.getError().equals("")) {
                        TextView name_surname_menu = activity.findViewById(R.id.menuNameSurname);
                        TextView rate_menu = activity.findViewById(R.id.menuRate);
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

    @Override
    public void RedirectToDriver(HomeActivity activity, Context context, ScheduledExecutorService executor) {
        String url_order = Env.URL_API_ORDERS + "/" + DBClass.getHash(context);
        Runnable geoListener = () -> {
            try {
                RootOrderOne r = new Gson().fromJson(HttpApi.getId(url_order), RootOrderOne.class);
                if (r.getActive().equals("3") || r.getActive().equals("4") || r.getActive().equals("5")) {
                    activity.startActivity(new Intent("com.example.taxi_full.GoUser"));
                    executor.shutdown();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };


        executor.scheduleAtFixedRate(geoListener, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void RedirectToDriverFirst(HomeActivity activity, Context context) {
        String url_order = Env.URL_API_ORDERS + "/" + DBClass.getHash(context);
        new Thread(() -> {
            RootOrderOne r = null;
            try {
                if (!HttpApi.getId(url_order).equals("0")) {
                    r = new Gson().fromJson(HttpApi.getId(url_order), RootOrderOne.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (r != null && (r.getActive().equals("3") || r.getActive().equals("4") || r.getActive().equals("5"))) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activity.startActivity(new Intent("com.example.taxi_full.GoUser"));
            }
        }).start();
    }

    @Override
    public void start(HomeActivity activity, Context context,
                      boolean dialogOrder,
                      PlacemarkMapObject point1, PlacemarkMapObject point2,
                      int Class,
                      final ScheduledExecutorService executor, final ScheduledExecutorService executor2,
                      EditText start, EditText finish,
                      TextView eco, TextView middle, TextView business,
                      Button typeNal, Button typeOffNal,
                      WebSocketClient mWebSocketClientButton,
                      ImageView dollar_eco, ImageView dollar_middle, ImageView dollar_business) {
        echoTextMenu(activity, context);
        getClassOrder(activity, context,
                start, finish, eco, middle, business,
                typeNal, typeOffNal, dollar_eco, dollar_middle, dollar_business);
    }
}
