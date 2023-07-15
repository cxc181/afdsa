package com.yuqian.itax.util.util;


import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Slf4j
public class Md5Util {
    private static final String DEFAULT_ENCODING = "GB2312";

    private static MessageDigest mdigest = null;
    private static char digits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public Md5Util() {
        super();
    }


    public static String MD5(String input) {
        return MD5(input, DEFAULT_ENCODING);
    }

    public static String MD5(String text, String enocding) {
        if (StringUtils.isNotBlank(text)) {
            if (StringUtils.isBlank(enocding)) {
                enocding = DEFAULT_ENCODING;
            }

            try {
                byte[] output = MessageDigest.getInstance("MD5").digest(text.getBytes(enocding));
                StringBuilder out = new StringBuilder();

                for(int i = 0; i < output.length; ++i) {
                    String t = Integer.toHexString(output[i] & 255);
                    out.append(t.length() == 1 ? "0" + t : t);
                }

                return out.toString().toUpperCase();
            } catch (NoSuchAlgorithmException var6) {
                log.error(var6.getMessage());
            } catch (UnsupportedEncodingException var7) {
                log.error(var7.getMessage());
            }
        }

        return "";
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

    private static String encodeHex(byte[] bytes) {
        StringBuilder buffer = new StringBuilder(bytes.length * 2);

        for(int i = 0; i < bytes.length; ++i) {
            if ((bytes[i] & 255) < 16) {
                buffer.append("0");
            }

            buffer.append(Long.toString((long)(bytes[i] & 255), 16));
        }

        return buffer.toString();
    }

    /*public static void main(String[] args) {
        System.out.println(MD5("bqgcfz"));
    }*/


    /**
     * 生成有效签名
     *
     * @param orgin
     * @return
     */
    public static String signature(String orgin) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            result = byte2hex(md.digest(orgin.toString().getBytes("utf-8")));
        } catch (Exception e) {
            throw new RuntimeException("sign签名错误！");
        }
        return result;
    }

    /**
     * 二行制转字符
     *
     * @param b
     * @return
     */
    private static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1){
                hs.append("0").append(stmp);
            }else{
                hs.append(stmp);
            }
        }
        return hs.toString().toLowerCase();
    }

    /**
     * 生成 sign 参数
     *
     * @param map
     * @param secretKey
     *            密钥
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String createSign(Map map, String secretKey) {
        Map<String, String> param = map;

        StringBuffer signStr = new StringBuffer();
        int size = param.entrySet().size();
        int i = 1;
        for (Map.Entry<String, String> entry : param.entrySet()) {
            if (i == size) {
                signStr.append(entry.getKey()).append("=").append(entry.getValue());
            } else {
                signStr.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            i++;
        }
        signStr.append("&").append("key=").append(secretKey);
        return signature(signStr.toString()).toUpperCase();
    }

    private static MessageDigest getMdInst() {
        if (null == mdigest) {
            try {
                mdigest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
               log.error(e.getMessage());
            }
        }
        return mdigest;
    }

    /**
     * @Description 快递实时查询MD5加密方法
     * @Author  Kaven
     * @Date   2020/2/12 10:57
     * @Param  s
     * @Return String
     * @Exception
    */
    public static String encode(String s) {
        if(null == s) {
            return "";
        }

        try {
            byte[] bytes = s.getBytes();
            MessageDigest messageDigest = getMdInst();
            if(null != messageDigest) {
                messageDigest.update(bytes);
                byte[] md = messageDigest.digest();
                int j = md.length;
                char str[] = new char[j * 2];
                int k = 0;
                for (int i = 0; i < j; i++) {
                    byte byte0 = md[i];
                    str[k++] = digits[byte0 >>> 4 & 0xf];
                    str[k++] = digits[byte0 & 0xf];
                }
                return new String(str);
            }
            return null;
        } catch (Exception e) {
           log.error(e.getMessage());
            return null;
        }
    }

    public static String md5ByGuava(final String sourceString) {
        if (StringUtil.isNotBlank(sourceString)) {
            return Hashing.md5().hashString(sourceString, Charset.forName(Charsets.UTF_8.toString())).toString();
        }
        return null;
    }
}

