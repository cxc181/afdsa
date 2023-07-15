package com.yuqian.itax.util.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
public class AESEncryptUtil {
    //使用AES-128-CBC加密模式，key需要为16位,key和iv可以相同！
    private static String KEY = "1234567890123456";
    private static String IV = "1234567890123456";

    /**
     * 加密方法
     * @param data  要加密的数据
     * @param key 加密key
     * @param iv 加密iv
     * @return 加密的结果
     * @throws Exception
     */
    public static String encrypt(String data, String key, String iv){
        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");//"算法/模式/补码方式"NoPadding PkcsPadding
            int blockSize = cipher.getBlockSize();

            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);

            return new Base64().encodeToString(encrypted);

        } catch (Exception e) {
            log.error("加密参数失败：{}",e.getMessage());
            return "";
        }
    }

    /**
     * 解密方法
     * @param data 要解密的数据
     * @param key  解密key
     * @param iv 解密iv
     * @return 解密的结果
     * @throws Exception
     */
    public static String desEncrypt(String data, String key, String iv){
        try {
            byte[] encrypted1 = new Base64().decode(data);

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString;
        } catch (Exception e) {
            log.error("解密参数失败：{}",e.getMessage());
            return "";
        }
    }

    /**
     * 使用默认的key和iv加密
     * @param data
     * @return
     * @throws Exception
     */
    public static String encrypt(String data) throws Exception {
        return encrypt(data, KEY, IV);
    }

    /**
     * 使用默认的key和iv解密
     * @param data
     * @return
     * @throws Exception
     */
    public static String desEncrypt(String data) throws Exception {
        return desEncrypt(data, KEY, IV);
    }

    /**
     * 测试
     */
    public static void main(String args[]) throws Exception {

        String test = "{\n" +
                "  \"parkId\": \"4\"\n" +
                "}";
//        String test =new String(test1.getBytes(),"UTF-8");
        String data = null;
        String key =  KEY;
        String iv = IV;
        // /g2wzfqvMOeazgtsUVbq1kmJawROa6mcRAzwG1/GeJ4=
        key = StringUtils.rightPad("NBGT",16,"NBGT");
        iv  = StringUtils.rightPad("NBJRF1",16,"0");;
        data = encrypt(test, key, iv);
        System.out.println("数据："+test);
        System.out.println("加密："+data);
       // data = "24hKUrdw7fO3HW0eyF3hIMJe8IN86Zh8izfp5KyMokCrZ00Mj/bCRZfla9MtcD4qa6IGGSNjMyaPdgkNqI6HoejjZNn7Ovfeoznp74r9POsYB6AUEva7zyc7LSj+DwYE4AP1U1TosyVV481katfaY6d50eRcMPAUTcmwCaaN9CEth8D/krY4otfWMJ5fle6MD7a9e/ioTprowhgyjdXCDRU7s4o+V0K1eBFjQT8LqaxmA8zc8rJ5tHv0QpctGWTZBi+/paTvA/90YYk5wqHDyn4/Z31ussTH3kNRa/YRuCaO70SIIgOJZqUnbfqY+eBtTJ8JILR50+4uiECh3luzhUMk1+B3Azno8pEScQLNGKVpnW14vTL0Ce8rqJnY2mH1TXFw6pE1VwyfI9gdYTY+0QMV3gDxmUNNa9b2RtU0WWQt19nwOYiBu9kavf1dc0kj2k36nCDV7pmaeVGLiD/lztua2rrE0dNf8WtERHRAaIxz/YRG4PoFhzCD4sDOkLfTxlRcDjI84bO7PvPAthayeuVty1ReAj/z02OBeHdouFmHrbFXNJMCxWe8ERZNUXQgl2O77BJmwflxeqtqG4tVRwuOejnM0p9t46kLn3h0RxN/MVVga5LQW9XbbtM2arfmRf8DzQUiwg8yHkTX+tkZf3SMFAvLoZG/1xHVVKGJlZj93VY9idWDNbg7pEpbbGEB+WBp2F/1uVaUGgzVkTJOk4kB/7EMKxgCMid+uKi7ffxgncnS3anVrHPMNsuHY9eJYhrPCVdPzPbdLkeJcHDKF5xCC1cLQEdSvdSCzvlrjD4125skLdahkcPN3zHqFil5zhsgLazI99W8TYMRfiUjYnrRQAeE9/CTttUPeoqjBzcC8VyTNcv9Peg3057/nXfk5re8S0eRddjvjmEszqExeI/pAlKLR7eXvLeovv+NO6UhlxHY7YdrdXsEHytrwZFRoXf4bU8BzUhuyLbkG+BPJerUZhWSZTlsBgQ1HL0b5a8hIIJSoGQejxmRPSCVbyirXWJZAZ0pABz+JzxorrlhX3ToXsRnbv3FFfMSHpz5puay/r1RLCbJwd3tKFPqzRyzdGwXZfK9pTdqrcztBrulhHkBM/Gd3El9HaNYvCsWmXx5PVFl6rj+KOMnOm9xI/jksy2RHAQhp/l7TsxrKJ5qR88QoTS/PFFfR/pp/SUT8fvcCiEoXGK3EpNlr6xFO9vYbEU6sgkpwbTvMUXSsHS0zI5x9FcfS4Kx7PnwD1wuLdoqLMkvnJHYypShW47AY6eCUVi/vmmrM+JZp9Nggy0hUPMzUNJenR7ppAIvxsGBmNRcVz8XOMVcSXFwscp3KiY0CD4J4jLjAPkuc2OD9Irk4nV1i/XSMai2BoUJLltVspLaJ238jlhdCPOKoY46IR/8PwOWMlvR/w3WkkSbIaYKoyeGs4gpY9L3k16W+oH14TWsO5n04ZKC+jlpytXxCNPU5MXmZyIlmZXuP64hXyP1MHEAyOVxbp6+Y30q0DT/oEU+YGvefxcSKw3COMLk7xlZ4sMaLu258tmt5LhKtZ0XraE2c4Cd+P85/UUYOLXGsQpuw2Ro/FJA2S7jYfTh9FnbDP3Wow3B/4itpUe080vCh9ENpvUUIe8QxAz11RmE7SWfv6qXojDhhwWlliBVpxpOej5ojM7rgJ1aexBxTtnwNIzZnBN08DKCWeBpN9kfcjbwBNRDQoN0Popbiu3pKtig";

                String jiemi =desEncrypt(data, key, iv).trim();
        System.out.println("解密："+jiemi);

//        System.out.println(StringUtils.rightPad("YSC123456",13,"aaa"));
    }
}
