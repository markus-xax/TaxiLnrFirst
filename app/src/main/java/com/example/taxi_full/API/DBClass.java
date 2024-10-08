package com.example.taxi_full.API;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBClass {

    public Cursor userCursor;
    public SQLiteDatabase db;
    private int DC;
    private String hash;
    private int active;

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

    public int getActiveSMS(Context context) {
        DBHelper dbHelper = new DBHelper(context);

        db = dbHelper.getWritableDatabase();

        userCursor = db.query(DBHelper.TABLE_USER_VALUES, null, null, null, null, null, null);
        if (userCursor.moveToFirst()) {
            int ActiveIndex = userCursor.getColumnIndex(DBHelper.KEY_ACTIVE);
            int ActiveSMSIndex = userCursor.getColumnIndex(DBHelper.KEY_ACTIVE_SMS);
            do {
                if (userCursor.getInt(ActiveIndex) == 1) {
                    active = userCursor.getInt(ActiveSMSIndex);
                }
            } while (userCursor.moveToNext());
        } else
            return -1;

        userCursor.close();
        db.close();

        return active;
    }
}
