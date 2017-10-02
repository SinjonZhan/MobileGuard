package com.soleil.lockscreen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    private DevicePolicyManager dpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();


    }

    private void initView() {
        setContentView(R.layout.activity_main);

    }

    public void lockScreen(View view) {
        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName who = new ComponentName(this, DeviceAdminSample.class);
        if (dpm.isAdminActive(who)) {
            dpm.lockNow();

        }else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,who);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "设备管理器.... ");
            startActivityForResult(intent, 1);
        }

    }
}
