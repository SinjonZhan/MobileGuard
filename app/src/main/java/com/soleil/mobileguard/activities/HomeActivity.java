package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.adapter.MyAdapter;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;

public class HomeActivity extends Activity {

    private GridView gv_menus;//主界面的按钮
    private AlertDialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent(); //初始化事件
    }

    private void initEvent() {
        gv_menus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //手机防盗
                        showSetPasswordDialog();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showSetPasswordDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(getApplicationContext(), R.layout.gv_enter_password_dialog, null);
        builder.setView(view);

        final EditText ed_passwordone = (EditText) view.findViewById(R.id.et_dialog_set_password_one);
        final EditText ed_passwordtwo = (EditText) view.findViewById(R.id.et_dialog_set_password_two);

        Button bt_set_password = (Button) view.findViewById(R.id.et_dialog_set_password);
        Button bt_cancel = (Button) view.findViewById(R.id.et_dialog_cancel);

        bt_set_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passone = ed_passwordone.getText().toString().trim();
                String passtwo = ed_passwordtwo.getText().toString().trim();
                if (TextUtils.isEmpty(passone) || TextUtils.isEmpty(passtwo)) {
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                } else if (!passone.equals(passtwo)) {
                    Toast.makeText(getApplicationContext(), "密码不一致", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    //保存密码到sp中
                    Toast.makeText(getApplicationContext(), "保存密码成功", Toast.LENGTH_LONG).show();

                    SpTools.putString(getApplicationContext(), MyConstants.PASSWORD, passone );
                    dialog.dismiss();
                }
            }

        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();


    }

    private void initData() {
        gv_menus.setAdapter(new MyAdapter(getApplicationContext()));
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.btn_green_normal);
//        ImageView iv = new ImageView(this);
//        iv.setImageBitmap(bitmap);
//        iv.setScaleType(ImageView.ScaleType.FIT_XY);
//        gv_menus.setSelector(iv.getDrawable());
    }

    private void initView() {
        setContentView(R.layout.activity_home);
        gv_menus = (GridView) findViewById(R.id.gv_home_menus);
    }
}
