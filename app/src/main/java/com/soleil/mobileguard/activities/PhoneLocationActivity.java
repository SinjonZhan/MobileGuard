package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.engine.PhoneLocationEngine;


/**
 * 手机电话归属地查询界面
 */
public class PhoneLocationActivity extends Activity {

    private EditText et_phone;
    private TextView tv_address;
    private Button bt_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initEvent();
    }

    private void initEvent() {
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                locationQuery();
            }
        });

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationQuery();

            }
        });
    }

    private void locationQuery() {
        String phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            //抖动效果
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            et_phone.startAnimation(shake);
            //震动效果
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{200, 300, 300, 200, 500, 100}, 3);
            return;
        }
        String location = PhoneLocationEngine.locationQuery(phone, getApplicationContext());
        tv_address.setText("归属地:" + location);
    }

    private void initView() {
        setContentView(R.layout.activity_phonelocation);
        bt_search = (Button) findViewById(R.id.bt_phonelocation_search);
        et_phone = (EditText) findViewById(R.id.et_phonelocation_number);
        tv_address = (TextView) findViewById(R.id.tv_phonelocation_address);

    }
}
