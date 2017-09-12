package com.soleil.mobileguard.utils;

public class EncryptTools {
    public static String encrypt(String str, int seed) {
        byte[] bytes = str.getBytes();
        for(int i = 0; i<bytes.length; i++) {
            bytes[i]^=seed;
        }
        return new String(bytes);
    }
    public static String decrypt(String str, int seed) {
        byte[] bytes = str.getBytes();
        for(int i = 0; i<bytes.length; i++) {
            bytes[i]^=seed;
        }
        return new String(bytes);
    }
}
