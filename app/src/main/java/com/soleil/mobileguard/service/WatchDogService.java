package com.soleil.mobileguard.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * 看门狗服务
 */
public class WatchDogService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
