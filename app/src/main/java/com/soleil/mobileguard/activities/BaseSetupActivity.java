package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.soleil.mobileguard.R;


public abstract class BaseSetupActivity extends Activity {

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initGesture();
        initData();//初始化数据
        initEvent();//初始化事件


    }

    public void initData() {

    }

    public void initEvent() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void initGesture() {
        gestureDetector = new GestureDetector(new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (velocityX > 200) {
                    float dx = e2.getX() - e1.getX();
                    if (Math.abs(dx) < 100) {
                        return true;
                    }
                    if (dx < 0) {
                        next(null);
                    } else {
                        prev(null);
                    }
                }
                return true;
            }
        });
    }


    public abstract void initView();

    public void next(View view) {
        //完成界面切换
        //实现动画
//        Intent intent = new Intent(this, Activity.class);


        nextActivity();

        nextAnimation();//实现动画

    }

    protected abstract void nextActivity();

    protected abstract void prevActivity();

    public void startActivity(Class type) {
        Intent intent = new Intent(this, type);
        startActivity(intent);
        finish();
    }

    public void prev(View view) {

        prevActivity();

        prevAnimation();
    }

    private void nextAnimation() {
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    private void prevAnimation() {
        overridePendingTransition(R.anim.prev_in, R.anim.prev_out);
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "设置向导未完成\n\t可重新设置", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
}
