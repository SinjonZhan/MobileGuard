package com.soleil.mobileguard.activities;

import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.service.LostFindService;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.ServiceUtils;
import com.soleil.mobileguard.utils.SpTools;

public class SetUp4Activity extends BaseSetupActivity {

    private CheckBox cb;
    private TextView tv_isStartProtected;

    @Override
    protected void nextActivity() {
        SpTools.putBoolean(getApplicationContext(), MyConstants.ISSETUP, true);
        startActivity(LostFindActivity.class);

    }

    public void initData() {
        if (SpTools.getBoolean(getApplicationContext(),MyConstants.ISSETUP,false)) {
            cb.setChecked(true);
            tv_isStartProtected.setText("防盗保护已经开启");
        }else {
            cb.setChecked(false);
            tv_isStartProtected.setText("防盗保护尚未开启");
        }
    }

    public void initEvent() {
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_isStartProtected.setText("防盗保护已经开启");
                    if(!ServiceUtils.isServiceRunning(getApplicationContext(), "com.soleil.mobileguard.service.LostFindService")){
                    System.out.println("---------------check ture---------------");
                    Intent intent = new Intent(SetUp4Activity.this, LostFindService.class);
                    startService(intent);
                    }
                }else {
                    tv_isStartProtected.setText("防盗保护尚未开启");
                    Intent intent = new Intent(SetUp4Activity.this, LostFindService.class);
                    stopService(intent);
                }

            }
        });
    }

    @Override
    protected void prevActivity() {
        startActivity(SetUp3Activity.class);

    }

    public void initView() {
        setContentView(R.layout.activity_setup4);
        cb = (CheckBox) findViewById(R.id.cb_setup4_isprotected);
        tv_isStartProtected = (TextView) findViewById(R.id.tv_isstartlostfind);
    }


}
