package com.soleil.mobileguard.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ServiceUtils {
    public static boolean isServiceRunning(Context context, String serviceName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(50);
//        System.out.println("------------------------------");

        for (ActivityManager.RunningServiceInfo runningService : runningServices
                ) {
//            System.out.println(runningService.service.getClassName());
            if(runningService.service.getClassName().equals(serviceName)){
                isRunning = true;
                break;
            }
        }
//        System.out.println("------------------------------");


        return isRunning;
    }


}
