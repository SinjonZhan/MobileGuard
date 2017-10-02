package com.soleil.mobileguard.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.soleil.mobileguard.R;
import com.soleil.mobileguard.engine.SmsEngine;


/**
 * 高级工具：   电话归属地查询 短信备份和还原 程序锁的设置
 */
public class AToolActivity extends Activity {


    private ProgressBar pb_progress_backup;
    private ProgressDialog pd;
    private ProgressBar pb_progress_resume;

    @Override
    protected void onCreate( Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initView();


    }


    private void initView() {
        setContentView(R.layout.activity_atool);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pb_progress_backup = (ProgressBar) findViewById(R.id.pb_smsbackup_progress);
        pb_progress_resume = (ProgressBar) findViewById(R.id.pb_smsresume_progress);

    }

    /**
     * 手机归属地查询
     *
     * @param view
     */
    public void phoneQuery(View view) {
        Intent query = new Intent(this, PhoneLocationActivity.class);
        startActivity(query);
    }

    /**
     * 短信的备份
     *
     * @param view
     */
    public void smsBackup(View view) {


        SmsEngine.smsBackupJson(AToolActivity.this, new SmsEngine.BackupProgress() {
            @Override
            public void setMax(int max) {
                pd.setMax(max);
                pb_progress_backup.setMax(max);

            }

            @Override
            public void setProgress(int progress) {
                pb_progress_backup.setProgress(progress);
                pd.setProgress(progress);

            }

            @Override
            public void end() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        pb_progress_backup.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void show() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.show();
                        pb_progress_backup.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

    }

    public void smsResume(View view) {
        SmsEngine.smsResumeJson(AToolActivity.this, new SmsEngine.BackupProgress() {
            @Override
            public void setMax(int max) {
                pd.setMax(max);
                pb_progress_resume.setMax(max);

            }

            @Override
            public void setProgress(int progress) {
                pb_progress_resume.setProgress(progress);
                pd.setProgress(progress);

            }

            @Override
            public void end() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        pb_progress_resume.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void show() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.show();
                        pb_progress_resume.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    /**
     * 跳转到程序锁界面
     * @param view
     */
    public void lockActivity(View view) {
        Intent intent = new Intent(AToolActivity.this, LockActivity.class);
        startActivity(intent);
    }
}
