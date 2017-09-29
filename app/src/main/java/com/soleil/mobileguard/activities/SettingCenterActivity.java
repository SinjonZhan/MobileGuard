package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.service.ComingPhoneService;
import com.soleil.mobileguard.service.TelSmsBlackService;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.ServiceUtils;
import com.soleil.mobileguard.utils.SpTools;
import com.soleil.mobileguard.view.SettingCenterView;


public class SettingCenterActivity extends Activity {


    private SettingCenterView scv_setting_center_update;
    private SettingCenterView scv_setting_center_intercept_black;
    private SettingCenterView scv_setting_center_phoneLocationService;
    private TextView tv_locationstyle_content;
    private RelativeLayout rl_locationstyle_select;
    private String[] styleNames = new String[]{"苹果绿", "卫士蓝", "金属灰", "活力橙", "半透明"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initData() {
        tv_locationstyle_content.setText(styleNames[Integer.parseInt(SpTools.getString(getApplicationContext(), MyConstants.STYLEINDEX, "0"))]);
        scv_setting_center_update.setChecked(SpTools.getBoolean(getApplicationContext(), MyConstants.AUTOUPDATE, false));

        scv_setting_center_intercept_black.setChecked(ServiceUtils.isServiceRunning(getApplicationContext(), "com.soleil.mobileguard.service.TelSmsBlackService"));

        scv_setting_center_phoneLocationService.setChecked(ServiceUtils.isServiceRunning(getApplicationContext(), "com.soleil.mobileguard.service.ComingPhoneService"));
    }

    private void initEvent() {

        rl_locationstyle_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用对话框选择样式
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingCenterActivity.this);
                builder.setSingleChoiceItems(styleNames, Integer.parseInt(SpTools.getString(getApplicationContext(), MyConstants.STYLEINDEX, "0")), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //点击位置保存
                        SpTools.putString(getApplicationContext(), MyConstants.STYLEINDEX, which + "");
                        tv_locationstyle_content.setText(styleNames[which]);
                        dialog.dismiss();
                    }
                });
                builder.setTitle("选择归属地样式");

                builder.show();
            }
        });

        // 来电显示归属地服务显示
        scv_setting_center_phoneLocationService.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ServiceUtils.isServiceRunning(getApplicationContext(), "com.soleil.mobileguard.service.ComingPhoneService")) {

                    Intent service = new Intent(SettingCenterActivity.this, ComingPhoneService.class);
                    stopService(service);
                    scv_setting_center_phoneLocationService.setChecked(false);
                } else {
                    Intent service = new Intent(SettingCenterActivity.this, ComingPhoneService.class);
                    startService(service);
                    scv_setting_center_phoneLocationService.setChecked(true);
                }
            }
        });


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
        //获取来电显示的服务设置
        scv_setting_center_phoneLocationService = (SettingCenterView) findViewById(R.id.scv_setting_center_phonelocationservice);
        //归属地样式内容
        tv_locationstyle_content = (TextView) findViewById(R.id.tv_setting_center_locationstyle_content);
        //选择归属地的按钮
        rl_locationstyle_select = (RelativeLayout) findViewById(R.id.rl_setting_center_locationstyle_select);


    }

}
