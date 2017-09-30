package com.soleil.mobileguard.domain;

import android.graphics.drawable.Drawable;

/**
 * 进程的数据封装类
 */
public class TaskBean {
    private boolean isChecked;//是否被选中
    private boolean isSystem;//是否是系统apk
    private Drawable icon;//apk的图标
    private String name;//apk的名字
    private long memSize;//apk的内存
    private String packName;//apk的包名

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

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

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }
}
