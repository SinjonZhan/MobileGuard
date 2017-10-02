package com.soleil.mobileguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 程序锁的数据库
 */
public class LockDB extends SQLiteOpenHelper {
    public LockDB(Context context) {
        super(context, "lock.db", null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table locked(id integer primary key autoincrement, packname text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table locked");
        onCreate(db);
    }
}
