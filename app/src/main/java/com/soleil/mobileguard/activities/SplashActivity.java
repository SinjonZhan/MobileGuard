package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.soleil.mobileguard.BuildConfig;
import com.soleil.mobileguard.R;
import com.soleil.mobileguard.domain.UrlBean;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 手机卫士的Splash界面
 *
 * @作者 xinrong
 * @创建日期 2017/8/30 18:24
 */
public class SplashActivity extends Activity {

    private static final int ERROR = 3;
    private static final int LOADMAIN = 1;
    private static final int SHOWUPDATEDIALOG = 2;
    //splash的根布局
    private RelativeLayout rl_root;
    //splash的文本框
    private TextView tv_version_name;
    private String versionName;
    private int versionCode;
    private UrlBean parseJson;
    private long startTimeMills;

    private HttpUtils utils;
    private String pathName = "/mnt/sdcard/xx.apk";
    private ProgressBar pb_download;
    private HttpURLConnection conn;
    private BufferedReader reader;
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
                case ERROR:
                    switch (msg.arg1) {
                        case 404:
                            Toast.makeText(SplashActivity.this, "404：资源找不到", Toast.LENGTH_SHORT).show();
                            break;
                        case 4001:
                            Toast.makeText(SplashActivity.this, "4001：没有网络", Toast.LENGTH_SHORT).show();
                            break;
                        case 4002:
                            Toast.makeText(SplashActivity.this, "4002：URL地址错误", Toast.LENGTH_SHORT).show();
                            break;
                        case 4003:
                            Toast.makeText(SplashActivity.this, "4003：JSON格式错误", Toast.LENGTH_SHORT).show();
                            break;

                    }

                    break;

                default:
                    break;

            }
        }
    };

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


    }

    private void timeConsuming() {
        if (SpTools.getBoolean(getApplicationContext(), MyConstants.AUTOUPDATE, false)) {
            checkVersion();
        }

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
                int errorCode = -1; //表示正常，无错误
                try {

                    startTimeMills = System.currentTimeMillis();
                    URL url = new URL("http://192.168.80.2:8080/guardversion.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("GET");

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream is = conn.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(is));
                        String line = null;
                        StringBuilder jsonString = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            jsonString.append(line);
                        }
                        parseJson = parseJSON(jsonString);

                    } else {
                        Log.e("SplashActivity.this", "404"); //文件找不到
                        errorCode = 404;
                    }


                } catch (MalformedURLException e) { //4002
                    Log.e("SplashActivity.this", "4002");
                    errorCode = 4002;
                    e.printStackTrace();
                } catch (IOException e) { //4001
                    Log.e("SplashActivity.this", "4001");
                    errorCode = 4001;
                    e.printStackTrace();
                } catch (JSONException e) {//4003
                    Log.e("SplashActivity.this", "4003");
                    errorCode = 4003;
                    e.printStackTrace();
                } finally {
                    if (errorCode == -1) {
                        isNewVersion(parseJson);//程序无误， 判断是否有新版本
                    } else {
                        long endTimeMillis = System.currentTimeMillis();
                        if (endTimeMillis - startTimeMills < 3000) {
                            SystemClock.sleep(3000 - (endTimeMillis - startTimeMills));
                        }
                        Message msg = Message.obtain();
                        msg.what = ERROR;
                        msg.arg1 = errorCode;
                        handler.sendMessage(msg);
                    }
                    try {
                        if (reader == null || conn == null) {
                            return;
                        }
                        reader.close();
                        conn.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();


    }

    private void loadMain() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
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
                        downloadNewApk();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadMain();
            }
        }).show();

    }

    private void downloadNewApk() {
        utils = new HttpUtils();
        System.out.println(parseJson.getUrl());

        File file = new File(pathName);
        file.delete();

        utils.download(parseJson.getUrl(), pathName, new RequestCallBack<File>() {
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                pb_download.setVisibility(View.VISIBLE);//设置进度的显示
                pb_download.setMax((int) total);//设置进度条的最大值
                pb_download.setProgress((int) current);//设置当前进度
                super.onLoading(total, current, isUploading);

            }

            @Override

            public void onSuccess(ResponseInfo<File> responseInfo) {

                Toast.makeText(getApplicationContext(), "更新成功", Toast.LENGTH_LONG).show();
                //安装apk

                installApk();
                pb_download.setVisibility(View.GONE);
            }


            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(getApplicationContext(), "更新失败", Toast.LENGTH_LONG).show();
                pb_download.setVisibility(View.GONE);

            }
        });
    }

    private void installApk() {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        String type = "application/vnd.android.package-archive";

        Uri data = Uri.fromFile(new File(pathName));
        intent.setDataAndType(data, type);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadMain();
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void isNewVersion(UrlBean parseJson) {
        int serverCode = parseJson.getVersionName();
        long endTimeMillis = System.currentTimeMillis();
        if (endTimeMillis - startTimeMills < 3000) {
            SystemClock.sleep(3000 - (endTimeMillis - startTimeMills));
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

    private UrlBean parseJSON(StringBuilder jsonString) throws JSONException {
        UrlBean bean = new UrlBean();


        JSONObject jsonObject = new JSONObject(jsonString + "");
        String url = jsonObject.getString("url");
        int version = jsonObject.getInt("version");
        String desc = jsonObject.getString("desc");
        bean.setDesc(desc);
        bean.setUrl(url);
        bean.setVersionName(version);

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
        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                timeConsuming();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!SpTools.getBoolean(getApplicationContext(), MyConstants.AUTOUPDATE, false)) {
                    loadMain();

                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rl_root.startAnimation(as);
    }


    /**
     * 界面的初始化
     */
    private void initView() {
        setContentView(R.layout.activity_splash);
        rl_root = (RelativeLayout) findViewById(R.id.rl_splash_root);
        pb_download = (ProgressBar) findViewById(R.id.pb_splash_download_progress);
        tv_version_name = (TextView) findViewById(R.id.tv_splash_version_name);
    }
}
