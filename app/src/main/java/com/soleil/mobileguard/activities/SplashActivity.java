package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soleil.mobileguard.BuildConfig;
import com.soleil.mobileguard.R;
import com.soleil.mobileguard.domain.UrlBean;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 手机卫士的Splash界面
 *
 * @作者 xinrong
 * @创建日期 2017/8/30 18:24
 */
public class SplashActivity extends Activity {

    //splash的根布局
    private RelativeLayout rl_root;
    //splash的文本框
    private TextView tv_version_name;

    private String versionName;
    private int versionCode;
    private static final int LOADMAIN = 1;
    private static final int SHOWUPDATEDIALOG = 2;
    private UrlBean parseJson;
    private long startTimeMills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化布局
        initView();
        //初始化数据
        initData();
        //初始化动画
        initAnimation();
        //检查版本更新
        
        checkVersion();
    }

    private void initData() {

        versionCode = BuildConfig.VERSION_CODE;
        versionName = BuildConfig.VERSION_NAME;
        System.out.println("---------------" + versionName + "---------------");
        tv_version_name.setText(versionName);


    }

    private void checkVersion() {
        new Thread(new Runnable() {


            @Override
            public void run() {
                try {
                    startTimeMills = System.currentTimeMillis();
                    URL url = new URL("http://192.168.1.211:8080/guardversion.json");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("GET");

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream is = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        String line = null;
                        StringBuilder jsonString = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            jsonString.append(line);
                        }
                        parseJson = parseJSON(jsonString);

                        isNewVersion(parseJson);

                        System.out.println("版本号：" + parseJson.getVersionName());
                        reader.close();
                        conn.disconnect();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOADMAIN:
                    loadMain();
                    break;
                case SHOWUPDATEDIALOG:
                    showUpdateDialog();
                    break;
                default:
                    break;

            }
        }
    };

    private void loadMain() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("提醒")
                .setMessage("是否更新新版本？新版本特性如下；" + parseJson.getDesc())
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //更新apk
                        Log.d("this", "更新apk");
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadMain();
            }
        }).show();

    }

    private void isNewVersion(UrlBean parseJson) {
        int serverCode = parseJson.getVersionName();
        long endTimeMillis = System.currentTimeMillis();
        if (endTimeMillis - startTimeMills < 3000) {
            SystemClock.sleep(3000-(endTimeMillis - startTimeMills));
        }
        if (serverCode == versionCode) {
            Message msg = Message.obtain();
            msg.what = LOADMAIN;
            handler.sendMessage(msg);

        } else {
            //弹出对话框 用户选择|是否更新
            Message msg = Message.obtain();
            msg.what = SHOWUPDATEDIALOG;
            handler.sendMessage(msg);

        }
    }

    private UrlBean parseJSON(StringBuilder jsonString) {
        UrlBean bean = new UrlBean();

        try {
            JSONObject jsonObject = new JSONObject(jsonString + "");
            String url = jsonObject.getString("url");
            int version = jsonObject.getInt("version");
            String desc = jsonObject.getString("desc");
            bean.setDesc(desc);
            bean.setUrl(url);
            bean.setVersionName(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 动画的显示
     */
    private void initAnimation() {
        AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
        aa.setDuration(3000);
        aa.setFillAfter(true);

        ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        aa.setDuration(3000);
        aa.setFillAfter(true);

        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        aa.setDuration(3000);
        aa.setFillAfter(true);


        AnimationSet as = new AnimationSet(true);
        as.addAnimation(aa);
        as.addAnimation(sa);
        as.addAnimation(ra);
        as.setDuration(3000);
        as.setFillAfter(true);
        rl_root.startAnimation(as);
    }

    /**
     * 界面的初始化
     */
    private void initView() {
        setContentView(R.layout.activity_splash);
        rl_root = (RelativeLayout) findViewById(R.id.rl_splash_root);
        tv_version_name = (TextView) findViewById(R.id.tv_splash_version_name);
    }
}
