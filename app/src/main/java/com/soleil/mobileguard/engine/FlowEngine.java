package com.soleil.mobileguard.engine;

import android.content.Context;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 流量统计的业务类
 */
public class FlowEngine {

    /**
     * @return
     * 返回接收的流量信息
     */
    public static String getReceiver(Context context, int uid){
        String res = null;
        //读取流量信息文件/proc/uid_stat/uid/tcp_rcv
        String path = "/proc/uid_stat/" + uid + "/tcp_rcv";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
            String line = reader.readLine();
            long size = Long.parseLong(line);
            res = Formatter.formatFileSize(context, size);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
    /**
     * @return
     * 返回发送的流量信息
     */
    public static String getSend(Context context, int uid){
        String res = null;
        //读取流量信息文件/proc/uid_stat/uid/tcp_snd
        String path = "/proc/uid_stat/" + uid + "/tcp_snd";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
            String line = reader.readLine();
            long size = Long.parseLong(line);
            res = Formatter.formatFileSize(context, size);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
