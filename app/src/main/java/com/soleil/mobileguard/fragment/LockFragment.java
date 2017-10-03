package com.soleil.mobileguard.fragment;

import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.soleil.mobileguard.R;

public class LockFragment extends BaseUnlockOrLockFragment {
    @Override
    public boolean isMyDatas(String packName) {
        return allLockedPacks.contains(packName);


    }

    @Override
    protected void setLockedNumberTextView() {
        tv_unlocked_lab.setText("已加锁软件" + "(" + (unlockedUserDatas.size() + unlockedSysDatas.size()) + ")");
    }

    /**
     * @param iv_lock     加锁的按钮
     * @param convertView listview的item
     * @param packName    加锁包名
     */
    @Override
    protected void setImageViewEventAndBg(ImageView iv_lock, final View convertView, final String packName) {
        //初始化图片选择器
        iv_lock.setImageResource(R.drawable.iv_unlock_selector);

        iv_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        Log.i("Lock", "lock");
                //添加数据
                dao.remove(packName);

                //移除数据的动画
                TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF,-1f, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF,0);
                ta.setDuration(300);
                convertView.startAnimation(ta);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(400);
                        initData();

                    }
                }).start();
                //更新数据
            }

        });
    }
}
