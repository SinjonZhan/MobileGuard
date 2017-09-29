package com.soleil.mobileguard.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.soleil.mobileguard.utils.EncryptTools;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;


public class LocationService extends Service {

    private LocationManager lm;
    private StringBuilder tv_gps_msg;
    private LocationListener listener;


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                float accuracy = location.getAccuracy();//获取精度
                double altitude = location.getAltitude();//获取海拔高度
                double longitude = location.getLongitude();//获取经度
                double latitude = location.getLatitude();//获取纬度
                float speed = location.getSpeed();
                tv_gps_msg = new StringBuilder();
                tv_gps_msg.append("accuracy:" + accuracy + "\n");
                tv_gps_msg.append("altitude:" + altitude + "\n");
                tv_gps_msg.append("longitude:" + longitude + "\n");
                tv_gps_msg.append("latitude:" + latitude + "\n");
                tv_gps_msg.append("speed:" + speed + "\n");


                String safeNumber = SpTools.getString(getApplicationContext(), MyConstants.SAFENUMBER, "");
                safeNumber = EncryptTools.decrypt(safeNumber, MyConstants.MUSIC);
                //发送短信
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(safeNumber, null, tv_gps_msg + "", null, null);

                stopSelf();//调用onDestroy();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        lm.requestLocationUpdates("gps", 0, 0, listener);
        super.onCreate();


    }

    @Override
    public void onDestroy() {
        lm.removeUpdates(listener);
        lm = null;
        super.onDestroy();
    }
}
