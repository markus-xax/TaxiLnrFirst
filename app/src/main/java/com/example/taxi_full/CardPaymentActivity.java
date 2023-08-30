package com.example.taxi_full;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.model.RootAllWallet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.List;

public class CardPaymentActivity extends AppCompatActivity {
    private final String URL_API_P2P = "https://45.86.47.12/telegram.php";
    private final String URL_API = "http://45.86.47.12/api/wallet";
    private int c = 0;
    private int nn = 0;
    private final DBClass dbClass = new DBClass();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_number_activity);
        Button reject = findViewById(R.id.reject);
        EditText card_number = findViewById(R.id.card_number);
        ImageButton back = findViewById(R.id.backRejectDriver);

        back.setOnClickListener(view -> startActivity(new Intent("com.example.taxi_full.HomeDriver")));
        reject.setOnClickListener(view -> {
            String arg = null;
            try {
                if(getMoney() == 0)
                arg = "money=" + "error" + "&card=" + card_number.getText();
                else
                    arg = "money=" + nn + "&card=" + card_number.getText();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String finalArg = arg;
            new Thread(() -> {
                DBClass dbClass = new DBClass();
                HttpApi.post(URL_API_P2P, finalArg);
                if(HttpApi.put(URL_API +  "/" + dbClass.getHash(this), "active=0") == HttpURLConnection.HTTP_OK)
                   runOnUiThread(() -> Toast.makeText(this, "Перевод поступит в течении 10 минут", Toast.LENGTH_LONG).show());
            }).start();
        });
    }

    private int getMoney() throws InterruptedException {
        t.start();
        t.join();
        if(nn == 0)
            return 0;
        return nn;
    }

    Thread t = new Thread(
            () -> {
                DBClass db = new DBClass();
                String hash = db.getHash(getApplicationContext());
                String url = URL_API + "/" + hash;
                try {
                    Type listType = new TypeToken<List<RootAllWallet>>() {}.getType();
                    List<RootAllWallet> wallet = new Gson().fromJson(HttpApi.getId(url), listType);
                    String[][] data = new String[wallet.size()][3];
                    c = wallet.size();
                    double allInc = 0;
                    double nnInc = 0;
                    for(int i = 0; i< wallet.size(); i++) {
                            if (wallet.get(i).getMethood().equals("1"))
                                data[i][0] = "Наличный расчет";
                            else
                                data[i][0] = "Безналичный расчет";
                            data[i][1] = wallet.get(i).getInForTakeOff();
                            allInc += Double.parseDouble(wallet.get(i).getInForTakeOff());
                            if (wallet.get(i).getMethood().equals("2"))
                                nnInc += Double.parseDouble(wallet.get(i).getInForTakeOff());

                    }
                    double finalAllInc = allInc;
                    double finalNnInc = nnInc;
                    int allIncInt = (int)finalAllInc;
                    final int nnIncInt = (int)finalNnInc;
                    nn = nnIncInt;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    );
}
