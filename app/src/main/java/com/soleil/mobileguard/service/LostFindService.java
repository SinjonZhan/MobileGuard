package com.soleil.mobileguard.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;

import com.soleil.mobileguard.R;


public class LostFindService extends Service {

    private SmsReceiver receiver;
    private boolean isPlay = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        receiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver, filter);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }

    private class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Object datas[] = (Object[]) extras.get("pdus");
            System.out.println("------------------------------");

            for (Object data : datas
                    ) {

                SmsMessage sm = SmsMessage.createFromPdu((byte[]) data);
                String msg = sm.getMessageBody();
                if (msg.equals("#*gps*#")) {
                    //定位是耗时的，要在服务中执行
                    Intent  service= new Intent(context, LocationService.class);
                    startService(service);
                    abortBroadcast();
                } else if (msg.equals("#*lockscreen*#")) {
                    //一键锁屏
                    DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                    dpm.resetPassword("123", 0);
                    dpm.lockNow();

                    abortBroadcast();
                }else if (msg.equals("#*wipedata*#")) {
                    //一键锁屏
                    DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                    dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);

                    abortBroadcast();
                } else if (msg.equals("#*music*#")) {

                    if (isPlay) {
                        return;
                    }
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.js);
                    mp.setVolume(1, 1);
                    mp.start();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            isPlay = false;

                        }
                    });
                    isPlay = true;

                }



            }
            System.out.println("------------------------------");
        }
    }
}
