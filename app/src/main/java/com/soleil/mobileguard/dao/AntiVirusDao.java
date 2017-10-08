package com.soleil.mobileguard.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntiVirusDao {
    /**
     * @param md5  病毒文件的md5
     * @param desc 病毒文件的描述信息
     */
    public static void addVirus(String md5, String desc) {
        SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/data/com.soleil.mobileguard/files/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("md5", md5);
        values.put("type", 6);
        values.put("name", "Android.Hack.CarrierIQ.a");
        values.put("desc", desc);
        database.insert("datable", null, values);
        database.close();
    }

    /**
     * @param md5 文件的md5
     *
     * @return 是否是病毒文件
     *
     */
    public static boolean isVirus(String md5) {
        boolean res = false;
        SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/data/com.soleil.mobileguard/files/antivirus.db", null, SQLiteDatabase.OPEN_READWRITE);
        Cursor cursor = database.rawQuery("select 1 from datable where md5=?", new String[]{md5});
        if (cursor.moveToNext()) {
            res = true;
        }
        cursor.close();
        database.close();

        return res;
    }
}
