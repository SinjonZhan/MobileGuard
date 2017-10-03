package com.soleil.mobileguard.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.dao.LockDao;
import com.soleil.mobileguard.domain.AppBean;
import com.soleil.mobileguard.engine.AppManagerEngine;

import java.util.ArrayList;
import java.util.List;


public class BaseUnlockOrLockFragment extends Fragment {
    protected static final int LOADING = 1;
    protected static final int FINISH = 2;

    protected TextView tv_unlocked_lab;
    protected ListView lv_datas;
    protected List<AppBean> unlockedSysDatas = new ArrayList<>();//系统软件数据
    protected List<AppBean> unlockedUserDatas = new ArrayList<>();//用户软件数据
    protected List<AppBean> allApks;
    protected TextView tv_framelayout_lab;
    protected ProgressBar pb_loading;
    protected LockDao dao;
    protected MyAdapter adapter;
    protected List<String> allLockedPacks;//所有加锁app的包名
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_loading.setVisibility(View.VISIBLE);
                    tv_framelayout_lab.setVisibility(View.GONE);
                    lv_datas.setVisibility(View.GONE);
                    tv_unlocked_lab.setVisibility(View.GONE);


                    break;
                case FINISH:
                    pb_loading.setVisibility(View.GONE);
                    setLockedNumberTextView();
                    tv_unlocked_lab.setVisibility(View.VISIBLE);

                    tv_framelayout_lab.setText("个人软件" + "(" + unlockedUserDatas.size() + ")");
                    tv_framelayout_lab.setVisibility(View.VISIBLE);
                    lv_datas.setVisibility(View.VISIBLE);

                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    public List<String> getAllLockedPacks() {
        return allLockedPacks;
    }

    public void setAllLockedPacks(List<String> allLockedPacks) {
        this.allLockedPacks = allLockedPacks;
    }

    protected void setLockedNumberTextView() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //数据业务处理
        dao = new LockDao(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        initData();
        initEvent();

        super.onStart();
    }

    protected boolean isMyDatas(String packName) {
        return false;

    }
    private void initEvent() {
        lv_datas.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= (unlockedUserDatas.size() + 1)) {
                    tv_framelayout_lab.setText("系统软件" + "(" + unlockedSysDatas.size() + ")");
                } else {
                    tv_framelayout_lab.setText("个人软件" + "(" + unlockedUserDatas.size() + ")");

                }
            }
        });
    }


    protected void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (new Object()) {
                    handler.obtainMessage(LOADING).sendToTarget();

                    unlockedSysDatas.clear();
                    unlockedUserDatas.clear();

                    //取数据

                    allApks = AppManagerEngine.getAllApk(getActivity());


                    for (AppBean bean : allApks) {
                        if (isMyDatas(bean.getPackageName())) {
                            if (bean.isSystem()) {
                                unlockedSysDatas.add(bean);

                            } else {
                                unlockedUserDatas.add(bean);

                            }
                        }
                    }


                    handler.obtainMessage(FINISH).sendToTarget();

                }
            }
        }).start();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //加载布局
        View root = inflater.inflate(R.layout.fragment_unlocked, null);

        tv_unlocked_lab = (TextView) root.findViewById(R.id.tv_fragment_unlocked_lab);
        lv_datas = (ListView) root.findViewById(R.id.lv_fragment_unlocked_datas);
        tv_framelayout_lab = (TextView) root.findViewById(R.id.tv_frame_unlocked_lab);
        pb_loading = (ProgressBar) root.findViewById(R.id.pb_frame_loading);

        //适配器
        adapter = new MyAdapter();
        lv_datas.setAdapter(adapter);


        return root;
    }

    protected void setImageViewEventAndBg(ImageView iv_lock, View convertView, String packageName) {

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return unlockedSysDatas.size() + unlockedUserDatas.size() + 2;
        }

        @Override
        public AppBean getItem(int position) {
            try {

                if (position <= unlockedUserDatas.size()) {
                    return unlockedUserDatas.get(position - 1);
                } else {
                    return unlockedSysDatas.get(position - 2 - unlockedUserDatas.size());

                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (position == 0) {
                //用户apk的标签
                TextView tv_userLabel = new TextView(getActivity());
                tv_userLabel.setText("个人软件(" + unlockedUserDatas.size() + ")");
                tv_userLabel.setTextColor(Color.WHITE);
                tv_userLabel.setBackgroundColor(Color.GRAY);
                return tv_userLabel;
            } else if (position == unlockedUserDatas.size() + 1) {
                //系统apk的标签
                TextView tv_sysLabel = new TextView(getActivity());
                tv_sysLabel.setText("系统软件(" + unlockedSysDatas.size() + ")");
                tv_sysLabel.setTextColor(Color.WHITE);
                tv_sysLabel.setBackgroundColor(Color.GRAY);
                return tv_sysLabel;
            } else {
                if (convertView == null || !(convertView instanceof RelativeLayout)) {
                    convertView = View.inflate(getActivity(), R.layout.item_fragment_unlock_listview, null);
                    holder = new ViewHolder();

                    holder.drawable = (ImageView) convertView.findViewById(R.id.iv_fragment_unlocked_listview_item_drawable);
                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_fragment_unlocked_listview_item_name);
                    holder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_fragment_unlocked_listview_item_lock);

                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                final AppBean bean = getItem(position);

                if (bean == null) {
                    return convertView;
                }
                holder.drawable.setImageDrawable(bean.getIcon());
                holder.tv_name.setText(bean.getName());
                setImageViewEventAndBg(holder.iv_lock ,convertView ,bean.getPackageName());


                return convertView;
            }
        }



        private class ViewHolder {
            private ImageView drawable;
            private TextView tv_name;
            private ImageView iv_lock;
        }
    }

}
