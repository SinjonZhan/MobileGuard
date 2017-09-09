package com.soleil.mobileguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;


public class LostFindService extends Service {

    private SmsReceiver receiver;

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
                System.out.println("短信的内容：" + sm.getDisplayMessageBody() + "/n发信人：" + sm.getDisplayOriginatingAddress());

            }
            System.out.println("------------------------------");
        }
    }
}
