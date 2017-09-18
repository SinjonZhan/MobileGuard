package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.dao.BlackDao;
import com.soleil.mobileguard.domain.BlackBean;
import com.soleil.mobileguard.domain.BlackTable;

import java.util.ArrayList;
import java.util.List;


/**
 * 通讯卫士的数据处理，短信和电话
 */

public class TelSmsSafeActivityPage extends Activity {

    private static final int FINISH = 2;//完成加载数据
    private static final int LOADING = 1;//加载数据
    private final int perPage = 20;//每一页显示的数据
    List<BlackBean> datas = new ArrayList<>();
    private ListView lv_safeNumber;
    private Button bt_addSafeNumber;
    private TextView tv_nodata;
    private ProgressBar pb_loading;
    private BlackDao dao;
    private Adapter adapter;
    private int totalPage;//总页数
    private int currentPage = 1;//当前页默认1
    private EditText et_assignPage;
    private TextView tv_showCurrentPage;
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

                        //显示当前页和总页值
                        tv_showCurrentPage.setText(currentPage + "/" + totalPage);

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
//                SystemClock.sleep(1000);

                datas = dao.getPageDatas(currentPage, perPage);

                totalPage = dao.getTotalPage(perPage);

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

        et_assignPage = (EditText) findViewById(R.id.et_telsms_gotopage);

        tv_showCurrentPage = (TextView) findViewById(R.id.tv_telsms_totalpage);

    }

    public void PrevPage(View view) {
        currentPage--;

        //处理越界
        currentPage = currentPage % totalPage;

        initData();
    }

    public void nextPage(View view) {
        currentPage++;

        //处理越界
        if (currentPage == 0) {
            currentPage = totalPage;
        }
        initData();
    }

    public void endPage(View view) {
        currentPage = totalPage;
        initData();
    }

    public void jumpPage(View view) {
        String jumpPageStr = et_assignPage.getText().toString().trim();
        if (TextUtils.isEmpty(jumpPageStr)) {
            Toast.makeText(getApplicationContext(), "跳转页不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        currentPage = Integer.parseInt(jumpPageStr);

        if (currentPage >= 1 && currentPage <= totalPage) {

            initData();
        }else{
            Toast.makeText(getApplicationContext(), "请按套路出牌", Toast.LENGTH_SHORT).show();
        }

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
            ViewHolder holder;
            BlackBean bean;
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


            return convertView;
        }

        class ViewHolder {
            TextView tv_safeNumber;
            TextView tv_mode;
            ImageView iv_delete;
        }


    }
}
