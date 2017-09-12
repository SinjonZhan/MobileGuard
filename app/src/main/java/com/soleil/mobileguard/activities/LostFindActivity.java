package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.utils.EncryptTools;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;


public class LostFindActivity extends Activity {


    private TextView tv_safeName;
    private ImageView iv_lostFind;
    private TextView tv_enterGuideInterface;
    private LinearLayout ll;
    private boolean isPressMenu =false;


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
                finish();
            }
        });
    }

    private void initData() {
        //显示安全码


        tv_safeName.setText(EncryptTools.decrypt(SpTools.getString(getApplicationContext(), MyConstants.SAFENUMBER, ""), MyConstants.MUSIC));

        //判断防盗是否开启
        if (SpTools.getBoolean(getApplicationContext(), MyConstants.ISSETUP, false)) {
            iv_lostFind.setImageResource(R.drawable.lock);
        } else {
            iv_lostFind.setImageResource(R.drawable.unlock);
        }

    }

    private void initView() {
        setContentView(R.layout.activity_lostfind);
        tv_safeName = (TextView) findViewById(R.id.tv_lostfind_getsafename);
        iv_lostFind = (ImageView) findViewById(R.id.iv_islostfind);
        tv_enterGuideInterface = (TextView) findViewById(R.id.tv_enter_guideinterface);
        ll = (LinearLayout) findViewById(R.id.ll_lostfind_menu_button);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (!isPressMenu) {
                ll.setVisibility(View.VISIBLE);

            }else {
                ll.setVisibility(View.GONE);

            }
            isPressMenu = !isPressMenu;

        }
        return super.onKeyDown(keyCode, event);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.lostfind, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    public void pressMunu(View view) {
        showModifyLostFindNameDialog();
    }

//    @Override
//    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.mn_modify_name:
//                Toast.makeText(this, "修改防盗名", Toast.LENGTH_SHORT).show();
//                //弹出一个对话框，让用户自己修改防盗名
//                showModifyLostFindNameDialog();
//                break;
//            case R.id.mn_test_menu:
//                Toast.makeText(this, "测试菜单", Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                break;
//        }
//
//        return super.onMenuItemSelected(featureId, item);
//    }

    private void showModifyLostFindNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(getApplicationContext(), R.layout.menu_modify_name_dialog, null);
        builder.setView(view);
        final EditText et_name = (EditText) view.findViewById(R.id.et_mn_dialog_name);
        Button bt_set_name = (Button) view.findViewById(R.id.et_mn_dialog_set_name);
        Button bt_cancel = (Button) view.findViewById(R.id.et_mn_dialog_cancel);

        final AlertDialog dialog = builder.create();
        bt_set_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_name.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(LostFindActivity.this, "名字不能为空", Toast.LENGTH_SHORT).show();

                } else {

                    SpTools.putString(getApplicationContext(), MyConstants.TITLE, title);
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

        dialog.show();


    }


}
