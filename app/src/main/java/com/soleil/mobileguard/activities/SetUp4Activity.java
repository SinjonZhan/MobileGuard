package com.soleil.mobileguard.activities;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;

public class SetUp4Activity extends BaseSetupActivity {

    @Override
    protected void nextActivity() {
        SpTools.putBoolean(getApplicationContext(), MyConstants.ISSETUP, true);
        startActivity(LostFindActivity.class);

    }

    @Override
    protected void prevActivity() {
        startActivity(SetUp3Activity.class);

    }

    public void initView() {
        setContentView(R.layout.activity_setup4);
    }


}
