package com.soleil.mobileguard.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.soleil.mobileguard.db.LockDB;
import com.soleil.mobileguard.domain.LockedTable;

import java.util.ArrayList;
import java.util.List;

/**
 * 程序锁的数据存取层
 */
public class LockDao {
    private LockDB lockDB;

    private Context context;
    public LockDao(Context context) {
        lockDB = new LockDB(context);
        this.context = context;
    }

    public boolean isLocked(String packName) {
        boolean isLocked = false;
        SQLiteDatabase database = lockDB.getReadableDatabase();
        Cursor cursor = database.rawQuery("select 1 from " + LockedTable.TABLENAME + " where " + LockedTable.PACKNAME + "=?", new String[]{packName});
        if (cursor.moveToNext()) {


            isLocked = true;
        } else {

            isLocked = false;
        }
        cursor.close();
        database.close();
        return isLocked;
    }

    /**
     * 加程序锁
     *
     * @param packName 要加锁的包名
     */
    public void add(String packName) {
        SQLiteDatabase database = lockDB.getWritableDatabase();
        //表中列值得封装
        ContentValues values = new ContentValues();
        values.put(LockedTable.PACKNAME, packName);
        //插入
        database.insert(LockedTable.TABLENAME, null, values);
        database.close();

        context.getContentResolver().notifyChange(LockedTable.uri, null);

    }

    /**
     * 移除程序锁
     *
     * @param packname 解锁的app包名
     */
    public void remove(String packname) {
        SQLiteDatabase database = lockDB.getWritableDatabase();
        //删除
        database.delete(LockedTable.TABLENAME, LockedTable.PACKNAME + "=?", new String[]{packname});
        database.close();

        //发送内容观察者的通知
        context.getContentResolver().notifyChange(LockedTable.uri, null);
    }

    /**
     * 获取所有的加锁的app的包名
     */
    public List<String> getAllLockedDatas() {
        List<String> lockedNames = new ArrayList<>();
        SQLiteDatabase database = lockDB.getReadableDatabase();
        //取出所有包名
        Cursor cursor = database.rawQuery("select " + LockedTable.PACKNAME + " from " + LockedTable.TABLENAME, null);
        while (cursor.moveToNext()) {
            lockedNames.add(cursor.getString(0));
        }
        cursor.close();
        database.close();
        return lockedNames;
    }


}
