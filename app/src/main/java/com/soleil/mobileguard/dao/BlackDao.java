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

    /**
     * @param phone 发送人号码
     * @return 1、短信 2、电话 3、全部 4、不拦截
     */
    public int getMode(String phone) {
        SQLiteDatabase db = blackDb.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + BlackTable.MODE + " from " + BlackTable.BLACKTABLE + " where " + BlackTable.PHONE + "=?", new String[]{phone}, null);
        int mode = 0;


        if (cursor.moveToNext()) {
            mode = cursor.getInt(0);
            System.out.println("****************"+mode+"---------------");
        } else {
            mode = 0;
        }


        System.out.println("---------------"+mode+"---------------");


        cursor.close();
        db.close();
        return mode;
    }

    public int getTotalRows() {
        SQLiteDatabase db = blackDb.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(1) from " + BlackTable.BLACKTABLE, null);
        cursor.moveToNext();


        int totalRows = cursor.getInt(0);


        cursor.close();
        return totalRows;
    }

    /**
     * @param datasNumber 分批加载的数据条目
     * @param startIndex  取数据的其实位置
     * @return 返回分批加载的数据
     */
    public List<BlackBean> getMoreDatas(int datasNumber, int startIndex) {
        List<BlackBean> datas = new ArrayList<>();
        SQLiteDatabase database = blackDb.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + BlackTable.PHONE + "," + BlackTable.MODE + " from " + BlackTable.BLACKTABLE + " order by _id desc " + " limit ?,?", new String[]{startIndex + "", datasNumber + ""});
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

    public int getTotalPage(int perPage) {
        int totalRows = getTotalRows();
        int totalPages = (int) Math.ceil(totalRows * 1.0 / perPage);
        return totalPages;
    }

    public List<BlackBean> getPageDatas(int currentPage, int perPage) {
        List<BlackBean> datas = new ArrayList<>();
        SQLiteDatabase database = blackDb.getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + BlackTable.PHONE + "," + BlackTable.MODE + " from " + BlackTable.BLACKTABLE + " limit ?,?", new String[]{((currentPage - 1) * perPage) + "", perPage + ""});
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


    public List<BlackBean> getAllDatas() {
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
     * @param bean 黑名单信息的封装bean
     */
    public void add(BlackBean bean) {
        add(bean.getPhone(), bean.getMode());
    }

    /**
     * @param phone
     * @param mode  黑名单添加
     */
    public void add(String phone, int mode) {
        delete(phone);
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
