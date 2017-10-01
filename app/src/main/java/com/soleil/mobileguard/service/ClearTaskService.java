package com.soleil.mobileguard.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.List;


/**
 * 该服务主要完成锁屏的注册和反注册
 */
public class ClearTaskService extends Service {

    private ClearTaskReceiver clearTaskReceiver;
    private ActivityManager am;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //广播注册
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        clearTaskReceiver = new ClearTaskReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(clearTaskReceiver, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //广播反注册
        unregisterReceiver(clearTaskReceiver);

        super.onDestroy();
    }

    private class ClearTaskReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

            for (ActivityManager.RunningAppProcessInfo taskInfo : runningAppProcesses) {

                am.killBackgroundProcesses(taskInfo.processName);//清理进程


            }
            Log.d("ClearTaskService", "锁屏清理进程");

        }
    }
}
