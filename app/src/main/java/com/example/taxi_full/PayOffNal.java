package com.example.taxi_full;

import static java.lang.Thread.sleep;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.model.RootOrderOne;
import com.google.gson.Gson;

import java.io.IOException;

public class PayOffNal extends AppCompatActivity {
    private String Pay = "";

    private final String URL_API = "http://45.86.47.12/api/orders";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_off_nal);
        DBClass dbClass = new DBClass();

        new Thread(() -> {
            String hash = dbClass.getHash(this);
            String urlO = URL_API +"/"+hash;
            try {
                RootOrderOne rootOrderOne = new Gson().fromJson(HttpApi.getId(urlO), RootOrderOne.class);
                Pay = rootOrderOne.getPrice();
            } catch (IOException e) {e.printStackTrace();}
        }).start();

        try {
            pay();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    private void pay() throws InterruptedException {
        DBClass dbClass = new DBClass();
        String hash = dbClass.getHash(this);
        WebView web = findViewById(R.id.web_pay);
        web.getSettings().setJavaScriptEnabled(true);
        while (true){
            if(!Pay.equals("")) {
                web.loadUrl("https://45.86.47.12/pay.phtml?hash=" + hash + " p" + "&debt=" + Pay);
                return;
            }
            sleep(100);
        }

    }
}
