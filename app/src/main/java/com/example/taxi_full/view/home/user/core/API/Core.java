package com.example.taxi_full.view.home.user.core.API;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.view.fetures.Toast.CustomToast;
import com.example.taxi_full.view.home.user.HomeActivity;
import com.example.taxi_full.view.home.user.bottom.Bottom;
import com.yandex.mapkit.map.PlacemarkMapObject;

import org.java_websocket.client.WebSocketClient;

import java.util.concurrent.ScheduledExecutorService;

public interface Core {

    int DIALOG = 1;

    DBClass DBClass = new DBClass();
    Bottom bottom = new Bottom();
    CustomToast toast = new CustomToast();


    void go(HomeActivity activity, Context context,
            boolean dialogOrder,
            PlacemarkMapObject point1, PlacemarkMapObject point2,
            int Class,
            final ScheduledExecutorService executor, final ScheduledExecutorService executor2,
            EditText start, EditText finish,
            TextView eco, TextView middle, TextView business,
            Button typeNal, Button typeOffNal,
            WebSocketClient mWebSocketClientButton);

    void unGo(HomeActivity activity, Context context,
              PlacemarkMapObject point1, PlacemarkMapObject point2,
              int Class,
              EditText start, EditText finish,
              TextView eco, TextView middle, TextView business,
              Button typeNal, Button typeOffNal,
              WebSocketClient mWebSocketClientButton);

    void finishLoad(HomeActivity activity, Context context, EditText finish);

    void getClassOrder(HomeActivity activity, Context context,
                       EditText start, EditText finish,
                       TextView eco, TextView middle, TextView business,
                       Button typeNal, Button typeOffNal,
                       ImageView dollar_eco, ImageView dollar_middle, ImageView dollar_business);

    void echoTextMenu(HomeActivity activity, Context context);

    void RedirectToDriver(HomeActivity activity, Context context, ScheduledExecutorService executor);

    void RedirectToDriverFirst(HomeActivity activity, Context context);

    void start(HomeActivity activity, Context context,
               boolean dialogOrder,
               PlacemarkMapObject point1, PlacemarkMapObject point2,
               int Class,
               final ScheduledExecutorService executor, final ScheduledExecutorService executor2,
               EditText start, EditText finish,
               TextView eco, TextView middle, TextView business,
               Button typeNal, Button typeOffNal,
               WebSocketClient mWebSocketClientButton,
               ImageView dollar_eco, ImageView dollar_middle, ImageView dollar_business);
}
