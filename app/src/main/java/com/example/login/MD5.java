package com.example.login;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * MD5加密
 */
public class MD5 {
    public String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
