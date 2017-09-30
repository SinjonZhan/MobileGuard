package com.soleil.mobileguard.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.soleil.mobileguard.domain.AppBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取apk详细信息
 */
public class AppManagerEngine {
    public static long getSDAvail(Context context) {
        long sdAvail = 0;
        //获取sd卡的目录
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        sdAvail = externalStorageDirectory.getFreeSpace();//以字节数为单位

        return sdAvail;
    }

    public static long getRomAvail(Context context) {
        long romAvail = 0;
        //获取sd卡的目录
        File dataDirectory = Environment.getDataDirectory();
        romAvail = dataDirectory.getFreeSpace();//以字节数为单位

        return romAvail;
    }

    /**
     * @param context
     * @return 所有安装的apk信息
     */
    public static List<AppBean> getAllApk(Context context) {
        //获得所有的apk的数据

        List<AppBean> apks = new ArrayList<>();

        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            AppBean bean = new AppBean();
            bean.setName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
            bean.setPackageName(packageInfo.packageName);
            bean.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            bean.setSize(file.length());
            int flag = packageInfo.applicationInfo.flags;//获取当前apk的属性


            //是否是系统apk
            if ((flag & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //是系统apk
                bean.setSystem(true);
            } else {
                //用户apk
                bean.setSystem(false);
            }
            //是否存放在sd卡
            if ((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                //安装在SD卡中
                bean.setSd(true);
            } else {
                //ROm
                bean.setSd(false);
            }

            //添加apk的路径
            bean.setApkPath(packageInfo.applicationInfo.sourceDir);


            apks.add(bean);
        }
        return apks;
    }

}
