package com.soleil.mobileguard.activities;

import android.view.View;

import com.soleil.mobileguard.R;

public class SetUp3Activity extends BaseSetupActivity {


    @Override
    protected void nextActivity() {
        startActivity(SetUp4Activity.class);

    }

    @Override
    protected void prevActivity() {
        startActivity(SetUp2Activity.class);

    }

    public void initView() {
        setContentView(R.layout.activity_setup3);
    }


    public void selectSafeNumber(View view) {
    }
}
