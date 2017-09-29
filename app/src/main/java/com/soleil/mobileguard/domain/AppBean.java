package com.soleil.mobileguard.domain;

import android.graphics.drawable.Drawable;

/**
 * apk信息封装类
 */
public class AppBean {
    private Drawable icon;//apk的图标
    private String name;//apk的名字
    private long size;//apk的大小,字节为单位
    private boolean isSd;//apk的位置  ROM|SD

    private boolean isSystem;//是否是系统apk
    private String packageName;//apk的包名

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isSd() {
        return isSd;
    }

    public void setSd(boolean sd) {
        isSd = sd;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
