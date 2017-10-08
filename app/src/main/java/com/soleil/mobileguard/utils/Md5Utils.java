package com.soleil.mobileguard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Md5Utils {

    /**
     * @param path
     *      文件路径
     *
     * @return
     *      文件的MD5值
     */
    public static String getFileMD5(String path) {
        StringBuilder mess = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(new File(path));
            byte[] buffer = new byte[10240];
            int len = fis.read(buffer);
            while (len != -1) {
                md.update(buffer, 0, len);
                len = fis.read(buffer);
            }
            byte[] digest = md.digest();

            for (byte b : digest) {
                int d = b & 0xff;
                String hexString = Integer.toHexString(d);
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }
                hexString = hexString.toUpperCase();
                mess.append(hexString);
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mess + "";
    }


    public static String Md5(String str) {
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance("Md5");
            byte[] bytes = str.getBytes();
            byte[] digest = md.digest(bytes);
            for (byte b : digest) {
                int d = b & 0xff;
                String hexString = Integer.toHexString(d);
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }
                hexString = hexString.toUpperCase();
                sb.append(hexString);

            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb + "";
    }
}
