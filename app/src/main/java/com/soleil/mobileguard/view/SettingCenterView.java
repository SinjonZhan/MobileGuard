package com.soleil.mobileguard.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soleil.mobileguard.R;


public class SettingCenterView extends LinearLayout {

    private TextView tv_title;
    private TextView tv_content;
    private CheckBox cb_auto_update;
    private String[] split;
    private View item;


    public SettingCenterView(Context context) {
        super(context);
        initView();
    }

    public SettingCenterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initEvent();
        String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.soleil.mobileguard", "title");
        String content = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.soleil.mobileguard", "content");

        tv_title.setText(title);
        split = content.split("-");


    }

    public SettingCenterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();


    }

    private void initEvent() {
        item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_auto_update.isChecked()) {
                    cb_auto_update.setChecked(false);
                }else{
                    cb_auto_update.setChecked(true);

                }
            }
        });

        cb_auto_update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_content.setTextColor(Color.parseColor("#7700ff00"));
                    tv_content.setText(split[1]);


                } else {
                    tv_content.setTextColor(Color.parseColor("#ff0000"));
                    tv_content.setText(split[0]);

                }
            }
        });
    }


    private void initView() {
        item = View.inflate(getContext(), R.layout.item_settingcenter_view, null);
        addView(item);
        tv_title = (TextView) findViewById(R.id.tv_setting_center_auto_update_title);
        tv_content = (TextView) findViewById(R.id.tv_setting_center_auto_update_content);
        cb_auto_update = (CheckBox) findViewById(R.id.cb_setting_center_auto_update_checked);


    }

}
