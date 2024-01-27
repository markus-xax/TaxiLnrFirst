package com.example.taxi_full.view.debt;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.model.RootDebt;
import com.example.taxi_full.R;
import com.example.taxi_full.view.home.driver.HomeActivityDriver;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Debt extends AppCompatActivity {
    private final String URL_DEBT = "http://45.86.47.12/api/debt";
    private String debtPay = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debt);
        debt();
        ImageButton exit = findViewById(R.id.backDebt);

        exit.setOnClickListener(view -> {
            Intent i = new Intent(this, HomeActivityDriver.class);
            i.putExtra("debtInd", 1);
            startActivity(i);
        });

        try {
            payDebt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void debt(){
        new Thread(()->{
            try {
                TextView debtT = findViewById(R.id.debt);
                DBClass db = new DBClass();
                Type listType = new TypeToken<List<RootDebt>>() {
                }.getType();
                List<RootDebt> debt = new Gson().fromJson(HttpApi.getId(URL_DEBT + "/" + db.getHash(this)), listType);
                double d = 0;
                for (int i = 0; i < debt.size(); i++) {
                    d += Double.parseDouble(debt.get(i).getDebt());
                }
                double finalD = d;
                int debtInteger = (int)finalD;
                debtPay = String.valueOf(finalD);
                runOnUiThread(()->debtT.setText(debtInteger +"р"));
            }catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    private void payDebt() throws InterruptedException {
        DBClass db = new DBClass();
        String hash = db.getHash(this);
        WebView debt = findViewById(R.id.debtWeb);
        debt.getSettings().setJavaScriptEnabled(true);
        while (true){
            if(!debtPay.equals("")) {
                debt.loadUrl("https://45.86.47.12/pay.phtml?hash=" + hash + " d" + "&debt=" + debtPay);
                return;
            }
            sleep(100);
        }

    }
}
