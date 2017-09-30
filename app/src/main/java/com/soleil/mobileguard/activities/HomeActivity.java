package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.adapter.MyAdapter;
import com.soleil.mobileguard.engine.ReadContactsEngine;
import com.soleil.mobileguard.utils.Md5Utils;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;

public class HomeActivity extends Activity {

    private GridView gv_menus;//主界面的按钮
    private AlertDialog dialog;
    private MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent(); //初始化事件

//        System.out.println("*********"+ PhoneLocationEngine.mobileQuery("15687592222", getApplicationContext()));
        //做测试

        ReadContactsEngine.readContacts(getApplicationContext());

    }

    private void initEvent() {
        gv_menus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //手机防盗
                        if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(), MyConstants.PASSWORD, ""))) {
                            showSetPasswordDialog();
                        } else {
                            showEnterPassDialog();
                        }
                        break;

                    case 1:
                        //通讯卫士
                    {
                        Intent intent = new Intent(HomeActivity.this, TelSmsSafeActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 2:
                        //软件管家
                    {
                        Intent intent = new Intent(HomeActivity.this, AppManagerActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 3:
                        //进程管家
                    {
                        Intent intent = new Intent(HomeActivity.this, TaskManagerActivity.class);
                        startActivity(intent);
                        break;
                    }

                    case 7: {
                        Intent intent = new Intent(HomeActivity.this, AToolActivity.class);
                        startActivity(intent);

                        break;
                    }

                    case 8:
                        //设置中心
                    {

                        Intent intent = new Intent(HomeActivity.this, SettingCenterActivity.class);
                        startActivity(intent);
                        break;
                    }


                    default:
                        break;
                }
            }
        });
    }


    /**
     * 登录密码对话框
     */
    private void showEnterPassDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(getApplicationContext(), R.layout.gv_enter_password_dialog, null);
        builder.setView(view);

        TextView tv_dialog_title = (TextView) view.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText("防盗登录");

        final EditText ed_passwordone = (EditText) view.findViewById(R.id.et_dialog_set_password_one);
        final EditText ed_passwordtwo = (EditText) view.findViewById(R.id.et_dialog_set_password_two);

        Button bt_set_password = (Button) view.findViewById(R.id.et_dialog_set_password);
        Button bt_cancel = (Button) view.findViewById(R.id.et_dialog_cancel);

        ed_passwordtwo.setVisibility(View.GONE);


        bt_set_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passone = ed_passwordone.getText().toString().trim();

                if (TextUtils.isEmpty(passone)) {
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    String finalpass = Md5Utils.Md5(Md5Utils.Md5(passone));
                    if (finalpass.equals(SpTools.getString(getApplicationContext(), MyConstants.PASSWORD, ""))) {
                        //保存密码到sp中
                        Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_LONG).show();
                        return;
                    }
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

    /**
     * 设置密码对话框
     */
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
                    passone = Md5Utils.Md5(Md5Utils.Md5(passone));
                    SpTools.putString(getApplicationContext(), MyConstants.PASSWORD, passone);
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
        adapter = new MyAdapter(getApplicationContext());
        gv_menus.setAdapter(adapter);
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


    @Override
    protected void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }
}
