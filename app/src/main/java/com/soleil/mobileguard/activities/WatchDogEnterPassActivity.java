package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.soleil.mobileguard.R;


/**
 *
 */
public class WatchDogEnterPassActivity extends Activity {

    private ImageView iv_icon;
    private EditText et_password;
    private Button bt_enter;
    private String packName;
    private HomeReceiver homeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(homeReceiver);
        super.onDestroy();
    }

    private void initData() {
        //注册Home键的广播

        homeReceiver = new HomeReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homeReceiver, filter);

        //获取app包名
        packName = (String) getIntent().getStringExtra("packname");

        PackageManager packageManager = getPackageManager();
        try {

            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packName, 0);
            iv_icon.setImageDrawable(applicationInfo.loadIcon(packageManager));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        bt_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = et_password.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.equals("123")) {
                    //密码正确
                    //告诉看门狗这是熟人
                    Intent intent = new Intent();
                    intent.setAction("com.soleil.watchdog");
                    intent.putExtra("packname", packName);
                    //发送广播告诉看门狗是熟人
                    sendBroadcast(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "密码不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_watchdog_enterpass);
        iv_icon = (ImageView) findViewById(R.id.iv_watchdog_enterpass_icon);
        et_password = (EditText) findViewById(R.id.et_watchdog_enterpass_password);
        bt_enter = (Button) findViewById(R.id.bt_watchdog_enterpass_enter);

    }

    /**
     * home back键的处理
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goToHome();

        }


        return super.onKeyDown(keyCode, event);
    }

    private void goToHome() {
        /**
         <action android:name="android.intent.action.MAIN" />
         <category android:name="android.intent.category.HOME" />
         <category android:name="android.intent.category.DEFAULT" />
         <category android:name="android.intent.category.MONKEY"/>
         **/
        //主界面的意图
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
        finish();//关闭自己
    }

    private class HomeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                //如果是home键
                //回到主界面 并且关闭自己
                goToHome();
            }
        }
    }
}
