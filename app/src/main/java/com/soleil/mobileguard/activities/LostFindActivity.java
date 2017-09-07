package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;


public class LostFindActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SpTools.getBoolean(getApplicationContext(), MyConstants.ISSETUP, false)) {
            //进入过向导界面
            initView();

        } else {
            //设置向导界面
            Intent intent = new Intent(LostFindActivity.this, SetUp1Activity.class);
            startActivity(intent);
            finish();//注意关闭界面
        }

    }

    private void initView() {
        setContentView(R.layout.activity_lostfind);
    }
}
