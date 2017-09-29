package com.soleil.mobileguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.engine.PhoneLocationEngine;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;


/**
 * 监控电话，显示归属地
 */
public class ComingPhoneService extends Service {

    int bgStyle[] = new int[]{R.drawable.call_locate_green, R.drawable.call_locate_blue, R.drawable.call_locate_gray, R.drawable.call_locate_orange, R.drawable.call_locate_white};
    private TelephonyManager tm;
    private PhoneStateListener listener;
    private WindowManager.LayoutParams params;
    private WindowManager wm;
    private View mView;
    private OutCallReceiver outCallReceiver;
    /**
     * 是否外拨
     */
    private boolean isOutCall = false;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (mView != null) {
                wm.removeView(mView);
                mView = null;

            }

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        //监听外拨 注册广播
        outCallReceiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(outCallReceiver, filter);


        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        //初始化吐司参数
        initToastParams();

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE: //空闲
                        closeLocationToast();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING://响铃

                        showLocationToast(incomingNumber);

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK: //通话

                        closeLocationToast();

                        break;

                }

                super.onCallStateChanged(state, incomingNumber);
            }


        };
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);


        super.onCreate();
    }

    /**
     * 初始化吐司参数
     */
    private void initToastParams() {
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
//        params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        params.x = (int) Float.parseFloat(SpTools.getString(getApplicationContext(), MyConstants.TOASTX, "0"));
        params.y = (int) Float.parseFloat(SpTools.getString(getApplicationContext(), MyConstants.TOASTY, "0"));

        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;//吐司天生无事件,要改变类型
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        ;

    }

    /**
     * 关闭吐司
     */
    private void closeLocationToast() {
        if (isOutCall) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(3000);
                    handler.obtainMessage().sendToTarget();
                }
            }).start();
            isOutCall =false;
        } else {

        if (mView != null) {
            wm.removeViewImmediate(mView);
            mView = null;

        }
        }
    }

    /**
     * 显示吐司
     *
     * @param incomingNumber
     */
    private void showLocationToast(String incomingNumber) {
        if (!isOutCall)
            closeLocationToast();
        //显示自定义吐司
        mView = View.inflate(getApplicationContext(), R.layout.sys_toast, null);
        int whichStyle = Integer.parseInt(SpTools.getString(getApplicationContext(), MyConstants.STYLEINDEX, "0"));
        mView.setBackgroundResource(bgStyle[whichStyle]);
        mView.setOnTouchListener(new View.OnTouchListener() {

            private float startY;
            private float startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN://按下
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE://移动

                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        float dx = endX - startX;
                        float dy = endY - startY;

                        params.x += dx;
                        params.y += dy;
                        startX = endX;
                        startY = endY;

                        wm.updateViewLayout(mView, params);

                        break;
                    case MotionEvent.ACTION_UP://松开

                        if (params.x < 0) {
                            params.x = 0;
                        } else if (params.x + mView.getWidth() > wm.getDefaultDisplay().getWidth()) {
                            params.x = wm.getDefaultDisplay().getWidth() - mView.getWidth();
                        }

                        if (params.y < 0) {
                            params.y = 0;
                        } else if (params.y + mView.getHeight() > wm.getDefaultDisplay().getHeight()) {
                            params.y = wm.getDefaultDisplay().getHeight() - mView.getHeight();
                        }

                        SpTools.putString(getApplicationContext(), MyConstants.TOASTX, params.x + "");
                        SpTools.putString(getApplicationContext(), MyConstants.TOASTY, params.y + "");

                        break;

                }
                return false;
            }
        });
        TextView tv_location = (TextView) mView.findViewById(R.id.tv_toast_location);
        tv_location.setText(PhoneLocationEngine.locationQuery(incomingNumber, getApplicationContext()));
        wm.addView(mView, params);
    }



    @Override
    public void onDestroy() {
        //监听外拨 注册广播
        unregisterReceiver(outCallReceiver);

        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

    /**
     * 外拨电话的广播
     */
    private class OutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isOutCall = true;
            String phoneNumber = getResultData();
            showLocationToast(phoneNumber);//显示外拨电话的吐司
        }
    }
}
