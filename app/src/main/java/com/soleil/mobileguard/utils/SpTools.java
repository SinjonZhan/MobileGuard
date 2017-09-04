package com.soleil.mobileguard.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpTools {
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.SPFILE, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }


    public static void getString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.SPFILE, Context.MODE_PRIVATE);
        sp.getString(key, value);
    }
}
