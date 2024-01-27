package com.example.taxi_full.API;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.taxi_full.view.DBHelper;

public class DBClass {

    public Cursor userCursor;
    public SQLiteDatabase db;
    private int DC;
    private String hash;

    public String getHash(Context context){
        DBHelper dbHelper = new DBHelper(context);

        db = dbHelper.getWritableDatabase();

        userCursor = db.query(DBHelper.TABLE_USER_VALUES, null, null, null, null, null, null);
        if (userCursor.moveToFirst()) {
            int IdIndex = userCursor.getColumnIndex(DBHelper.KEY_ID);
            int DcIndex = userCursor.getColumnIndex(DBHelper.KEY_DC);
            int HashIndex = userCursor.getColumnIndex(DBHelper.KEY_TOKEN);
            int ActiveIndex = userCursor.getColumnIndex(DBHelper.KEY_ACTIVE);
            do {
                if (userCursor.getInt(ActiveIndex) == 1) {
                    DC = userCursor.getInt(DcIndex);
                    hash = userCursor.getString(HashIndex);
                }
            } while (userCursor.moveToNext());
        } else
           return "0 rows";

        userCursor.close();
        db.close();

        return hash;
    }

    public String getDC(Context context){
        DBHelper dbHelper = new DBHelper(context);

        db = dbHelper.getWritableDatabase();

        userCursor = db.query(DBHelper.TABLE_USER_VALUES, null, null, null, null, null, null);
        if (userCursor.moveToFirst()) {
            int IdIndex = userCursor.getColumnIndex(DBHelper.KEY_ID);
            int DcIndex = userCursor.getColumnIndex(DBHelper.KEY_DC);
            int HashIndex = userCursor.getColumnIndex(DBHelper.KEY_TOKEN);
            int ActiveIndex = userCursor.getColumnIndex(DBHelper.KEY_ACTIVE);
            do {
                if (userCursor.getInt(ActiveIndex) == 1) {
                    DC = userCursor.getInt(DcIndex);
                    hash = userCursor.getString(HashIndex);
                }
            } while (userCursor.moveToNext());
        } else
            return "0 rows";

        userCursor.close();
        db.close();

        return DC+"";
    }
}
