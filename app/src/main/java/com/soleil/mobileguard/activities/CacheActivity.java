package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.domain.AppBean;
import com.soleil.mobileguard.engine.AppManagerEngine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class CacheActivity extends Activity {

    private static final int SCANNING = 1;
    private static final int FINISH = 2;
    private ProgressBar pb_loading;
    private LinearLayout ll_mess;
    private List<CacheInfo> cacheDatas = new ArrayList<>();
    private TextView tv_nodata;
    private GetCacheInfo getCacheInfo;
    private PackageManager pm;
    private TextView tv_scan_name;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANNING://扫描
                    pb_loading.setVisibility(View.VISIBLE);
                    ll_mess.setVisibility(View.GONE);
                    tv_nodata.setVisibility(View.GONE);

                    tv_scan_name.setVisibility(View.VISIBLE);

                    tv_scan_name.setText("正在扫描:" + msg.obj);

                    break;

                case FINISH://扫描完成
                    pb_loading.setVisibility(View.GONE);
                    tv_scan_name.setVisibility(View.GONE);

                    //判断是否有缓存
                    if (cacheDatas.size() == 0) {
                        //没有缓存
                        ll_mess.setVisibility(View.GONE);
                        tv_nodata.setVisibility(View.VISIBLE);

                    } else {
                        //有缓存
                        //取缓存信息添加到布局中
                        ll_mess.setVisibility(View.VISIBLE);
                        tv_nodata.setVisibility(View.GONE);

                        for (CacheInfo info :
                                cacheDatas) {
                            //取每条缓存信息
                            //显示
                            View view = View.inflate(getApplicationContext(),
                                    R.layout.item_cache_linearlayout, null);
                            ImageView iv_icon = (ImageView) view
                                    .findViewById(R.id.iv_cache_linearlayout_item_icon);
                            iv_icon.setImageDrawable(info.icon);// 显示图标
                            TextView tv_title = (TextView) view
                                    .findViewById(R.id.tv_cache_linearlayout_item_title);
                            tv_title.setText(info.name);// 显示app名字
                            TextView tv_cachesize = (TextView) view
                                    .findViewById(R.id.tv_cache_linearlayout_cachesize);
                            tv_cachesize.setText(info.cacheSize);// 显示缓存大小

                            ll_mess.addView(view, 0);// 添加缓存信息的显示

                        }
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
        initView();

        scanAllApp();//遍历所有app，是否有缓存信息
    }

    private void scanAllApp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.obtainMessage(SCANNING).sendToTarget(); //发送显示进度条的消息

                List<AppBean> allApk = AppManagerEngine.getAllApk(getApplicationContext());
                //遍历所有安装的apk，判断是否有缓存信息
                for (AppBean bean :
                        allApk) {
                    Message msg = handler.obtainMessage(SCANNING);
                    msg.obj = bean.getPackageName();//传递包名
                    handler.sendMessage(msg);
                    //获得app的缓存大小 并存于容器中
                    getAppCacheSize(bean.getPackageName());
                    SystemClock.sleep(20);
                }

                handler.obtainMessage(FINISH).sendToTarget();

            }
        }).start();
    }

    private void initView() {
        setContentView(R.layout.activity_cache);

        //扫描的进度条
        pb_loading = (ProgressBar) findViewById(R.id.pb_cache_scan);

        //显示所有缓存信息的容器
        ll_mess = (LinearLayout) findViewById(R.id.ll_cache_mess);

        //没有缓存时显示的文本
        tv_nodata = (TextView) findViewById(R.id.tv_cache_nodata);

        //扫描的app包名
        tv_scan_name = (TextView) findViewById(R.id.tv_cache_scanning_appname);


        pm = getPackageManager();


    }

    /**
     * 清理所有缓存
     *
     * @param view
     */
    public void clearAll(View view) {

    }

    private void getAppCacheSize(String packageName) {
        Class clazz = pm.getClass();// 获取PackageManager的类类型
        try {
            Method method = clazz.getDeclaredMethod("getPackageSizeInfo",
                    String.class,int.class,  IPackageStatsObserver.class);
            // 把包名传递给回调对象
            //getcacheInfo.packName = packageName;
            method.invoke(pm, packageName, Process.myUid()/100000, new GetCacheInfo(packageName));// 结果回调在IPackageStatsObserver的对象中
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class GetCacheInfo extends IPackageStatsObserver.Stub {

        String packName;
        public GetCacheInfo(String packName){
            this.packName = packName;
        }


        /**
         * 回调结果
         *
         * @param pStats
         * @param succeeded
         * @throws RemoteException
         */
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {

            if (pStats.cacheSize > 0) {
                System.out.println("有缓存信息：" + packName);
                //有缓存
                //记录app和缓存信息（放到容器中)
                //图标 名字 缓存大小
                CacheInfo cacheInfo = new CacheInfo();
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(packName, 0);
                    //图标
                    cacheInfo.icon = packageInfo.applicationInfo.loadIcon(pm);
                    //名字
                    cacheInfo.name = packageInfo.applicationInfo.loadLabel(pm) + "";
                    //缓存大小
                    cacheInfo.cacheSize = Formatter.formatFileSize(getApplicationContext(), pStats.cacheSize);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                //添加一条缓存信息
                cacheDatas.add(cacheInfo);
            }
        }
    }

    /**
     * 缓存信息封装类
     */
    private class CacheInfo {
        Drawable icon;
        String name;
        String cacheSize;//格式化后的字符串
    }

}
