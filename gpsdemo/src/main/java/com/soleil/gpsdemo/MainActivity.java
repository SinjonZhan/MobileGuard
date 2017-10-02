package com.soleil.gpsdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView tv_gps_msg;
    private LocationManager lm;
    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            float accuracy = location.getAccuracy();//获取精度
            double altitude = location.getAltitude();//获取海拔高度
            double longitude = location.getLongitude();//获取经度
            double latitude = location.getLatitude();//获取纬度
            float speed = location.getSpeed();

            tv_gps_msg.append("accuracy:" + accuracy + "\n");
            tv_gps_msg.append("altitude:" + altitude + "\n");
            tv_gps_msg.append("longitude:" + longitude + "\n");
            tv_gps_msg.append("latitude:" + latitude + "\n");
            tv_gps_msg.append("speed:" + speed + "\n");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates("gps", 0, 0, listener);
    }

    private void initView() {
        setContentView(R.layout.activity_mian);
        tv_gps_msg = (TextView) findViewById(R.id.tv_gps_msg);
    }


}
