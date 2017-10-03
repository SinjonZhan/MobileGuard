package com.soleil.mobileguard.domain;

import android.net.Uri;

/**
 * 程序锁数据库 加锁的表结构
 */
public interface LockedTable {
    String TABLENAME = "locked"; //程序锁表名
    String PACKNAME = "packname";//程序锁列名

    Uri uri = Uri.parse("content://soleil/locked");//数据库内容观察者地址
}
