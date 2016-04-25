package com.xiao.mobiesafe.utils;

public class EncryptTools {
    public static String encrypt(String str) {
        byte[] bytes = str.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] += 1;
        }
        return new String(bytes);
    }

    public static String decryption(String str) {
        byte[] bytes = str.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] -= 1;
        }
        return new String(bytes);
    }
}
