package com.soleil.mobileguard.domain;

/**
 * 程序锁数据库 加锁的表结构
 */
public interface LockedTable {
    String TABLENAME = "locked"; //程序锁表名
    String PACKNAME = "packname";//程序锁列名
}
