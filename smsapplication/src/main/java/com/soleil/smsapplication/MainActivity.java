package com.soleil.smsapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText et_sms_to;
    private EditText et_sms_content;
    private Button bt_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        initView();
        initEvent();
    }

    private void initEvent() {
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(et_sms_to.getText().toString(), null, et_sms_content.getText().toString(), null, null);
            }
        });
    }

    private void initView() {
        et_sms_to = (EditText) findViewById(R.id.et_sms_to);
        et_sms_content = (EditText) findViewById(R.id.et_sms_content);
        bt_send = (Button) findViewById(R.id.bt_send);

    }
}
