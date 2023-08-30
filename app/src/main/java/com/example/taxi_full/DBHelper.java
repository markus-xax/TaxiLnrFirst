package com.example.taxi_full;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "userValues";
    public static final String TABLE_USER_VALUES = "user";

    public static final String KEY_ID = "_id";
    public static final String KEY_DC = "DC";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_ACTIVE = "active";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_USER_VALUES + "(" + KEY_ID
                + " integer primary key," + KEY_DC + " integer," + KEY_TOKEN + " text," + KEY_ACTIVE + " integer " + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + TABLE_USER_VALUES);

        onCreate(db);
    }
}
