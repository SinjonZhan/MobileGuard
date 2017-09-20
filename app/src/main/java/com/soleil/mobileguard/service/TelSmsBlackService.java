package com.soleil.mobileguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.soleil.mobileguard.dao.BlackDao;
import com.soleil.mobileguard.domain.BlackTable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * 监听电话短信拦截服务
 */
public class TelSmsBlackService extends Service {

    private SmsReceiver SmsReceiver;
    private BlackDao dao;

    private PhoneStateListener listener;
    private TelephonyManager tm;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        dao = new BlackDao(getApplicationContext());
        //短信广播注册
        {
            SmsReceiver = new SmsReceiver();
            IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            filter.addAction("android.provider.Telephony.SMS_DELIVER");
            filter.setPriority(1000);
            registerReceiver(SmsReceiver, filter);
        }


        //注册电话监听
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, final String incomingNumber) {

                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE://空闲

                        break;
                    case TelephonyManager.CALL_STATE_RINGING://响铃

                        int mode = dao.getMode(incomingNumber);

                        if ((mode & BlackTable.TEL) != 0) {
                            endcall();

                            //删除电话日志

                            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, new ContentObserver(new Handler()) {


                                @Override
                                public void onChange(boolean selfChange) {
                                    deleteCallLog(incomingNumber);
                                    getContentResolver().unregisterContentObserver(this);
                                    super.onChange(selfChange);
                                }


                            });


                        }

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK://通话

                        break;
                    default:
                        break;

                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    private void endcall() {
        //tm.endcall() 1.5版本已经阉割
        //实现方法 ServiceManager.getService(); 但改方法不能直接调用
        //反射调用
        try {
            Class<?> clazz = Class.forName("android.os.ServiceManager");

            Method method = clazz.getDeclaredMethod("getService", String.class);


            IBinder binder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);

            ITelephony iTelephony = ITelephony.Stub.asInterface(binder);

            iTelephony.endCall();//挂断电话

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    private void deleteCallLog(String incomingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //取消注册短信广播
        unregisterReceiver(SmsReceiver);
        //取消电话监听
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);

        super.onDestroy();
    }

    private class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] datas = (Object[]) intent.getExtras().get("pdus");
            for (Object data :
                    datas) {
                SmsMessage sm = SmsMessage.createFromPdu((byte[]) data);
                //获取发件人号码
                String address = sm.getDisplayOriginatingAddress();
                System.out.println("---------------" + address + "---------------");
                int mode = dao.getMode(address);
                System.out.println("---------------" + (mode & BlackTable.SMS) + "********");
                if ((mode & BlackTable.SMS) != 0) {
                    abortBroadcast(); //中止广播传递
                }

            }

        }
    }


}
