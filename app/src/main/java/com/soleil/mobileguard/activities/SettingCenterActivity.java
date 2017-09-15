package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;
import com.soleil.mobileguard.view.SettingCenterView;


public class SettingCenterActivity extends Activity {


    private SettingCenterView scv_setting_center_update;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initData() {
        scv_setting_center_update.setChecked(SpTools.getBoolean(getApplicationContext(),MyConstants.AUTOUPDATE,false));
    }

    private void initEvent() {
        scv_setting_center_update.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //原有点击功能
                scv_setting_center_update.setChecked(!scv_setting_center_update.isChecked());
                //添加的功能
                SpTools.putBoolean(getApplicationContext(), MyConstants.AUTOUPDATE, scv_setting_center_update.isChecked());

            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_settingcenter);
        scv_setting_center_update = (SettingCenterView) findViewById(R.id.scv_setting_center_update);
    }
}
