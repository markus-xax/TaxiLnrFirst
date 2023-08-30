package com.example.taxi_full;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taxi_full.API.HttpApi;
import com.example.taxi_full.API.Regx;

import java.net.HttpURLConnection;

public class RegistrationActivity extends AppCompatActivity {

    Cursor userCursor;
    SQLiteDatabase db;
    int DC;
    String hash;
    DBHelper dbHelper;

    public final String URL_API = "http://45.86.47.12/api/users";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sing_up);

        dbHelper = new DBHelper(this);

    }

    public void reg(View view) {
        EditText phone = (EditText) findViewById(R.id.editTextPhone);
        EditText email = (EditText) findViewById(R.id.editTextTextEmailAddress);
        EditText name = (EditText) findViewById(R.id.editTextTextPersonName);
        EditText surname = (EditText) findViewById(R.id.editTextTextPersonSurname);

        if (Regx.EmailRegx(email.getText().toString()) && Regx.PhoneRegx(phone.getText().toString())) {

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

            cursor.close();
            db.close();

            String arr = "name=" + name.getText().toString() + "&email=" + email.getText().toString() + "&surname=" + surname.getText().toString() + "&driver=" + DC + "&phone=" + phone.getText().toString() + "&hash=" + hash;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (HttpApi.post(URL_API, arr) == HttpURLConnection.HTTP_OK) {
                        startActivity(new Intent("com.example.taxi_full.Login"));
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Пользоватлель с таким номером телефона уже существует!", Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "Неправильно введены номер телефона или email!", Toast.LENGTH_LONG).show();
        }
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
}