package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.service.TelSmsBlackService;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.ServiceUtils;
import com.soleil.mobileguard.utils.SpTools;
import com.soleil.mobileguard.view.SettingCenterView;


public class SettingCenterActivity extends Activity {


    private SettingCenterView scv_setting_center_update;
    private SettingCenterView scv_setting_center_intercept_black;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initData() {
        scv_setting_center_update.setChecked(SpTools.getBoolean(getApplicationContext(), MyConstants.AUTOUPDATE, false));

        scv_setting_center_intercept_black.setChecked(ServiceUtils.isServiceRunning(getApplicationContext(), "com.soleil.mobileguard.service.TelSmsBlackService"));
    }


    private void initEvent() {
        //无法获取到焦点，不能设置动态监听

        scv_setting_center_update.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scv_setting_center_update.setChecked(!scv_setting_center_update.isChecked());
                //添加的功能
                SpTools.putBoolean(getApplicationContext(), MyConstants.AUTOUPDATE, scv_setting_center_update.isChecked());
            }
        });

        scv_setting_center_intercept_black.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //拦截黑名单 控制服务启动与停止
                if (ServiceUtils.isServiceRunning(getApplicationContext(), "com.soleil.mobileguard.service.TelSmsBlackService")) {

                    Intent service = new Intent(SettingCenterActivity.this, TelSmsBlackService.class);
                    stopService(service);
                    scv_setting_center_intercept_black.setChecked(false);
                } else {
                    Intent service = new Intent(SettingCenterActivity.this, TelSmsBlackService.class);
                    startService(service);
                    scv_setting_center_intercept_black.setChecked(true);
                }

            }
        });


    }


    private void initView() {
        setContentView(R.layout.activity_settingcenter);
        scv_setting_center_update = (SettingCenterView) findViewById(R.id.scv_setting_center_update);
        scv_setting_center_intercept_black = (SettingCenterView) findViewById(R.id.scv_setting_center_intercept_black);
    }
}
