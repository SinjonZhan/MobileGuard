package com.soleil.mobileguard.activities;

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.dao.LockDao;
import com.soleil.mobileguard.domain.LockedTable;
import com.soleil.mobileguard.fragment.LockFragment;
import com.soleil.mobileguard.fragment.UnlockFragment;

import java.util.List;


/**
 * 程序锁的界面Fragment
 */
public class LockActivity extends FragmentActivity {

    private TextView tv_lock;
    private TextView tv_unlock;
    private FrameLayout fl_content;
    private UnlockFragment unlockFragment;
    private LockFragment lockFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {

        //注册内容观察者

        ContentObserver observer = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //数据dao层存取
                        LockDao dao = new LockDao(getApplicationContext());
                        List<String> allLockedDatas = dao.getAllLockedDatas();

                        lockFragment.setAllLockedPacks(allLockedDatas);
                        unlockFragment.setAllLockedPacks(allLockedDatas);
                    }
                }).start();
            }
        };
        getContentResolver().registerContentObserver(LockedTable.uri,true,  observer);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //开启事务
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //替换布局

                if (v.getId() == R.id.tv_unlock) {
                    transaction.replace(R.id.fl_lockedactivity_content, unlockFragment);
                    tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                    tv_lock.setBackgroundResource(R.drawable.tab_right_default);


                }else {
                    transaction.replace(R.id.fl_lockedactivity_content, lockFragment);
                    tv_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                    tv_unlock.setBackgroundResource(R.drawable.tab_left_default);


                }
                //提交事务
                transaction.commit();
//                switch (v.getId()) {
//                    case R.id.tv_unlock: {
//                        //开启事务
//                        FragmentTransaction transaction = fragmentManager.beginTransaction();
//                        //替换布局
//                        transaction.replace(R.id.fl_lockedactivity_content, unlockFragment);
//                        //提交事务
//                        transaction.commit();
//                    }
//                    break;
//                    case R.id.tv_lock: {
//                        //开启事务
//                        FragmentTransaction transaction = fragmentManager.beginTransaction();
//                        //替换布局
//                        transaction.replace(R.id.fl_lockedactivity_content, lockFragment);
//                        //提交事务
//                        transaction.commit();
//                    }
//                    break;
//                    default:
//                        break;
//                }

            }
        };

        tv_lock.setOnClickListener(listener);
        tv_unlock.setOnClickListener(listener);
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //数据dao层存取
                LockDao dao = new LockDao(getApplicationContext());
                List<String> allLockedDatas = dao.getAllLockedDatas();

                lockFragment.setAllLockedPacks(allLockedDatas);
                unlockFragment.setAllLockedPacks(allLockedDatas);
            }
        }).start();
        //fragmemnt的管理器
        fragmentManager = getSupportFragmentManager();

        //fragment替换framelayout

        //开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //替换布局
        transaction.replace(R.id.fl_lockedactivity_content, unlockFragment);
        tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
        //提交事务
        transaction.commit();
    }

    private void initView() {
        setContentView(R.layout.activity_lock);
        //锁
        tv_lock = (TextView) findViewById(R.id.tv_lock);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        //要替换成fragment的组件
        fl_content = (FrameLayout) findViewById(R.id.fl_lockedactivity_content);

        //未加锁的fragment
        unlockFragment = new UnlockFragment();
        //加锁的gragment
        lockFragment = new LockFragment();


    }


}
