package com.yuqian.itax.util.util;

import com.github.pagehelper.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import java.util.Objects;
@Slf4j
public class SecretKeyUtil {

    public static final String KEY_ALGORITHM = "RSA";
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    public static final String SIGNATURE_ALGORITHM="MD5withRSA";
    /**
     * 分隔符
     */
    private static final String SPLIT = "&";
     /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * map对象中存放公私钥
     * @return
     * @throws Exception
     */
    public static Map<String, Object> initKey() throws Exception {
        //获得对象 KeyPairGenerator 参数 RSA 1024个字节
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(2048);
        //通过对象 KeyPairGenerator 获取对象KeyPair
        KeyPair keyPair = keyPairGen.generateKeyPair();
        
        //通过对象 KeyPair 获取RSA公私钥对象RSAPublicKey RSAPrivateKey
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //公私钥对象存入map中
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }
    
    /**
     * 获得公钥字符串
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKeyStr(Map<String, Object> keyMap) throws Exception {
        //获得map中的公钥对象 转为key对象
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        //编码返回字符串
        return Base64.encodeBase64String(key.getEncoded());
    }
 
 
    /**
     * 获得私钥字符串
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKeyStr(Map<String, Object> keyMap) throws Exception {
        //获得map中的私钥对象 转为key对象
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        //编码返回字符串
        return Base64.encodeBase64String(key.getEncoded());
    }
    
    /**
     * 获取公钥
     * @param key
     * @return
     * @throws Exception
     */
    protected static PublicKey getPublicKey(String key) throws Exception {  
        byte[] keyBytes;  
        keyBytes = Base64.decodeBase64(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        PublicKey publicKey = keyFactory.generatePublic(keySpec);  
        return publicKey;  
    }  
    
    /**
     * 获取私钥
     * @param key
     * @return
     * @throws Exception
     */
    protected static PrivateKey getPrivateKey(String key) throws Exception {  
        byte[] keyBytes;  
        keyBytes = Base64.decodeBase64(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);  
        return privateKey;  
    }
    
  	/**
  	 * 加密
  	 * @param plainText
  	 * @param publicKeyStr
  	 * @return
  	 * @throws Exception
  	 */
    protected static byte[] encrypt(byte[] plainText,String publicKeyStr)throws Exception{  
        PublicKey publicKey = getPublicKey(publicKeyStr);  
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);  
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
        int inputLen = plainText.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        int i = 0;
        byte[] cache;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(plainText, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(plainText, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptText = out.toByteArray();
        out.close();  
        return encryptText;  
    }  
    
    /**
     * 加密
     * @param data
     * @param publicKeyStr
     * @return
     * @throws Exception
     */
    public static String encryptToBase64(String data,String publicKeyStr)throws Exception{
    	return new String(Base64.encodeBase64(encrypt(data.getBytes(),publicKeyStr)));
    }
      
    /**
     * 解密
     * @param encryptText
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    protected static byte[] decrypt(byte[] encryptText,String privateKeyStr)throws Exception{  
        PrivateKey privateKey = getPrivateKey(privateKeyStr);  
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  
        int inputLen = encryptText.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptText, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptText, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] plainText = out.toByteArray();
        out.close();  
        return plainText;  
    }  
    
    /**
     * 解密
     * @param data
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static String decryptToBase64(String data,String privateKeyStr)throws Exception{
    	byte[] b = Base64.decodeBase64(data.getBytes());
    	return new String(decrypt(b, privateKeyStr));
    }
    
    /**
     * 对象参数签名
     *  获取对象属性拼接签名(排除掉有注解的字段)
     * @param obj
     * @param md5Key
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	public static String sign(Object obj,String md5Key)throws Exception{
        StringBuilder sb = new StringBuilder();
        try{
            Class clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String key = field.toString().substring(field.toString().lastIndexOf(".") + 1);
                //排除对象序列化的uid字段
                if("serialVersionUID".equals(key)){
                	continue ;
                }
                String value = String.valueOf(field.get(obj));
//                String value = (String) field.get(obj);
                if (value == null) {
                    value = "";
                }
                //字段没有@SignExclude注解的拼签名串
                //这部分是把需要参与签名的字段拼成一个待签名的字符串
                if (!field.isAnnotationPresent(SignExclude.class)) {
                    sb.append(SPLIT);
                    sb.append(value);
                }
            }
            sb.append(SPLIT).append(md5Key);
//            System.out.println("原签名串：" + sb.toString());
            String hmac = MD5.GetMD5Code(sb.toString());
//            System.out.println("签名串："+hmac);
            return hmac;
        }catch (Exception e){
           log.error(e.getMessage());
        }
        return null;
    }
    
    /**
     * 验证签名
     * @param obj        对象
     * @param md5Key     签名秘钥
     * @param hmac       签名字符串
     * @return
     * @throws Exception
     */
    public static boolean verify(Object obj,String md5Key,String hmac)throws Exception{
        String _hmac = sign(obj,md5Key);
        return StringUtil.isNotEmpty(_hmac) && Objects.equals(hmac, _hmac);
    }
 
 
    public static void main(String[] args) {
        Map<String, Object> keyMap;
//        byte[] cipherText;
        String temp = null;
        String input = "Hello World! 你好！！";
        try {
            keyMap = initKey();
            String publicKey = getPublicKeyStr(keyMap);
            System.out.println("公钥------------------");
            System.out.println(publicKey);
            String privateKey = getPrivateKeyStr(keyMap);
            System.out.println("私钥------------------");
            System.out.println(privateKey);
            
            System.out.println("测试可行性-------------------");
            System.out.println("明文======="+input);
            
            temp = encryptToBase64(input, publicKey);
            //加密后的东西 
            System.out.println("密文======="+temp);
            //开始解密 
            temp = decryptToBase64(temp, privateKey);
            System.out.println("解密后明文===== " + temp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
 
 

}
