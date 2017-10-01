package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.service.ClearTaskService;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.ServiceUtils;
import com.soleil.mobileguard.utils.SpTools;


public class TaskManagerSettingActivity extends Activity {

    private CheckBox cb_lockClearTask;
    private CheckBox cb_showSysTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initData() {
        cb_lockClearTask.setChecked(ServiceUtils.isServiceRunning(getApplicationContext(), "com.soleil.mobileguard.service.ClearTaskService"));
        cb_showSysTask.setChecked(SpTools.getBoolean(getApplicationContext(), MyConstants.SHOWTASK, true));

    }

    private void initEvent() {
        cb_showSysTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SpTools.putBoolean(getApplicationContext(), MyConstants.SHOWTASK, true);


                } else {
                    SpTools.putBoolean(getApplicationContext(), MyConstants.SHOWTASK, false);



                }
            }
        });

        cb_lockClearTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //锁屏是个广播，代码注册
                    //选中 就开启该广播服务
//                    SpTools.putBoolean(getApplicationContext(), MyConstants.LOCKCLEARTASK, true);
                    Intent service = new Intent(TaskManagerSettingActivity.this, ClearTaskService.class);
                    startService(service);

                } else {
//                    SpTools.putBoolean(getApplicationContext(), MyConstants.LOCKCLEARTASK, false);

                    Intent service = new Intent(TaskManagerSettingActivity.this, ClearTaskService.class);
                    stopService(service);
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_task_setting);
        cb_lockClearTask = (CheckBox) findViewById(R.id.cb_lock_clear_task);
        cb_showSysTask = (CheckBox) findViewById(R.id.db_show_sys_task);
    }
}
