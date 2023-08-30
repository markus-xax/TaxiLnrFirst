package com.example.taxi_full;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taxi_full.API.DBClass;

public class SMSActivity extends AppCompatActivity {
    public DBClass dbClass;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_enter_sms_code);

    }

    public void auth(View view){
        //код для проверки
        dbClass = new DBClass();
        String DC = dbClass.getDC(SMSActivity.this);
        if(DC.equals("1"))
            startActivity(new Intent("com.example.taxi_full.HomeDriver"));
            else
        startActivity(new Intent("com.example.taxi_full.Home"));
    }
}
