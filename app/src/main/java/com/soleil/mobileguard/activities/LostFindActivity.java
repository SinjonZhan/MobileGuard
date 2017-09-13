package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.utils.EncryptTools;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;

import static android.view.animation.Animation.RELATIVE_TO_SELF;


public class LostFindActivity extends Activity {


    private TextView tv_safeName;
    private ImageView iv_lostFind;
    private TextView tv_enterGuideInterface;


    private PopupWindow pw;
    private View view;
    private ScaleAnimation sa;
    private Button bt_change_lostFind_name;
    private View bt_popupWindow;
    private Button bt_popup_window;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SpTools.getBoolean(getApplicationContext(), MyConstants.ISSETUP, false)) {
            //进入过向导界面
            initView();
            initData();
            initEvent();
            initPopupWindow();

        } else {
            //设置向导界面
            Intent intent = new Intent(LostFindActivity.this, SetUp1Activity.class);
            startActivity(intent);
            finish();//注意关闭界面
        }

    }

    private void initPopupWindow() {
        view = View.inflate(getApplicationContext(), R.layout.popup_lostfind_menu, null);

        bt_change_lostFind_name = (Button) view.findViewById(R.id.bt_change_lostfind_name);
        bt_change_lostFind_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifyLostFindNameDialog();
            }
        });


        pw = new PopupWindow(view, -2, -2);

        sa = new ScaleAnimation(1.0f, 1.0f, 0f, 1.0f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0f);
        sa.setDuration(1000);
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
        bt_popup_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow();
            }
        });
//        bt_popup_window.setFocusable(true);

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

    //点击按钮弹出窗体
    public void popupWindow() {


        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        } else {


            int[] location = new int[2];
            bt_popup_window.getLocationInWindow(location);
            pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            view.startAnimation(sa);

            pw.showAtLocation(bt_popup_window, Gravity.LEFT | Gravity.TOP, location[0], location[1] + bt_popup_window.getHeight());
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();

        }
        return super.onTouchEvent(event);
    }
    @Override
    protected void onDestroy() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
            pw = null;
        }
        super.onDestroy();
    }

    private void initView() {
        setContentView(R.layout.activity_lostfind);
        tv_safeName = (TextView) findViewById(R.id.tv_lostfind_getsafename);
        iv_lostFind = (ImageView) findViewById(R.id.iv_islostfind);
        tv_enterGuideInterface = (TextView) findViewById(R.id.tv_enter_guideinterface);
        bt_popup_window = (Button) findViewById(R.id.bt_popup_window);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {

            popupWindow();


        }
        return super.onKeyDown(keyCode, event);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.lostfind, menu);
//        return super.onCreateOptionsMenu(menu);
//    }


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
