package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.domain.AppBean;
import com.soleil.mobileguard.engine.AppManagerEngine;
import com.soleil.mobileguard.engine.FlowEngine;

import java.util.ArrayList;
import java.util.List;


public class FlowActivity extends Activity {

    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private ListView lv_flowDatas;

    //    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case LOADING:
//
//
//                    break;
//                case FINISH:
//
//
//                    break;
//                default:
//                    break;
//
//            }
//        }
//    };

    private MyAdapter adapter;
    private List<AppBean> allApk = new ArrayList<>();
    private ConnectivityManager cm;

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            adapter.notifyDataSetChanged();
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                allApk = AppManagerEngine.getAllApk(getApplicationContext());
                for (int i = 0; i < allApk.size(); i++) {
                    AppBean bean = allApk.get(i);
                    if (FlowEngine.getSend(getApplicationContext(), bean.getUid()) == null) {
                        allApk.remove(i);
                        i--;
                    }
                }
                handler.obtainMessage().sendToTarget();
            }
        }).start();


    }

    private void initView() {
        setContentView(R.layout.activity_flow);
        lv_flowDatas = (ListView) findViewById(R.id.lv_flow);
        adapter = new MyAdapter();
        lv_flowDatas.setAdapter(adapter);

        //流量信息管理类
        cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);


    }

    /**
     * 显示流量信息的对话框
     */
    private void showFlowMess(String mess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("流量信息")
                .setMessage(mess)
                .setPositiveButton("确定", null);
        builder.show();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return allApk.size();
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

            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_flow_listview_item, null);
                holder = new ViewHolder();
                holder.drawable = (ImageView) convertView.findViewById(R.id.iv_drawable);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.iv_flowInfos = (ImageView) convertView.findViewById(R.id.iv_flowinfo);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final AppBean bean = allApk.get(position);
            holder.drawable.setImageDrawable(bean.getIcon());
            holder.tv_name.setText(bean.getName());
            holder.iv_flowInfos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.i("flow", "有反应");
                    //接收的流量
                    String rev = FlowEngine.getReceiver(getApplicationContext(), bean.getUid());

                    String snd = FlowEngine.getSend(getApplicationContext(), bean.getUid());

                    showFlowMess(cm.getActiveNetworkInfo().getTypeName() + "\n" + "接收的流量" + rev + "\n发送流量" + snd);

                }
            });


            return convertView;
        }
    }

    class ViewHolder {
        private ImageView drawable;
        private TextView tv_name;

        private ImageView iv_flowInfos;
    }
}




