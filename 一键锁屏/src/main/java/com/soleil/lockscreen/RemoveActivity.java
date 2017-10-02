package com.soleil.lockscreen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;


public class RemoveActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);
    }

    public void remove(View view) {

        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName who = new ComponentName(this, DeviceAdminSample.class);
        dpm.removeActiveAdmin(who);

        /*
         <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <action android:name="android.intent.action.DELETE" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="package" />
            </intent-filter>
         */
        Intent remove = new Intent("android.intent.action.DELETE");
        remove.addCategory("android.intent.category.DEFAULT");
        remove.setData(Uri.parse("package:" + getPackageName()));
        startActivity(remove);//卸载用户apk的界面
    }
}
