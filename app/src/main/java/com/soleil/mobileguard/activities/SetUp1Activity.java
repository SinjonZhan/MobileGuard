package com.soleil.mobileguard.activities;

import com.soleil.mobileguard.R;

public class SetUp1Activity extends BaseSetupActivity{


    public void initView() {
        setContentView(R.layout.activity_setup1);
    }

    @Override
    protected void nextActivity() {
        startActivity(SetUp2Activity.class);
    }

    @Override
    protected void prevActivity() {

    }
}
