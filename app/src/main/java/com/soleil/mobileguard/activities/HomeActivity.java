package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.soleil.mobileguard.R;


public class HomeActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_home);
    }
}
