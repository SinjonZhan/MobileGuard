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

    public LockDao(Context context) {
        lockDB = new LockDB(context);
    }

    /**
     * 加程序锁
     * @param packName
     * 要加锁的包名
     */
    public void add(String packName) {
        SQLiteDatabase database = lockDB.getWritableDatabase();
        //表中列值得封装
        ContentValues values = new ContentValues();
        values.put(LockedTable.PACKNAME, packName);
        //插入
        database.insert(LockedTable.TABLENAME, null, values);
        database.close();
    }

    /**
     * 移除程序锁
     * @param packname
     * 解锁的app包名
     */
    public void remove(String packname) {
        SQLiteDatabase database = lockDB.getWritableDatabase();
        //删除
        database.delete(LockedTable.TABLENAME, LockedTable.PACKNAME + "=?", new String[]{packname});
    }

    /**
     * 获取所有的加锁的app的包名
     */
    public List<String> getAllLockedDatas(){
        List<String> lockedNames = new ArrayList<>();
        SQLiteDatabase database = lockDB.getReadableDatabase();
        //取出所有包名
        Cursor cursor = database.rawQuery("select " + LockedTable.PACKNAME + " from " + LockedTable.TABLENAME, null);
        while (cursor.moveToNext()) {
            lockedNames.add(cursor.getString(0));
        }
        return lockedNames;
    }


}
