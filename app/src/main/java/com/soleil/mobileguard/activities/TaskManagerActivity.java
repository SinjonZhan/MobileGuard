package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.domain.TaskBean;
import com.soleil.mobileguard.engine.TaskManagerEngine;

import java.util.ArrayList;
import java.util.List;


/**
 * 进程管理的Activity
 */
public class TaskManagerActivity extends Activity {
    private static final int LOADING = 1;
    private static final int FINISH = 2;
    List<TaskBean> userTasks = new ArrayList<>();
    List<TaskBean> sysTasks = new ArrayList<>();
    private TextView tv_runningTask;
    private TextView tv_mem;
    private ListView lv_tasks;
    private ProgressBar pb_loading;
    private TextView tv_label;
    private List<TaskBean> allRunningTaskInfos;
    private TaskAdapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_loading.setVisibility(View.VISIBLE);
                    tv_label.setVisibility(View.GONE);
                    lv_tasks.setVisibility(View.GONE);


                    break;
                case FINISH:
                    lv_tasks.setVisibility(View.VISIBLE);
                    tv_label.setVisibility(View.VISIBLE);
                    pb_loading.setVisibility(View.GONE);

                    tv_label.setText("用户进程" + "(" + userTasks.size() + ")");

                    tv_runningTask.setText("运行中的进程:" + (allRunningTaskInfos.size()));
                    tv_mem.setText("可用/总内存:" + (Formatter.formatFileSize(getApplicationContext(), TaskManagerEngine.getAvailableMemSize(getApplicationContext()))
                            + "/" +
                            (Formatter.formatFileSize(getApplicationContext(), TaskManagerEngine.getTotalMemSize(getApplicationContext())))));
                    adapter.notifyDataSetChanged();


                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        lv_tasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        lv_tasks.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //判断显示的位置
                // 如果为系统进程
                //改变标签显示的内容

                if (firstVisibleItem >= userTasks.size() + 1) {
                    tv_label.setText("系统进程" + "(" + sysTasks.size() + ")");
                } else {
                    tv_label.setText("用户进程" + "(" + userTasks.size() + ")");

                }
            }

        });
    }

    private void initData() {
        allRunningTaskInfos = TaskManagerEngine.getAllRunningTaskInfos(getApplicationContext());
        adapter = new TaskAdapter();
        lv_tasks.setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {

                handler.obtainMessage(LOADING).sendToTarget();
                SystemClock.sleep(1000);
                sysTasks.clear();
                userTasks.clear();

                for (TaskBean bean : allRunningTaskInfos) {
                    if (bean.isSystem()) {
                        sysTasks.add(bean);
                    } else {
                        userTasks.add(bean);
                    }
                }
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }).start();

    }

    private void initView() {
        setContentView(R.layout.activity_task_manager);

        tv_runningTask = (TextView) findViewById(R.id.tv_runnningtask);//运行的进程数文本

        tv_mem = (TextView) findViewById(R.id.tv_avail_total_mem);//可用/总内存大小

        lv_tasks = (ListView) findViewById(R.id.lv_taskmanager_datas);//进程列表

        pb_loading = (ProgressBar) findViewById(R.id.pb_taskmanager_loading);//列表加载进度条

        tv_label = (TextView) findViewById(R.id.tv_taskmanager_listview_lable);//；ListView中的标签


    }

    /**
     * 清理进程
     *
     * @param view
     */
    public void clearTask(View view) {

    }

    /**
     * 全选进程
     *
     * @param view
     */
    public void selectAll(View view) {

        for (TaskBean bean : allRunningTaskInfos


                ) {
            if (bean.getPackName().equals(getPackageName())) {
                bean.setChecked(false);

            }
            bean.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 反选进程
     *
     * @param view
     */
    public void selectInvert(View view) {
        for (TaskBean bean : allRunningTaskInfos


                ) {
            if (bean.getPackName().equals(getPackageName())) {
                bean.setChecked(false);

            }
            bean.setChecked(!bean.isChecked());
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置
     *
     * @param view
     */
    public void setting(View view) {

    }


    private class TaskAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userTasks.size() + 1 + sysTasks.size() + 1;
        }

        @Override
        public TaskBean getItem(int position) {
            TaskBean bean = null;
            if (position <= userTasks.size()) {
                bean = userTasks.get(position - 1);
            } else {
                bean = sysTasks.get(position - 1 - 1 - userTasks.size());

            }
            return bean;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;

            if (position == 0) {
                //用户apk的标签
                TextView tv_userLabel = new TextView(getApplicationContext());
                tv_userLabel.setText("用户进程(" + userTasks.size() + ")");
                tv_userLabel.setTextColor(Color.WHITE);
                tv_userLabel.setBackgroundColor(Color.GRAY);
                return tv_userLabel;
            } else if (position == userTasks.size() + 1) {
                //系统apk的标签
                TextView tv_sysLabel = new TextView(getApplicationContext());
                tv_sysLabel.setText("系统进程(" + sysTasks.size() + ")");
                tv_sysLabel.setTextColor(Color.WHITE);
                tv_sysLabel.setBackgroundColor(Color.GRAY);
                return tv_sysLabel;
            } else {
                if (convertView == null || !(convertView instanceof RelativeLayout)) {
                    convertView = View.inflate(getApplicationContext(), R.layout.item_task_manager_listview, null);
                    holder = new ViewHolder();

                    holder.drawable = (ImageView) convertView.findViewById(R.id.iv_drawable);
                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.cb_checked = (CheckBox) convertView.findViewById(R.id.cb_checked);
                    holder.tv_mem = (TextView) convertView.findViewById(R.id.tv_mem);
                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                //不能是成员变量
                // 因为ListView中position始终指向界面显示的最后一个数据
                final TaskBean bean = getItem(position);


                holder.drawable.setImageDrawable(bean.getIcon());
                holder.tv_name.setText(bean.getName());

                holder.tv_mem.setText(Formatter.formatFileSize(getApplicationContext(), bean.getMemSize()));
                //记录复选框的状态

                holder.cb_checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        bean.setChecked(isChecked);


                    }
                });
                holder.cb_checked.setChecked(bean.isChecked());

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //设置复选框的反选
                        if (bean.getPackName().equals(getPackageName())) {
                            holder.cb_checked.setChecked(false);
                        } else {
                            holder.cb_checked.setChecked(!holder.cb_checked.isChecked());

                        }

                    }
                });


                //判断是否是本身apk 是就隐藏checkbox
                if (bean.getPackName().equals(getPackageName())) {
                    holder.cb_checked.setVisibility(View.GONE);
                }
                return convertView;
            }
        }
    }

    private class ViewHolder {
        private ImageView drawable;
        private TextView tv_name;
        private TextView tv_mem;
        private CheckBox cb_checked;
    }
}
