package com.soleil.mobileguard.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.engine.TaskManagerEngine;
import com.soleil.mobileguard.receiver.ExampleAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;


public class AppWidgetService extends Service {

    private Timer timer;
    private TimerTask timerTask;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        final AppWidgetManager awm = AppWidgetManager.getInstance(getApplicationContext());

        //动态更新数据，时间定时器
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                ComponentName provider = new ComponentName(getApplicationContext(), ExampleAppWidgetProvider.class);

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
                //更新数据

                int runningNumber = TaskManagerEngine.getAllRunningTaskInfos(getApplicationContext()).size();
                long availMem = TaskManagerEngine.getAvailableMemSize(getApplicationContext());

                String availMemStr = android.text.format.Formatter.formatFileSize(getApplicationContext(), availMem);
                views.setTextViewText(R.id.process_count, "正在运行软件:" + runningNumber);
                views.setTextViewText(R.id.process_memory, "可用内存:" + availMemStr);
                Intent intent = new Intent("com.soleil.widget.cleartask");

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);


                awm.updateAppWidget(provider, views);

            }
        };
        timer.schedule(timerTask, 0, 1000 );  //每10s执行一次task
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

}
