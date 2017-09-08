package com.soleil.mobileguard.activities;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;

public class SetUp3Activity extends BaseSetupActivity {


    private TextView et_safenumber;
    private String safeNumber;

    @Override
    protected void nextActivity() {
        startActivity(SetUp4Activity.class);

    }

    @Override
    protected void prevActivity() {
        startActivity(SetUp2Activity.class);

    }

    public void initView() {
        setContentView(R.layout.activity_setup3);
        et_safenumber =  (TextView) findViewById(R.id.et_setup3_safenumber);
    }


    @Override
    public void next(View v){
        safeNumber = et_safenumber.getText().toString().trim();
        if(TextUtils.isEmpty(safeNumber)){
            Toast.makeText(getApplicationContext(), "安全号码不能为空", Toast.LENGTH_SHORT).show();
            return ;
        }else {

        SpTools.putString(getApplicationContext(), MyConstants.SAFENUMBER, safeNumber);
        }


        super.next(v);
    }

    @Override
    public void initData() {
        et_safenumber.setText(SpTools.getString(getApplicationContext(),MyConstants.SAFENUMBER,""));
    }

    /**
     * @param view
     * 从手机联系人里获取安全号码
     */
    public void selectSafeNumber(View view) {
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
        String phone = data.getStringExtra(MyConstants.SAFENUMBER);
        et_safenumber.setText(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
