package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.dao.BlackDao;
import com.soleil.mobileguard.domain.BlackBean;
import com.soleil.mobileguard.domain.BlackTable;
import com.soleil.mobileguard.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;


/**
 * 通讯卫士的数据处理，短信和电话
 */

public class TelSmsSafeActivity extends Activity {

    private static final int FINISH = 2;//完成加载数据
    private static final int LOADING = 1;//加载数据
    private final int DATASINBATCHES = 20; //分批加载的数据个数
    List<BlackBean> datas = new ArrayList<>();
    private ListView lv_safeNumber;
    private Button bt_addSafeNumber;
    private TextView tv_nodata;
    private ProgressBar pb_loading;
    private BlackDao dao;
    private Adapter adapter;
    private List<BlackBean> datasInBatches;
    private int lastVisiblePosition;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING://加载数据

                    if (lastVisiblePosition == (dao.getTotalRows() - 1) && lastVisiblePosition >= 10) {

                        Toast.makeText(getApplicationContext(), "到底了", Toast.LENGTH_SHORT).show();

                        return;
                    }
                    pb_loading.setVisibility(View.VISIBLE);
                    lv_safeNumber.setVisibility(View.GONE);
                    tv_nodata.setVisibility(View.GONE);

                    break;
                case FINISH://完成加载数据
                    //判断是否有数据

                    //有数据
                    if (datas.size() != 0 | datasInBatches.size() != 0) {

                        pb_loading.setVisibility(View.GONE);
                        lv_safeNumber.setVisibility(View.VISIBLE);
                        tv_nodata.setVisibility(View.GONE);

                        adapter.notifyDataSetChanged(); //listView中数据发生变化
                    } else {

                        if (datas.size() != 0) {
                            return;
                        }

                        //无数据
                        pb_loading.setVisibility(View.GONE);
                        lv_safeNumber.setVisibility(View.GONE);
                        tv_nodata.setVisibility(View.VISIBLE);

                    }

                    break;
                default:
                    break;

            }
        }
    };
    private PopupWindow pw;
    private ScaleAnimation sa;
    private View popupWindowview; //弹出的窗口加载的视图
    private EditText et_blackUser_phone;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();


        initPopupWindow();
    }


    //点击弹出窗体外部便关闭弹出窗体
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();

        }
        return super.dispatchTouchEvent(ev);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        if (pw != null && pw.isShowing()) {
//            pw.dismiss();
//
//        }
//        return super.onTouchEvent(event);
//    }

    //点击按钮弹出窗体
    public void popupWindow() {


        if (pw != null && pw.isShowing()) {
            pw.dismiss();
        } else {


            int[] location = new int[2];
            bt_addSafeNumber.getLocationInWindow(location);
            pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindowview.startAnimation(sa);

            pw.showAtLocation(bt_addSafeNumber, Gravity.LEFT | Gravity.TOP, location[0], location[1] + bt_addSafeNumber.getHeight());
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String phone = data.getStringExtra(MyConstants.SAFENUMBER);
            addBlackUserBySelf();
            et_blackUser_phone.setText(phone);

        } else {
            //用户点击返回键的事件处理

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initPopupWindow() {
        popupWindowview = View.inflate(getApplicationContext(), R.layout.popup_telsms_addblackuser_menu, null);
        //popupWindwo的组件初始化
        Button bt_addBlackUser_bySelf = (Button) popupWindowview.findViewById(R.id.bt_addblackuser_byself);
        Button bt_addBlackUser_byContacts = (Button) popupWindowview.findViewById(R.id.bt_addblackuser_bycontacts);
        Button bt_addBlackUser_byLog = (Button) popupWindowview.findViewById(R.id.bt_addblackuser_bylog);
        Button bt_addBlackUser_bySms = (Button) popupWindowview.findViewById(R.id.bt_addblackuser_bysms);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_addblackuser_byself:
                        //点击手动添加联系人触发动作
                        addBlackUserBySelf();

                        break;
                    case R.id.bt_addblackuser_bycontacts:
                        //点击导入联系人触发动作
                    {

                        Intent intent = new Intent(TelSmsSafeActivity.this, FriendsActivity.class);
                        startActivityForResult(intent, 1);

                    }
                        break;
                    case R.id.bt_addblackuser_bylog:
                        //点击通过通讯记录联系人触发动作
                    {

                        Intent intent = new Intent(TelSmsSafeActivity.this, ReadTelFriendsActivity.class);
                        startActivityForResult(intent, 1);

                    }
                        break;
                    case R.id.bt_addblackuser_bysms:
                        //点击通过记录联系人触发动作
                    {

                        Intent intent = new Intent(TelSmsSafeActivity.this, ReadSmsFriendsActivity.class);
                        startActivityForResult(intent, 1);

                    }
                        break;

                }
            }
        };


        bt_addBlackUser_bySelf.setOnClickListener(listener);
        bt_addBlackUser_byContacts.setOnClickListener(listener);
        bt_addBlackUser_byLog.setOnClickListener(listener);
        bt_addBlackUser_bySms.setOnClickListener(listener);


        pw = new PopupWindow(popupWindowview, -2, -2);

        sa = new ScaleAnimation(1.0f, 1.0f, 0f, 1.0f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0f);
        sa.setDuration(1000);
    }

    /**
     * 手动添加黑名单
     */
    private void addBlackUserBySelf() {
        //加载手动添加黑名单的对话框
        View view = View.inflate(getApplicationContext(), R.layout.dialog_telsms_add_blackuser, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(TelSmsSafeActivity.this);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        Button bt_addBlackUser = (Button) view.findViewById(R.id.bt_telsms_add_blackuser);
        Button bt_cancelAddBlackUser = (Button) view.findViewById(R.id.bt_telsms_cancel_add_blackuser);

        et_blackUser_phone = (EditText) view.findViewById(R.id.et_balckuser_phone);
        final CheckBox intercept_sms = (CheckBox) view.findViewById(R.id.cb_telsms_intercept_sms);
        final CheckBox intercept_tel = (CheckBox) view.findViewById(R.id.cb_telsms_intercept_tel);

        bt_addBlackUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加到黑名单
                int intercept_mode = 0;
                String phone = et_blackUser_phone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(TelSmsSafeActivity.this, "黑名单号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!intercept_sms.isChecked() && !intercept_tel.isChecked()) {

                    Toast.makeText(TelSmsSafeActivity.this, "至少选中一种模式", Toast.LENGTH_SHORT).show();

                    return;
                }

                if (intercept_sms.isChecked()) {
                    intercept_mode |= BlackTable.SMS;
                }

                if (intercept_tel.isChecked()) {
                    intercept_mode |= BlackTable.TEL;

                }

                BlackBean bean = new BlackBean(et_blackUser_phone.getText().toString().trim(), intercept_mode);


                dao.add(bean);
                datas.remove(bean); //靠equals()和hashCode()方法判断数据是否一致

                adapter.notifyDataSetChanged();


                datas.add(0, bean);

                adapter.notifyDataSetChanged();

                handler.obtainMessage(FINISH).sendToTarget();

                dialog.dismiss();

            }
        });
        bt_cancelAddBlackUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

//        builder.setCancelable(true);
        dialog.setCancelable(false);
        dialog.show();
    }


    private void initEvent() {

        bt_addSafeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow();
            }
        });

        //给ListView设置滑动时间
        lv_safeNumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            /**
             * @param view
             * @param scrollState
             * 根据状态做出相应动作
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    lastVisiblePosition = view.getLastVisiblePosition();
//                    System.out.println("---------------"+lastVisiblePosition+"---------------");
                    if (lastVisiblePosition == datas.size() - 1) {
                        initData();


                    }
                }
            }

            /**
             * @param view
             * @param firstVisibleItem
             * @param visibleItemCount
             * @param totalItemCount
             *
             *      按住滑动
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    private void initData() {
        new Thread() {
            public void run() {

                //取数据之前，发消息 显示加载
                handler.obtainMessage(LOADING).sendToTarget();
                //取数据
                //SystemClock.sleep(2000);
                // 加载分批数据
                datasInBatches = dao.getMoreDatas(DATASINBATCHES, datas.size());

                datas.addAll(datasInBatches);

                //发消息取数据完成
                handler.obtainMessage(FINISH).sendToTarget();


            }
        }.start();
    }

    private void initView() {
        setContentView(R.layout.activity_telsmssafe);
        lv_safeNumber = (ListView) findViewById(R.id.lv_telsms_safenumber);
        bt_addSafeNumber = (Button) findViewById(R.id.bt_telsms_addsafenumber);
        tv_nodata = (TextView) findViewById(R.id.tv_telsms_nodata);
        pb_loading = (ProgressBar) findViewById(R.id.pb_telsms_loading);

        dao = new BlackDao(getApplicationContext());
        adapter = new Adapter();

        lv_safeNumber.setAdapter(adapter);


    }


    private class Adapter extends BaseAdapter {


        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final BlackBean bean;
            if (convertView == null) {

                convertView = View.inflate(getApplicationContext(), R.layout.item_telsmssafe_listview, null);

                holder = new ViewHolder();
                holder.tv_safeNumber = (TextView) convertView.findViewById(R.id.tv_telsmssafe_listview_item_safenumber);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_telsmssafe_listview_item_mode);
                holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_telsmssafe_listview_item_delete);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            bean = datas.get(position);

            holder.tv_safeNumber.setText(bean.getPhone());

            switch (bean.getMode()) {
                case BlackTable.SMS:
                    holder.tv_mode.setText("短信拦截");
                    break;
                case BlackTable.TEL:
                    holder.tv_mode.setText("电话拦截");
                    break;
                case BlackTable.ALL:
                    holder.tv_mode.setText("全部拦截");
                    break;

            }

            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(TelSmsSafeActivity.this);
                    builder.setTitle("注意").setMessage("是否删除该数据").setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dao.delete(bean.getPhone());

                            datas.remove(position);

                            adapter.notifyDataSetChanged();

                            Log.d("TelSmsSafeActivity", datas.size() + "");

                            if (datas.size() == 0) {
                                initData();

                            }

                        }
                    }).setNegativeButton("取消", null).show();


                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView tv_safeNumber;
            TextView tv_mode;
            ImageView iv_delete;
        }


    }
}
