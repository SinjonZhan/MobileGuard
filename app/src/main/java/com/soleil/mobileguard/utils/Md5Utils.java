package com.soleil.mobileguard.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Md5Utils {
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
                sb.append(hexString);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb + "";
    }
}
