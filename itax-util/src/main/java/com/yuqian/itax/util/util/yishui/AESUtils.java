package com.yuqian.itax.util.util.yishui;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {
    private static final String padding = "AES/ECB/NoPadding";
    private static final String algorithm = "AES";

    //入口函数
    public static String decrypt(String data, String key){
        if(StrUtil.isBlank(data) || "[]".equals(data) || "null".equals(data.toLowerCase())){
            return "";
        }
        byte[] result = decrypt(Base64.decode(data.getBytes()),key.getBytes());
        //去掉填充
        if(result.length > 0){
            byte lastByte = result[result.length - 1];
            int dataLength;
            if((int)lastByte > 0){
                dataLength = result.length - (int)lastByte;
            }else{
                dataLength = result.length;
            }
            if(dataLength > 0){
                byte[] tmp = new byte[dataLength];
                System.arraycopy(result, 0 ,tmp, 0 , dataLength);
                return new String(Base64.decode(tmp));
            }
        }
        return "";
    }

    private static byte[] decrypt(byte[] data, byte[] key) {
        byte[] result = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
            Cipher cipher = Cipher.getInstance(padding);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            data = zeroPadding(data, 16);
            result = cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static byte[] zeroPadding(byte[] data, int blockSize) {
        int paddingLength = data.length;
        if (data.length % blockSize != 0) {
            paddingLength = paddingLength + (blockSize - paddingLength % blockSize);
        }
        byte[] paddingData = new byte[paddingLength];
        System.arraycopy(data, 0, paddingData, 0, data.length);
        return paddingData;
    }

}
