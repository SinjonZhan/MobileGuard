package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;


public class LostFindActivity extends Activity {

    private TextView tv_safeName;
    private ImageView iv_lostFind;
    private TextView tv_enterGuideInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SpTools.getBoolean(getApplicationContext(), MyConstants.ISSETUP, false)) {
            //进入过向导界面
            initView();
            initData();
            initEvent();

        } else {
            //设置向导界面
            Intent intent = new Intent(LostFindActivity.this, SetUp1Activity.class);
            startActivity(intent);
            finish();//注意关闭界面
        }

    }

    private void initEvent() {
        tv_enterGuideInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LostFindActivity.this, SetUp1Activity.class);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        //显示安全码
        tv_safeName.setText(SpTools.getString(getApplicationContext(),MyConstants.SAFENUMBER,""));

        //判断防盗是否开启
        if (SpTools.getBoolean(getApplicationContext(), MyConstants.ISSETUP, false)) {
            iv_lostFind.setImageResource(R.drawable.lock);
        }else {
            iv_lostFind.setImageResource(R.drawable.unlock);
        }

    }

    private void initView() {
        setContentView(R.layout.activity_lostfind);
        tv_safeName = (TextView) findViewById(R.id.tv_lostfind_getsafename);
        iv_lostFind = (ImageView) findViewById(R.id.iv_islostfind);
        tv_enterGuideInterface = (TextView) findViewById(R.id.tv_enter_guideinterface);
    }

}
