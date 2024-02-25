package com.example.taxi_full.view.auth.login.sms;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.DBHelper;
import com.example.taxi_full.R;

public class SMSActivity extends AppCompatActivity {
    public DBClass dbClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_enter_sms_code);
        dbClass = new DBClass();
        String DC = dbClass.getDC(SMSActivity.this);
    }

    public void auth(View view){
        //код для проверки
        dbClass = new DBClass();
        String DC = dbClass.getDC(SMSActivity.this);
        if(DC.equals("1")) {
            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            Cursor cursor = database.query(DBHelper.TABLE_USER_VALUES, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                int dcIndex = cursor.getColumnIndex(DBHelper.KEY_DC);
                do {
                    int dc = cursor.getInt(dcIndex);
                    if (dc == 1) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBHelper.KEY_ACTIVE_SMS, 1);
                        database.update(DBHelper.TABLE_USER_VALUES, contentValues, DBHelper.KEY_DC + " = ?", new String[]{"1"});
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
            database.close();
            startActivity(new Intent("com.example.taxi_full.HomeDriver"));
        }
        else {
            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            Cursor cursor = database.query(DBHelper.TABLE_USER_VALUES, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                int dcIndex = cursor.getColumnIndex(DBHelper.KEY_DC);
                do {
                    int dc = cursor.getInt(dcIndex);
                    if (dc == 0) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBHelper.KEY_ACTIVE_SMS, 1);
                        database.update(DBHelper.TABLE_USER_VALUES, contentValues, DBHelper.KEY_DC + " = ?", new String[]{"0"});
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
            database.close();
            startActivity(new Intent("com.example.taxi_full.Home"));
        }
    }
}
