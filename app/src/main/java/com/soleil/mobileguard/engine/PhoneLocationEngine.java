package com.soleil.mobileguard.engine;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 电话归属地
 */
public class PhoneLocationEngine {

    public static String locationQuery(String phoneNumber, Context context) {
        String location = phoneNumber;

        //如果是手机号
        Pattern pattern = Pattern.compile("1[3578]{1}[0-9]{9}");
        Matcher matcher = pattern.matcher(phoneNumber);
        boolean b = matcher.matches();
        if (b) {
            location = mobileQuery(phoneNumber, context);
        } else if (phoneNumber.length() >= 11) {

            //如果是固定电话
            location = phoneQuery(phoneNumber, context);
        } else {

            //如果是服务号
            location = ServiceNumberQuery(phoneNumber);
        }


        return location;
    }

    /**
     * @param phoneNumber
     * @param context
     * @return 固定电话号码归属地  有两位  三位区号之分
     */
    public static String phoneQuery(String phoneNumber, Context context) {


        String res = phoneNumber;
        SQLiteDatabase database = SQLiteDatabase.openDatabase(
                "/data/data/com.soleil.mobileguard/files/address.db", null,
                SQLiteDatabase.OPEN_READONLY);
        String quHao;
        if (phoneNumber.charAt(1) == '1' || phoneNumber.charAt(1) == '2') {
            quHao = phoneNumber.substring(1, 3);
        } else {
            quHao = phoneNumber.substring(1, 4);
        }

        database.rawQuery("select location from data2 where area =?", new String[]{quHao});
        return res;

    }

    /**
     * @param phoneNumber
     * @return 服务号查询 如110
     */
    public static String ServiceNumberQuery(String phoneNumber) {
        String res = "";
        if (phoneNumber.equals("110")) {
            res = "匪警";
        } else if (phoneNumber.equals("10086")) {
            res = "中国移动";

        } else if (phoneNumber.equals("5554")) {
            res = "模拟器";

        }
        return res;
    }


    /**
     * @param phoneNumber 电话号码全称
     * @param context
     * @return 手机号码归属地
     */
    public static String mobileQuery(String phoneNumber, Context context) {



		/*
         * phoneNumber 三种类型： 1， 手机号 2， 固定电话 3， 服务号码 110 120 95559 95555
		 */
        String res = "归属地查询不到";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(
                "/data/data/com.soleil.mobileguard/files/address.db", null,
                SQLiteDatabase.OPEN_READONLY);

        Cursor cursor1 = database.query("data1", new String[]{"outkey"}, "id=?", new String[]{phoneNumber.substring(0, 7)}, null, null, null);


        if (cursor1.moveToNext()) {
            String outkey = cursor1.getString(0);

            Cursor cursor2 = database.query("data2", new String[]{"location"}, "area=?", new String[]{outkey}, null, null, null);
            if (cursor2.moveToNext()) {
                res = cursor2.getString(0);
            }

            cursor2.close();

        }
        cursor1.close();
        database.close();
        return res;

    }
}