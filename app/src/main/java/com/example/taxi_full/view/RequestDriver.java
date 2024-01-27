package com.example.taxi_full.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.model.RootOrderOne;
import com.example.taxi_full.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;

public class RequestDriver extends AppCompatActivity {
    private final String URL_API = "http://45.86.47.12/api/rate/";
    private final String URL_API_USERS = "http://45.86.47.12/api/users";
    private final String URL_API_ORDERS_TREE = "http://45.86.47.12/api/ordersThree";
    private final String URL_API_TIME = "http://45.86.47.12/api/time";
    private RootOrderOne r;
    private String hash;
    private DBClass dbClass = new DBClass();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_driver);

        hash = dbClass.getHash(this);

        Button request = findViewById(R.id.save_request_driver);

        RatingBar ratingBar = findViewById(R.id.ratingBar);

        new Thread(() -> {
            try {
                r = new Gson().fromJson(HttpApi.getId(URL_API_ORDERS_TREE+"/"+hash), RootOrderOne.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        request.setOnClickListener(view -> {
            if(ratingBar.getRating() != 0) {
                String arg_active = "active=4";
                String arg = "driver=1&rate=" + ratingBar.getRating();
                String url = URL_API_USERS + "/" + hash;
                String urlTime = URL_API_TIME + "/" + hash;

                new Thread(() -> {
                    if (HttpApi.put(url, arg_active) == HttpURLConnection.HTTP_OK) {
                        if (HttpApi.put(URL_API + r.getHash_user(), arg) == HttpURLConnection.HTTP_OK) {
                            if(HttpApi.put(urlTime, "") == HttpURLConnection.HTTP_OK)
                                startActivity(new Intent("com.example.taxi_full.HomeDriver"));
                        }
                    }
                }).start();
            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
