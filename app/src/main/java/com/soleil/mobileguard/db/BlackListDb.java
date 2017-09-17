package com.soleil.mobileguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BlackListDb extends SQLiteOpenHelper {
    public BlackListDb(Context context) {
        super(context, "black.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table blacktb(_id integer primary key autoincrement,phone text,mode integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table blacktb");
        onCreate(db);
    }
}
