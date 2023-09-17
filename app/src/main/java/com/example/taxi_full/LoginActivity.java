package com.example.taxi_full;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.Regx;
import com.example.taxi_full.API.model.RootUserOne;
import com.google.gson.Gson;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private final String reg = "Регистрация";
    private final String auth = "Продолжить";
    private String URL_API = "http://45.86.47.12/api/users/";
    private String URL_API_USER = "http://45.86.47.12/api/user/";
    private DBHelper dbHelper;
    SQLiteDatabase db;
    int DC;
    String hash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        EditText editText = (EditText)findViewById(R.id.loginPhone);
        Button button = (Button)findViewById(R.id.button);
        dbHelper = new DBHelper(this);


        DBClass dbClass = new DBClass();
        if(!dbClass.getHash(this).equals("0 rows"))
            OAuth();

        Button reg = findViewById(R.id.buttonReg);
        reg.setOnClickListener(view-> startActivity(new Intent("com.example.taxi_full.Registration")));
    }

    public void RegAuth(View view) {

        Button button = (Button) findViewById(R.id.button);
        EditText phoneN = (EditText) findViewById(R.id.loginPhone);

        if (Regx.PhoneRegx(phoneN.getText().toString())) {


            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(DBHelper.TABLE_USER_VALUES, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                int IdIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                int DcIndex = cursor.getColumnIndex(DBHelper.KEY_DC);
                int HashIndex = cursor.getColumnIndex(DBHelper.KEY_TOKEN);
                int ActiveIndex = cursor.getColumnIndex(DBHelper.KEY_ACTIVE);
                do {
                    if (cursor.getInt(ActiveIndex) == 1) {
                        DC = cursor.getInt(DcIndex);
                        hash = cursor.getString(HashIndex);
                    }
                } while (cursor.moveToNext());
            } else
                Log.d("mLog", "0 rows");
            DBClass dbClass = new DBClass();


            String url = URL_API + phoneN.getText().toString() + "/" + dbClass.getDC(LoginActivity.this);
            new Thread(() -> {
                try {
                    RootUserOne root = null;
                    root = new Gson().fromJson(HttpApi.getId(url), RootUserOne.class);
                    if (root != null && (phoneN.getText().toString()).equals(root.getPhoneTrim())) {
                        startActivity(new Intent("com.example.taxi_full.SMS_Code"));
                    }
                } catch (IOException e) {
                    Log.d("IOE-ex", e.getMessage());
                }

            }).start();
        } else {
            Toast.makeText(this, "Неправильно введен номер телефона!", Toast.LENGTH_LONG).show();
        }
    }


    /*
    *
    * Невозможно сделать быструю авторизацию без подтверждения по смс
    *
    * */

    private void FastAuth(){
        DBClass dbClass = new DBClass();
        String DC = dbClass.getDC(this);
        String hash = dbClass.getHash(this);

        EditText phoneN = (EditText) findViewById(R.id.loginPhone);

        String url = URL_API + phoneN.getText().toString() + "/" + DC;
        new Thread(() -> {
            try {
                RootUserOne root = new Gson().fromJson(HttpApi.getId(url), RootUserOne.class);
                if ((phoneN.getText().toString()).equals(root.getPhoneTrim()) && hash.equals(root.getHash())) {
                    startActivity(new Intent("com.example.taxi_full.Home"));
                }
            } catch (IOException e) {
                Log.d("IOE-ex", e.getMessage());
            }

        }).start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    private void OAuth() {
        DBClass dbClass = new DBClass();
        EditText phoneN = (EditText) findViewById(R.id.loginPhone);
        String url = URL_API_USER + dbClass.getHash(this) + "/" + dbClass.getDC(this);
        new Thread(()->{
            try {
                if (!HttpApi.getId(url).equals("0")){
                    RootUserOne rootUserOne = new Gson().fromJson(HttpApi.getId(url), RootUserOne.class);
                    runOnUiThread(()-> phoneN.setText(rootUserOne.getPhoneTrim()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
