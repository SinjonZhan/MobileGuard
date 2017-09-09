package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.domain.Contact;
import com.soleil.mobileguard.engine.ReadContactsEngine;
import com.soleil.mobileguard.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends Activity {

    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private List<Contact> datas = new ArrayList<>();
    private ProgressDialog pd;
    private MyAdapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pd = new ProgressDialog(FriendsActivity.this);
                    pd.setTitle("注意");
                    pd.setMessage("正在玩命加载数据......");
                    pd.show();


                    break;
                case FINISH:
                    if (pd != null) {
                        pd.dismiss();
                        pd = null;

                    }
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;

            }
        }
    };
    private ListView lv_datas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview_contacts);
        lv_datas = (ListView) findViewById(R.id.lv_contact);
        adapter = new MyAdapter();
        lv_datas.setAdapter(adapter);

        initData(); //初始化数据

        initEvent();//初始化事件

    }

    private void initEvent() {
        lv_datas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact data = datas.get(position);
                String phone = data.getPhone();
                Intent intent = new Intent();
                intent.putExtra(MyConstants.SAFENUMBER, phone);
                setResult(1, intent);

                finish();//关闭自己

            }
        });
    }

    private void initData() {
        //读取 数据  本地或网络都存在耗时
        //子线程访问数据
        new Thread() {
            public void run() {
                Message msg = new Message();
                msg.what = LOADING;
                handler.sendMessage(msg);

                SystemClock.sleep(2000);//睡眠一下 看进度框

                datas = ReadContactsEngine.readContacts(getApplicationContext());

                //数据获取完成,发送数据加载完成的消息
                msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private class MyAdapter extends BaseAdapter {

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
            View view = View.inflate(getApplicationContext(), R.layout.item_friendsativity_listview, null);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_friend_listview_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_friend_listview_phone);
            Contact contact = datas.get(position);
            tv_name.setText(contact.getName());
            tv_phone.setText(contact.getPhone());

            return view;
        }
    }
}


