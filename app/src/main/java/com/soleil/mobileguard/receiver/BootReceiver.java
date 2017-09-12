package com.soleil.mobileguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.soleil.mobileguard.service.LostFindService;
import com.soleil.mobileguard.utils.EncryptTools;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;


public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //从sp中获取老的sim卡的信息
        String oldSim = SpTools.getString(context, MyConstants.SIM, "");

        //获取当前SIM卡的信息
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();

        if (!simSerialNumber.equals(oldSim+"1")) {
            SmsManager sm = SmsManager.getDefault();
            String safeNumber = SpTools.getString(context, MyConstants.SAFENUMBER, null);
            safeNumber = EncryptTools.decrypt(safeNumber, MyConstants.MUSIC);
            sm.sendTextMessage(safeNumber, null, "wo shi xiao tou！", null, null);
        }

        if (SpTools.getBoolean(context, MyConstants.LOSTFIND, false)) {
            Intent service = new Intent(context, LostFindService.class);
            context.startService(service);

        }
    }
}
