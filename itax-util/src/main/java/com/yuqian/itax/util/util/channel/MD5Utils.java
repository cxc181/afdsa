package com.yuqian.itax.util.util.channel;

import com.duob.encrypt.utils.MD5Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MD5Utils {

    public MD5Utils() {
    }

    public static String encrypt(String source) {
        return encodeMd5(source.getBytes());
    }

    private static String encodeMd5(byte[] source) {
        try {
            return encodeHex(MessageDigest.getInstance("MD5").digest(source));
        } catch (NoSuchAlgorithmException var2) {
            throw new IllegalStateException(var2.getMessage(), var2);
        }
    }
    public static final String md5sign(Map<String, Object> params, String signKey) {
        List<String> keys = Arrays.asList(params.keySet().toArray(new String[params.size()]));
        Collections.sort(keys);
        keys = new ArrayList<String>(keys);
        StringBuilder sb = new StringBuilder();
        Iterator var4 = keys.iterator();
        while (var4.hasNext()) {
            String key = (String) var4.next();
            Object value = params.get(key);
            Object strValue = "";
            if (value != null && !"sign".equals(key)) {
                strValue =  value;
                sb.append(key).append("=").append(strValue).append("&");
            }
        }
        sb.append(signKey);
        return MD5Util.encrypt(sb.toString());
    }

    private static String encodeHex(byte[] bytes) {
        StringBuffer buffer = new StringBuffer(bytes.length * 2);

        for(int i = 0; i < bytes.length; ++i) {
            if ((bytes[i] & 255) < 16) {
                buffer.append("0");
            }

            buffer.append(Long.toString((long)(bytes[i] & 255), 16));
        }

        return buffer.toString();
    }

    public static final String getMessageDigest(byte[] buffer) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;

            for(int i = 0; i < j; ++i) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }

            return new String(str);
        } catch (Exception var9) {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(encrypt("123456"));
    }
}
