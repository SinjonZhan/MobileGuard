package com.soleil.mobileguard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


public class MyTextView extends TextView {
    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() { //获取焦点
        return true;
    }
}
