package com.soleil.mobileguard.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;


public class ClearTaskReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo taskInfo : runningAppProcesses) {

            am.killBackgroundProcesses(taskInfo.processName);//清理进程


        }
        Log.d("ClearTaskService", "widget进程清理");
    }
}
