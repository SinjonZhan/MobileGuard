package com.example;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyTest {
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

    public static void main(String[] args) {
//
//        String a = new String("abc");
//        a = "dfg";
//        System.out.println(a);
        Pattern pattern = Pattern.compile("1[3578]{1}[0-9]{9}");
        Matcher matcher = pattern.matcher("13912345567");
        boolean matches = matcher.matches();
        System.out.println("------------------------------"+matches);
    }
}
