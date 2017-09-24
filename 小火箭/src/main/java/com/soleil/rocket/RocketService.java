package com.soleil.rocket;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class RocketService extends Service {

    private WindowManager.LayoutParams params;
    private WindowManager wm;
    private View view;
    private Handler hander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    wm.updateViewLayout(view, params);
                    break;
                default:
                    break;
        }
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initToastParams() {
        // TODO Auto-generated method stub
        // XXX This should be changed to use a Dialog, with a Theme.Toast
        // defined that sets up the layout params appropriately.

        params = new WindowManager.LayoutParams();
        ;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        //对齐方式左上角
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		/* | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE */
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //初始化土司的位置
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 土司天生不相应时间,改变类型
        params.setTitle("Toast");
    }
    private void showRocket(){

        view = View.inflate(getApplicationContext(), R.layout.rocket, null);
        AnimationDrawable ad = (AnimationDrawable) view.findViewById(R.id.iv_rocket).getBackground();
        ad.start();



        view.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private float startY;
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

                        hander.obtainMessage(1).sendToTarget();

                        break;
                    case MotionEvent.ACTION_UP://松开 发射



                        if (params.x > 100 && (params.x + view.getWidth()) < wm.getDefaultDisplay().getWidth() - 100 && params.y > 200 ) {

                            params.x = (wm.getDefaultDisplay().getWidth()-view.getWidth())/2;

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for(int j = 0; j<view.getHeight();) {
                                        SystemClock.sleep(50);
                                        params.y-=j;
                                        j+=5;
                                        hander.obtainMessage(1).sendToTarget();

                                    }
                                    stopSelf();
                                }
                            }).start();




                            Intent intent = new Intent(RocketService.this, SmokeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//在任务栈中显示
                            startActivity(intent);


                        }


                        break;

                }
                return false;
            }
        });

        wm.addView(view, params);

    }
    private void closeRocket(){
        if (view != null) {

        wm.removeView(view);
        }
    }

    @Override
    public void onCreate() {

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        initToastParams();
        showRocket();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        closeRocket();
        super.onDestroy();
    }


}
