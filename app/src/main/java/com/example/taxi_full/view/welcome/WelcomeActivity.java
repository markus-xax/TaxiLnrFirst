package com.example.taxi_full.view.welcome;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.R;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcone_screen);

        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    DBClass dbClass = new DBClass();
                    int logoTimer = 0;
                    while (logoTimer < 500) {
                        sleep(100);
                        logoTimer = logoTimer + 100;
                    }

                    if(dbClass.getActiveSMS(WelcomeActivity.this) == 1) {
                        if(dbClass.getDC(WelcomeActivity.this).equals("1"))
                            startActivity(new Intent("com.example.taxi_full.HomeDriver"));
                        else if (dbClass.getDC(WelcomeActivity.this).equals("0"))
                            startActivity(new Intent("com.example.taxi_full.Home"));
                    } else
                        startActivity(new Intent("com.example.taxi_full.Driver_client"));

//                    if(dbClass.getDC(WelcomeActivity.this).equals("1")) {
//                        Log.d("дата", "1");
//                        if(dbClass.getActiveSMS(WelcomeActivity.this) == 1) {
//                            Log.d("дата", "1.1");
//                            startActivity(new Intent("com.example.taxi_full.HomeDriver"));
//                        } else
//                            startActivity(new Intent("com.example.taxi_full.SMS_Code"));
//                    } else if (dbClass.getDC(WelcomeActivity.this).equals("0")) {
//                        Log.d("дата", "0");
//                        if(dbClass.getActiveSMS(WelcomeActivity.this) == 1) {
//                            Log.d("дата", "0.1");
//                            startActivity(new Intent("com.example.taxi_full.Home"));
//                        } else
//                            startActivity(new Intent("com.example.taxi_full.SMS_Code"));
//                    } else {
//                        Log.d("дата", "2");
//                        startActivity(new Intent("com.example.taxi_full.Driver_client"));
//                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        };
        logoTimer.start();
    }
}
