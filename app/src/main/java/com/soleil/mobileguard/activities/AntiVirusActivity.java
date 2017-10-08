package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.dao.AntiVirusDao;
import com.soleil.mobileguard.domain.AppBean;
import com.soleil.mobileguard.engine.AppManagerEngine;

import java.util.List;


public class AntiVirusActivity extends Activity {

    private static final int SCANNING = 1;
    private static final int FINISH = 3;
    private static final int MESSAGE = 2;
    private ImageView iv_scan;
    private ProgressBar pb_progress;
    private TextView tv_scanAppName;
    private LinearLayout ll_scanContent;
    private RotateAnimation ra;
    private AntiVirusDao dao;
    private List<AppBean> allApk;
    private int progress;//进度条的进度
    private boolean isRun = false;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANNING:
                    iv_scan.startAnimation(ra);

                    break;
                case MESSAGE:

                    pb_progress.setMax(allApk.size());
                    pb_progress.setProgress(progress);
                    AntiVirusBean bean = (AntiVirusBean) msg.obj;
                    TextView tv = new TextView(AntiVirusActivity.this);
                    if (bean.isVirus) {
                        tv.setTextColor(Color.RED);

                    } else {
                        tv.setTextColor(Color.BLACK);

                    }
                    tv.setText(bean.packName);

                    tv_scanAppName.setText("正在扫描:"+bean.packName);

                    ll_scanContent.addView(tv, 0);
                    break;
                case FINISH:
                    iv_scan.clearAnimation();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initAnimation();//初始化动画
        startScan();//开始扫描
    }

    /**
     * 开始扫描病毒
     */
    private void startScan() {
        //耗时扫描
        new Thread(new Runnable() {
            @Override
            public void run() {


                //开始扫描动画
                handler.obtainMessage(SCANNING).sendToTarget();
                //获取所有安装的APK
                allApk = AppManagerEngine.getAllApk(getApplicationContext());

                AntiVirusBean bean = new AntiVirusBean();//存放每个扫描的apk的结果信息

                isRun = true;
                for (AppBean appBean : allApk) {
                    if (!isRun) {
                        //停止线程运行
                        return;
                    }
                    bean.packName = appBean.getName();
                    if (dao.isVirus(appBean.getApkPath())) {
                        //是病毒
                        bean.isVirus = true;
                    } else {
                        //不是病毒
                        bean.isVirus = false;

                    }
                    Message msg = handler.obtainMessage(MESSAGE);
                    msg.obj = bean;
                    progress++;
                    handler.sendMessage(msg);
                    SystemClock.sleep(50);

                }
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }).start();
    }

    private void initAnimation() {
        ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(500);
        ra.setRepeatCount(Animation.INFINITE);
        //修改旋转的动画插值器
        ra.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float x) {
                return x;
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_antivirus);
        //扫描病毒的扇形
        iv_scan = (ImageView) findViewById(R.id.iv_antivirus_scan);
        //扫描的进度
        pb_progress = (ProgressBar) findViewById(R.id.pb_antivirus_scanprogress);
        //扫描的app的名字显示
        tv_scanAppName = (TextView) findViewById(R.id.tv_antivirus_title);
        //扫描结果显示
        ll_scanContent = (LinearLayout) findViewById(R.id.ll_antivirus_result);
        //病毒数据库处理
        dao = new AntiVirusDao();
    }

    private class AntiVirusBean {
        String packName;
        boolean isVirus;
    }

    @Override
    protected void onDestroy() {
        isRun = false;
        super.onDestroy();
    }
}
