package com.soleil.mobileguard.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.soleil.mobileguard.domain.TaskBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TaskManagerEngine {

    /**
     * @param context
     * @return  系统运行的所有进程apk
     */
    public static List<TaskBean> getAllRunningTaskInfos(Context context) {
        List<TaskBean> datas = new ArrayList<>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取运行中的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses
                ) {

                TaskBean bean = new TaskBean();
                String processName = info.processName;

                bean.setPackName(processName);//apk包名

                PackageManager pm = context.getPackageManager();

            PackageInfo packageInfo = null;
            try {
                //有些进程是无名进程
                packageInfo = pm.getPackageInfo(processName, 0);
            } catch (PackageManager.NameNotFoundException e) {
              continue;//继续循环不添加没有名字的进程
            }

            bean.setIcon(packageInfo.applicationInfo.loadIcon(pm));
                bean.setName(packageInfo.applicationInfo.loadLabel(pm) + "");

                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    bean.setSystem(true);
                } else {
                    bean.setSystem(false);
                }


                Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
                bean.setMemSize(processMemoryInfo[0].getTotalPrivateDirty() * 1024);//运行中占用的内存

                datas.add(bean);


            }



        return datas;
    }

    /**
     * @param context
     * @return 总内存大小
     */
    public static long getTotalMemSize(Context context) {
        long size = 0;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(outInfo);
//        //获得总内存大小,16级别以上，读取内存文件
//        size = outInfo.totalMem;
        File file = new File("/proc/meminfo");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String totalMemInfo = reader.readLine();

            int startIndex = totalMemInfo.indexOf(':');
            int endIndex = totalMemInfo.indexOf('k');
            totalMemInfo = totalMemInfo.substring(startIndex + 1, endIndex).trim();
            size = Long.parseLong(totalMemInfo);
            size *= 1024;//kb转为byte单位
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * @param context
     * @return 可用内存大小
     */
    public static long getAvailableMemSize(Context context) {
        long size = 0;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        //获得可用内存大小,9级别以上，读取内存文件
        size = outInfo.availMem;
        return size;
    }
}
