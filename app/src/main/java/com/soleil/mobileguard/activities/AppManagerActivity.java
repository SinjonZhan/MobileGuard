package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;
import com.soleil.mobileguard.R;
import com.soleil.mobileguard.domain.AppBean;
import com.soleil.mobileguard.engine.AppManagerEngine;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import cn.sharesdk.onekeyshare.OnekeyShare;


/**
 * 软件管家
 */
public class AppManagerActivity extends Activity {

    private static final int LOADING = 1;
    private static final int FINISH = 2;
    List<AppBean> userApks = new ArrayList<>();
    List<AppBean> sysApks = new ArrayList<>();
    private TextView tv_rom;
    private TextView tv_sd;
    private ListView lv_apks;
    private ProgressBar pb_loading;
    private List<AppBean> allApk;
    private apkAdapter adapter;
    private TextView tv_label;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_loading.setVisibility(View.VISIBLE);
                    tv_label.setVisibility(View.GONE);
                    lv_apks.setVisibility(View.GONE);


                    break;
                case FINISH:
                    lv_apks.setVisibility(View.VISIBLE);
                    tv_label.setVisibility(View.VISIBLE);
                    pb_loading.setVisibility(View.GONE);

                    tv_label.setText("个人软件" + "(" + userApks.size() + ")");

                    tv_rom.setText("ROM可用空间：" + (Formatter.formatFileSize(getApplicationContext(), AppManagerEngine.getRomAvail(getApplicationContext()))));
                    tv_sd.setText("SD卡可用空间：" + (Formatter.formatFileSize(getApplicationContext(), AppManagerEngine.getSDAvail(getApplicationContext()))));
                    adapter.notifyDataSetChanged();


                    break;
                default:
                    break;

            }
        }
    };
    private PopupWindow pw;
    private LinearLayout ll_start;
    private LinearLayout ll_remove;
    private LinearLayout ll_share;
    private LinearLayout ll_setting;
    private AppBean bean;
    private PackageManager pm;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
        initPopupWindow();//初始化弹出窗体

        //一键分享的初始化
        MobSDK.init(getApplicationContext(), "2157e82d10cb4", "49d86a30787f5684be2fba224713521b");

        initRemoveApkReceiver();//注册删除apk的广播接收者
    }

    private void initRemoveApkReceiver() {
        //删除apk的广播（包括系统apk）
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initData();
            }
        };
        //注册删除apk的广播
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        //注意配置数据模式
        filter.addDataScheme("package");

        registerReceiver(receiver, filter);
    }

    /**
     * 显示一键分享
     */
    private void showShare() {


        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle("一键分享");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("一键分享测试！");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/pic.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("一键分享测试！");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }

    @Override
    protected void onDestroy() {
        //取消删除apk广播注册
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    /**
     * 初始化弹出窗体
     */
    private void initPopupWindow() {
        View popupView = View.inflate(getApplicationContext(), R.layout.popup_appmanager, null);
        ll_remove = (LinearLayout) popupView.findViewById(R.id.ll_appmanager_popup_remove);
        ll_start = (LinearLayout) popupView.findViewById(R.id.ll_appmanager_popup_start);
        ll_share = (LinearLayout) popupView.findViewById(R.id.ll_appmanager_popup_share);
        ll_setting = (LinearLayout) popupView.findViewById(R.id.ll_appmanager_popup_setting);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_appmanager_popup_remove:
                        removeApk();//卸载apk
                        break;
                    case R.id.ll_appmanager_popup_start:
                        startApk();//启动apk
                        break;
                    case R.id.ll_appmanager_popup_share:
                        shareApk();//分享apk
                        break;
                    case R.id.ll_appmanager_popup_setting:
                        settingCenter();//设置中心
                        break;

                    default:
                        break;
                }
            }


        };

        ll_remove.setOnClickListener(listener);
        ll_start.setOnClickListener(listener);
        ll_share.setOnClickListener(listener);
        ll_setting.setOnClickListener(listener);

        pw = new PopupWindow(popupView, -2, -2);

        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ScaleAnimation sa = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);

        sa.setDuration(1000);


    }

    /**
     * 设置apk
     */
    private void settingCenter() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + bean.getPackageName()));
        startActivity(intent);
    }

    /**
     * 分享apk
     */
    private void shareApk() {
        //短信分享
//        Intent intent = new Intent("android.intent.action.SEND");
//        intent.addCategory( "android.intent.category.DEFAULT");
//        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_TEXT, "downloadUrl:baidu yixia");
//        startActivity(intent);
        //分享微博
        showShare();

    }

    /**
     * 启动apk
     */
    private void startApk() {

        String packageName = bean.getPackageName();
        Intent launchIntentForPackage = pm.getLaunchIntentForPackage(packageName);
        Intent intent = new Intent(launchIntentForPackage);
        //判断app是否有主界面
        if (intent == null) {
            Toast.makeText(getApplicationContext(), "该app没有启动界面", Toast.LENGTH_SHORT).show();
        }
        startActivity(intent);
    }

    /**
     * 移除apk
     */
    private void removeApk() {


        if (!bean.isSystem()) {
            //卸载用户apk
        /*
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <action android:name="android.intent.action.DELETE" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="package" />
            </intent-filter>
           */
            Intent intent = new Intent("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + bean.getPackageName()));
            startActivity(intent);
            //刷新数据  监听删除的广播

        } else {
            //卸载系统apk，默认删除不掉 赋予root权限

            //命令卸载代码中
            try {
                //是否Root刷机
                if (!RootTools.isRootAvailable()) {
                    Toast.makeText(getApplicationContext(), "请先Root刷机\n才能删除系统apk", Toast.LENGTH_SHORT).show();
                    return;
                }

                //是否Root权限授权给当前apk
                if (!RootTools.isAccessGiven()) {
                    Toast.makeText(getApplicationContext(), "请先Root刷机\n才能删除系统apk", Toast.LENGTH_SHORT).show();
                    return;
                }

                RootTools.sendShell("mount -o remount rw /system", 8000);//设置命令超时时间
                System.out.println("------------------------------");
                System.out.println(bean.getApkPath());
                System.out.println("------------------------------");
                RootTools.sendShell("rm -r " + bean.getApkPath(), 8000);
                RootTools.sendShell("mount -o remount r /system", 8000);//权限改回
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (RootToolsException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 显示弹出窗体
     */
    public void showPopupWindow(View parent, int x, int y) {
        closePopupWindow();
        pw.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, x, y);
    }

    /**
     * 关闭弹出窗体
     */
    public void closePopupWindow() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        }
    }


    private void initEvent() {

        lv_apks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取点击位置，点击标签不做处理
                if (position == (userApks.size() + 1)) {
                    return;
                }
                //点击条目的信息
                bean = (AppBean) lv_apks.getItemAtPosition(position);
                int[] location = new int[2];
                view.getLocationInWindow(location);

                int width = allApk.get(1).getIcon().getIntrinsicWidth();

                showPopupWindow(view, location[0]+width+10 , location[1]);
            }
        });

        lv_apks.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //判断显示的位置
                // 如果为系统软件
                //改变标签显示的内容
                closePopupWindow();
                if (firstVisibleItem >= userApks.size() + 1) {
                    tv_label.setText("系统软件" + "(" + sysApks.size() + ")");
//                    adapter.notifyDataSetChanged();
                } else {
                    tv_label.setText("个人软件" + "(" + userApks.size() + ")");
//                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    /**
     * 加载apk获取所有apk信息
     */
    private void initData() {
        allApk = AppManagerEngine.getAllApk(getApplicationContext());
        adapter = new apkAdapter();
        lv_apks.setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {

                handler.obtainMessage(LOADING).sendToTarget();
                SystemClock.sleep(1000);
                sysApks.clear();
                userApks.clear();

                for (AppBean bean : allApk) {
                    if (bean.isSystem()) {
                        sysApks.add(bean);
                    } else {
                        userApks.add(bean);
                    }
                }
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }).start();


    }


    private void initView() {
        setContentView(R.layout.activity_app_manager);
        tv_rom = (TextView) findViewById(R.id.tv_romfreespace);
        tv_sd = (TextView) findViewById(R.id.tv_sdfreespace);

        //apk的listview
        lv_apks = (ListView) findViewById(R.id.lv_appmanager_adddatas);

        //apk的加载进度框
        pb_loading = (ProgressBar) findViewById(R.id.pb_appmanager_loading);

        //listview的label标签
        tv_label = (TextView) findViewById(R.id.tv_appmanager_listview_lable);

        pm = getPackageManager();

    }



    private class apkAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return allApk.size()+2;
        }

        @Override
        public AppBean getItem(int position) {
            AppBean bean = null;
            if (position <= userApks.size()) {
                bean = userApks.get(position - 1);
            } else {
                bean = sysApks.get(position - 1 - 1 - userApks.size());

            }
            return bean;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (position == 0) {
                //用户apk的标签
                TextView tv_userLabel = new TextView(getApplicationContext());
                tv_userLabel.setText("个人软件(" + userApks.size() + ")");
                tv_userLabel.setTextColor(Color.WHITE);
                tv_userLabel.setBackgroundColor(Color.GRAY);
                return tv_userLabel;
            } else if (position == userApks.size() + 1) {
                //系统apk的标签
                TextView tv_sysLabel = new TextView(getApplicationContext());
                tv_sysLabel.setText("系统软件(" + sysApks.size() + ")");
                tv_sysLabel.setTextColor(Color.WHITE);
                tv_sysLabel.setBackgroundColor(Color.GRAY);
                return tv_sysLabel;
            } else {
                if (convertView == null || !(convertView instanceof RelativeLayout)) {
                    convertView = View.inflate(getApplicationContext(), R.layout.item_app_manager_listview, null);
                    holder = new ViewHolder();

                    holder.drawable = (ImageView) convertView.findViewById(R.id.iv_drawable);
                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.tv_isSd = (TextView) convertView.findViewById(R.id.tv_issd);
                    holder.tv_volume = (TextView) convertView.findViewById(R.id.tv_volume);
                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                AppBean bean = getItem(position);

                holder.drawable.setImageDrawable(bean.getIcon());
                holder.tv_name.setText(bean.getName());
                holder.tv_isSd.setText(bean.isSd() ? "SD卡存储" : "ROM存储");
                holder.tv_volume.setText(Formatter.formatFileSize(getApplicationContext(), bean.getSize()));


                return convertView;
            }
        }
    }

    private class ViewHolder {
        private ImageView drawable;
        private TextView tv_name;
        private TextView tv_isSd;
        private TextView tv_volume;
    }
}
