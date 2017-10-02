package com.soleil.popupwindowdemo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.PopupWindow;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class MainActivity extends Activity {

    private PopupWindow pw;
    private View view;
    private ScaleAnimation sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        view = View.inflate(getApplicationContext(), R.layout.popup, null);

        pw = new PopupWindow(view, -2, -2);

        sa = new ScaleAnimation(1.0f, 1.0f, 0f, 1.0f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0f);
        sa.setDuration(1000);

    }

    @Override
    protected void onDestroy() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
            pw = null;
        }
        super.onDestroy();
    }

    public void popupWindow(View v) {

        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        } else {


            int[] location = new int[2];
            v.getLocationInWindow(location);
            pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            view.startAnimation(sa);

            pw.showAtLocation(v, Gravity.LEFT | Gravity.TOP, location[0] + v.getWidth(), location[1] + v.getHeight());
        }

    }
}
