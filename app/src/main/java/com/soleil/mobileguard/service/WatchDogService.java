package com.soleil.mobileguard.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.soleil.mobileguard.activities.WatchDogEnterPassActivity;
import com.soleil.mobileguard.dao.LockDao;

import java.util.List;


/**
 * 看门狗服务
 */
public class WatchDogService extends Service {
    private boolean isWatch = true;//是否监控
    private ActivityManager am;
    private LockDao dao;
    private WatchDogReceiver watchDogReceiver;
    private String shuRen = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    @Override
    public void onCreate() {
        //看门狗广播.锁屏停止看门狗
        watchDogReceiver = new WatchDogReceiver();
        IntentFilter filter = new IntentFilter("com.soleil.watchdog");
        filter.addAction(Intent.ACTION_SCREEN_OFF);//锁屏action
        filter.addAction(Intent.ACTION_SCREEN_ON);//解锁锁屏
        registerReceiver(watchDogReceiver, filter);


        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        dao = new LockDao(getApplicationContext());

        watchDog();//看门狗逻辑
        super.onCreate();
    }

    private void watchDog() {
        isWatch = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isWatch) {
                    //通过isWatch控制线程
                    //1、获取任务栈
                    //获得最新的任务栈
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);//最新打开的任务栈
                    String packageName = runningTaskInfo.topActivity.getPackageName();//获取顶部活动包名

                    Log.d("watch", packageName);
                    //判断 是否加锁
                    if (dao.isLocked(packageName)) {
                        //判断是否是熟人
                        if (packageName.equals(shuRen)) {
                            //不拦截

                        } else {

                            //输入口令
                            //正确才能访问
                            Intent enterPass = new Intent(getApplicationContext(), WatchDogEnterPassActivity.class);
                            enterPass.putExtra("packname", packageName);//把包名传给输入口令的活动
                            //服务中打开活动要new一个新的任务栈
                            enterPass.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(enterPass);
                        }
                    } else {
                        //什么也不做
                    }

                    SystemClock.sleep(50);//每隔50ms监控任务栈


                }

            }
        }).start();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(watchDogReceiver);
        isWatch = false;
        super.onDestroy();
    }

    /**
     * 看门狗的广播接收者 熟人信息的获取
     */
    private class WatchDogReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.soleil.watchdog")) {

                shuRen = intent.getStringExtra("packname");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                //清理熟人
                shuRen = "";
                isWatch = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                //启动看门狗
                watchDog();

            }
        }
    }
}
