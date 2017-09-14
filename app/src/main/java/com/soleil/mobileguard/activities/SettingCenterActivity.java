package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.soleil.mobileguard.R;


public class SettingCenterActivity extends Activity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initData() {

    }

    private void initEvent() {

    }

    private void initView() {
        setContentView(R.layout.activity_settingcenter);

    }
}
