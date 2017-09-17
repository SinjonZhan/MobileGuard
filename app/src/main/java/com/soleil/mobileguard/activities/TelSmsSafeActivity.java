package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.dao.BlackDao;
import com.soleil.mobileguard.domain.BlackBean;
import com.soleil.mobileguard.domain.BlackTable;

import java.util.ArrayList;
import java.util.List;


/**
 * 通讯卫士的数据处理，短信和电话
 */

public class TelSmsSafeActivity extends Activity {

    private static final int FINISH = 2;//完成加载数据
    private static final int LOADING = 1;//加载数据
    List<BlackBean> datas = new ArrayList<>();
    private ListView lv_safeNumber;
    private Button bt_addSafeNumber;
    private TextView tv_nodata;
    private ProgressBar pb_loading;
    private BlackDao dao;
    private Adapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING://加载数据
                    pb_loading.setVisibility(View.VISIBLE);
                    lv_safeNumber.setVisibility(View.GONE);
                    tv_nodata.setVisibility(View.GONE);
                    break;
                case FINISH://完成加载数据
                    //判断是否有数据

                    //有数据
                    if (datas.size() != 0) {

                        pb_loading.setVisibility(View.GONE);
                        lv_safeNumber.setVisibility(View.VISIBLE);
                        tv_nodata.setVisibility(View.GONE);

                        adapter.notifyDataSetChanged(); //listView中数据发生变化
                    } else {


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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();


    }

    private void initData() {
        new Thread() {
            public void run() {

                //取数据之前，发消息 显示加载
                handler.obtainMessage(LOADING).sendToTarget();
                //取数据
                SystemClock.sleep(2000);

                datas = dao.getAllDatas();
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
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = View.inflate(getApplicationContext(), R.layout.item_telsmssafe_listview, null);
            TextView tv_safeNumber = (TextView) view.findViewById(R.id.tv_telsmssafe_listview_item_safenumber);
            TextView tv_mode = (TextView) view.findViewById(R.id.tv_telsmssafe_listview_item_mode);
            ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_telsmssafe_listview_item_delete);


            BlackBean bean = datas.get(position);
            tv_safeNumber.setText(bean.getPhone());

            switch (bean.getMode()) {
                case BlackTable.SMS:
                    tv_mode.setText("短信拦截");
                    break;
                case BlackTable.TEL:
                    tv_mode.setText("电话拦截");
                    break;
                case BlackTable.ALL:
                    tv_mode.setText("全部拦截");
                    break;

            }


            return view;
        }
    }
}
