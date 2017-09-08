package com.soleil.mobileguard.activities;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;

public class SetUp2Activity extends BaseSetupActivity {

    private Button bt_bind;
    private ImageView iv_isBind;

    public void initView() {
        setContentView(R.layout.activity_setup2);
        bt_bind = (Button) findViewById(R.id.bt_setup2_bindsim);
        iv_isBind = (ImageView) findViewById(R.id.iv_setup2_isbind);
    }

    @Override
    public void initData() {
        if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(), MyConstants.SIM, ""))) {
            iv_isBind.setImageResource(R.drawable.unlock);
        }else{
            iv_isBind.setImageResource(R.drawable.lock);
        }

    }

    public void initEvent() {
        bt_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(), MyConstants.SIM, ""))) {

                    {
                        //读取sim卡
                        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        String serialNumber = tm.getSimSerialNumber();
                        SpTools.putString(getApplicationContext(), MyConstants.SIM, serialNumber);

                    }

                    {
                        //切换图标
                        iv_isBind.setImageResource(R.drawable.lock);
                        Toast.makeText(getApplicationContext(), "绑定成功", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    SpTools.putString(getApplicationContext(), MyConstants.SIM, "");
                    iv_isBind.setImageResource(R.drawable.unlock);
                    Toast.makeText(getApplicationContext(), "解绑成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void next(View v) {
        if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(), MyConstants.SIM, ""))) {
            Toast.makeText(getApplicationContext(), "未绑定sim卡", Toast.LENGTH_SHORT).show();
            return;

        }
        super.next(v);
    }

    @Override
    protected void nextActivity() {

        startActivity(SetUp3Activity.class);
    }

    @Override
    protected void prevActivity() {
        startActivity(SetUp1Activity.class);

    }


}
