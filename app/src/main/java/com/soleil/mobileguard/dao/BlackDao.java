package com.soleil.mobileguard.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.soleil.mobileguard.db.BlackListDb;
import com.soleil.mobileguard.domain.BlackBean;
import com.soleil.mobileguard.domain.BlackTable;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单数据封装类
 */
public class BlackDao {
    private BlackListDb blackDb;

    public BlackDao(Context context) {
        this.blackDb = new BlackListDb(context);
    }

  public List<BlackBean> getAllDatas(){
      List<BlackBean> datas = new ArrayList<>();
      SQLiteDatabase database = blackDb.getReadableDatabase();
      Cursor cursor = database.rawQuery("select " + BlackTable.PHONE + "," + BlackTable.MODE + " from " + BlackTable.BLACKTABLE, null);
      while (cursor.moveToNext()) {
          BlackBean bb = new BlackBean();
          bb.setPhone(cursor.getString(0));
          bb.setMode(cursor.getInt(1));

          datas.add(bb);

      }
      cursor.close();
      database.close();
      return datas;
  }

    public void delete(String phone) {
        SQLiteDatabase db = blackDb.getWritableDatabase();
        db.delete(BlackTable.BLACKTABLE, BlackTable.PHONE + "=?", new String[]{phone});
        db.close();
    }

    public void update(String phone, int mode) {
        SQLiteDatabase db = blackDb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BlackTable.MODE, mode);
        db.update(BlackTable.BLACKTABLE, values, BlackTable.PHONE + "=?", new String[]{phone});
        db.close();
    }

    /**
     * @param bean
     * 黑名单信息的封装bean
     */
    public void add(BlackBean bean) {
        add(bean.getPhone(), bean.getMode());
    }

    /**
     * @param phone
     * @param mode
     * 黑名单添加
     */
    public void add(String phone, int mode) {
        //获取黑名单数据库
        SQLiteDatabase db = blackDb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BlackTable.PHONE, phone);
        values.put(BlackTable.MODE, mode);
        //往黑名单表中插入一条记录
        db.insert(BlackTable.BLACKTABLE, null, values);
        db.close();
    }


}
