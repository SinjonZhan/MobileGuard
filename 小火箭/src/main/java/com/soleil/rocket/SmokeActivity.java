package com.soleil.rocket;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;


public class SmokeActivity extends Activity {

    private ImageView iv_smoke_t;
    private ImageView iv_smoke_m;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);

        aa.setDuration(1000);
        iv_smoke_t.startAnimation(aa);
        iv_smoke_m.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        aa.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        }).start();
    }

    private void initView() {
        setContentView(R.layout.smoke);
        iv_smoke_t = (ImageView) findViewById(R.id.iv_smoke_t);
        iv_smoke_m = (ImageView) findViewById(R.id.iv_smoke_m);
    }
}
