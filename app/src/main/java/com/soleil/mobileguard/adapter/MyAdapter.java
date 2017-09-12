package com.soleil.mobileguard.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.utils.MyConstants;
import com.soleil.mobileguard.utils.SpTools;


public class MyAdapter extends BaseAdapter {

    private Context context;
    private int icons[] = {R.drawable.safe, R.drawable.callmsgsafe, R.drawable.item_gv_selector_app
            , R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan
            , R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings};
    private String names[] = {"手机防盗", "通讯卫士", "软件管家", "进程管理", "流量统计", "病毒查杀", "缓存清理", "高级工具", "设置中心"};

    public MyAdapter() {

    }

    public MyAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return icons.length;
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
        View view = View.inflate(context, R.layout.item_home_gv, null);
        ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_item_home_gv_icon);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_item_home_gv_name);


        iv_icon.setImageResource(icons[position]);
        tv_name.setText(names[position]);
        if(position==0) {
            if (!TextUtils.isEmpty(SpTools.getString(context, MyConstants.TITLE, "手机防盗"))) {
                tv_name.setText(SpTools.getString(context, MyConstants.TITLE, "手机防盗"));
            }
        }
        return view;
    }
}
