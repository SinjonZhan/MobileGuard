package com.soleil.mobileguard.engine;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;

import com.soleil.mobileguard.utils.JsonSpecialSymbolUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * 短信的备份和还原
 */
public class SmsEngine {

    /**
     * 短信的备份
     *
     * @param context
     * @param pd      备份的数据接口回调
     */
    public static void smsBackupJson(final Activity context, final BackupProgress pd) {

        new Thread(new Runnable() {
            @Override
            public void run() {


                //1,通过内容提供者获取到短信
                Uri uri = Uri.parse("content://sms");
                //获取电话记录的联系人游标
                final Cursor cursor = context.getContentResolver().query(uri, new String[]{"address", "date", "body", "type"}, null, null, " _id desc");

                System.out.println("------------------------------" + Environment.getExternalStorageDirectory());
                //2,写到文件中
                File file = new File(Environment.getExternalStorageDirectory(), "mySms.json");

                try {
                    FileOutputStream fos = new FileOutputStream(file);

                    PrintWriter out = new PrintWriter(fos);
                    context.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.show();

                            pd.setMax(cursor.getCount());//设置进度条总进度


                        }
                    });

                    final Data data = new Data();


                    //写根标记
                    out.println("{\"count\":\"" + cursor.getCount() + "\"");
                    out.println(",\"smses\":[");

                    while (cursor.moveToNext()) {
                        data.progress++;
                        SystemClock.sleep(100);
                        //取短信
                        if (cursor.getPosition() == 0) {

                            out.println("{");
                        } else {
                            out.println(",{");

                        }


                        //address 封装
                        out.println("\"address\":\"" + cursor.getString(0) + "\",");
                        //date 封装
                        out.println("\"data\":\"" + cursor.getString(1) + "\",");
                        //body 封装
                        out.println("\"body\":\"" + JsonSpecialSymbolUtils.changeStr(cursor.getString(2)) + "\",");
                        //type 封装
                        out.println("\"type\":\"" + cursor.getString(3) + "\"");

                        out.println("}");
                        // 封装成xml标记

                        context.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                pd.setProgress(data.progress);
                            }
                        });

                    }

                    out.println("]}");
                    context.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            pd.end();


                            cursor.close();
                        }
                    });
                    //写根标记结束标记


                    out.flush();
                    out.close();//关闭流

                } catch (Exception e) {

                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * 短信的备份
     */
    public static void smsBackupXml(Activity context, final BackupProgress pd) {

        //1,通过内容提供者获取到短信
        Uri uri = Uri.parse("content://sms");
        //获取电话记录的联系人游标
        final Cursor cursor = context.getContentResolver().query(uri, new String[]{"address", "date", "body", "type"}, null, null, " _id desc");

        System.out.println("------------------------------" + Environment.getExternalStorageDirectory());
        //2,写到文件中
        File file = new File(Environment.getExternalStorageDirectory(), "mySms.xml");

        try {
            FileOutputStream fos = new FileOutputStream(file);

            PrintWriter out = new PrintWriter(fos);
            context.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    pd.show();

                    pd.setMax(cursor.getCount());//设置进度条总进度


                }
            });

            final Data data = new Data();

            //写根标记
            out.println("<smses count='" + cursor.getCount() + "'>");
            while (cursor.moveToNext()) {
                data.progress++;
                SystemClock.sleep(100);
                //取短信
                out.println("<sms>");

                //address 封装
                out.println("<address>" + cursor.getString(0) + "</address>");
                //date 封装
                out.println("<date>" + cursor.getString(1) + "</date>");
                //body 封装
                out.println("<body>" + cursor.getString(2) + "</body>");
                //type 封装
                out.println("<type>" + cursor.getString(3) + "</type>");

                out.println("</sms>");
                // 封装成xml标记

                context.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pd.setProgress(data.progress);
                    }
                });

            }

            context.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    pd.end();


                    cursor.close();
                }
            });
            //写根标记结束标记
            out.println("</smses>");

            out.flush();
            out.close();//关闭流

        } catch (Exception e) {

            e.printStackTrace();
        }


    }

    /**
     * 短信的还原Json
     *
     * @param context
     * @param pd      接口回调
     */
    public static void smsResumeJson(final Activity context, final BackupProgress pd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //1,通过内容提供者保存到短信
                Uri uri = Uri.parse("content://sms");
                try {
                    FileInputStream fis = new FileInputStream(new File(Environment.getExternalStorageDirectory(), "mySms.json"));
                    StringBuilder JsonSmsStr = new StringBuilder();
                    //IO流的封装
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        JsonSmsStr.append(line);
                    }
//                    System.out.println("------------------------------");
//                    System.out.println(JsonSmsStr);
//                    System.out.println("------------------------------");

                    //解析Json数据
                    JSONObject jsonObj = new JSONObject(JsonSmsStr.toString());
                    System.out.println("------------------------------");
                    System.out.println(Integer.parseInt(jsonObj.getString("count")));
                    System.out.println("------------------------------");
                    final int counts = Integer.parseInt(jsonObj.getString("count"));
                    final Data dataOfProcess = new Data();
                    context.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.show();

                            pd.setMax(counts);//设置进度条总进度


                        }
                    });

                    //循环读取短信
                    JSONArray jsonArray = (JSONArray) jsonObj.get("smses");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dataOfProcess.progress = i;
                        SystemClock.sleep(100);
                        context.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                pd.setProgress(dataOfProcess.progress);



                            }
                        });
                        JSONObject smsJson = (JSONObject) jsonArray.get(i);
                        String address = smsJson.getString("address");
                        String body = smsJson.getString("body");
                        String data = smsJson.getString("data");
                        String type = smsJson.getString("type");

                        ContentValues values = new ContentValues();
                        values.put("address", address);
                        values.put("body", body);
                        values.put("data", data);
                        values.put("type", type);

                        context.getContentResolver().insert(uri, values);

                    }


                    reader.close();
                    context.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            pd.end();


                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public interface BackupProgress {
        void setMax(int max);

        void setProgress(int progress);

        void end();

        void show();
    }

    private static class Data {
        int progress = 0;
    }
}

