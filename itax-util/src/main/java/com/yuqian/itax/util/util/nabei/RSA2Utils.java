package com.yuqian.itax.util.util.nabei;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title RSA2Utils.java
 * @Description RSA2 Java实现
 * @author kevin.wlb
 * @createTime 2020年4月22日
 */
@Slf4j
public class RSA2Utils implements Serializable {
	private static final long serialVersionUID = -1781016032916945647L;
	// 获取公钥的key
	private static final String PUBLIC_KEY = "publicKey";
	// 获取私钥的key
	private static final String PRIVATE_KEY = "privateKey";
	//加密算法RSA
	private static final String KEY_ALGORITHM = "RSA";
	 //签名算法
	private static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	 //RSA最大加密明文大小
	private static final int MAX_ENCRYPT_BLOCK = 117;
	//RSA最大解密密文大小
	private static final int MAX_DECRYPT_BLOCK = 256;

	/**
	 * @return 密钥对:Map<String,String>{ publicKey="", privateKey="" }
	 * @throws Exception
	 */
	protected static Map<String, String> genKeyPair() {
		Map<String, String> keyMap = new HashMap<String, String>(2);
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
			keyPairGen.initialize(2048);
			KeyPair keyPair = keyPairGen.generateKeyPair();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			keyMap.put(PUBLIC_KEY, Base64.encodeBase64String(publicKey.getEncoded()));
			keyMap.put(PRIVATE_KEY, Base64.encodeBase64String(privateKey.getEncoded()));
		} catch (Exception e) {
			System.out.println("RSA2Utils.genKeyPair ERROR::");
		}
		return keyMap;
	}

	/**
	 * 对已加密数据进行签名
	 *
	 * @param data       已加密的数据
	 * @param privateKey 私钥
	 * @return 对已加密数据生成的签名
	 * @throws Exception
	 */
	@Deprecated
	public static String sign(String data, String privateKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(privateK);
		signature.update(data.getBytes());
		return Base64.encodeBase64String(signature.sign());
	}

	/**
	 * 验签
	 *
	 * @param data      签名之前的数据
	 * @param publicKey 公钥
	 * @param sign      签名之后的数据
	 * @return 验签是否成功
	 * @throws Exception
	 */
	@Deprecated
	public static boolean verify(String data, String publicKey, String sign) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicK = keyFactory.generatePublic(keySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(publicK);
		signature.update(data.getBytes());
		return signature.verify(Base64.decodeBase64(sign));
	}

	/**
	 * 用私钥对数据进行解密
	 *
	 * @param data 
	 * @param privateKey    私钥
	 * @return 解密后的数据
	 * @throws Exception
	 */
	public static String decrypt(String data, String privateKey) throws Exception {
		ByteArrayOutputStream out = null;
		try {
			byte[] keyBytes = Base64.decodeBase64(privateKey);
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateK);
			byte[] encryptedData = Base64.decodeBase64(data);
			int inputLen = encryptedData.length;
			out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
					cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_DECRYPT_BLOCK;
			}
			byte[] decryptedData = out.toByteArray();
			return new String(decryptedData);
		} catch (Exception e) {
			throw e;
		}finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 公钥加密
	 * @param data      需要加密的数据
	 * @param publicKey 公钥
	 * @return 使用公钥加密后的数据
	 * @throws Exception
	 */
	public static String encrypt(String data, String publicKey) throws Exception {
		ByteArrayOutputStream out = null;
		try {
			byte[] keyBytes = Base64.decodeBase64(publicKey);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key publicK = keyFactory.generatePublic(x509KeySpec);
			// 对数据加密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicK);
			byte[] byteData = data.getBytes();
			int inputLen = byteData.length;
			int offSet = 0;
			byte[] cache;
			int i = 0;
			out = new ByteArrayOutputStream();
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
					cache = cipher.doFinal(byteData, offSet, MAX_ENCRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(byteData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_ENCRYPT_BLOCK;
			}
			return Base64.encodeBase64URLSafeString(out.toByteArray());
		} catch (Exception e) {
			throw e;
		}finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		
	}

	public static void main(String[] args) {

		/* RSA 1024 */
//        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIarYvrIMZGHKa8f2E6ubg0//28R1zJ4ArD+XELXYvDrM8UBR42PqJCpjPN3hC91YAnnk2Y9U+X5o/rGxH5ZTZzYy+rkAmZFJa1fK2mWDxPYJoxH+DGHQc+h8t83BMB4pKqVPhcJVF6Ie+qpD5RFUU/e5iEz8ZZFDroVE3ubKaKwIDAQAB";
//        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIhqti+sgxkYcprx/YTq5uDT//bxHXMngCsP5cQtdi8OszxQFHjY+okKmM83eEL3VgCeeTZj1T5fmj+sbEfllNnNjL6uQCZkUlrV8raZYPE9gmjEf4MYdBz6Hy3zcEwHikqpU+FwlUXoh76qkPlEVRT97mITPxlkUOuhUTe5sporAgMBAAECgYA0aSND37iifKUTaKOpXIKFoI23910EMAnrAXmaTIkafUBZjL7Ay0Q+QIcDHeGjgNlW9YvGXMbB5wMhMYKMgOUV1FpeqQdDslO4Z7zynRjkDJkjOKkE2/j10CvmNO8e2uCWKsYYUE9IyTkxcypjBCv16ifT0qmdxb7uKLccYI16eQJBANMutfNO/q7kUKiYvilBLN9+pZOg6eTmKmV0Xygoa3ClpQTfurwLA8W/Fv3oXnjHXTryNVHeoxSH69imo0RZ9kcCQQClXhMbXlfvl5iInmwziFhtYBztvkLuyQ084FgszR7iR0nuOWoURLQa5O7sLL724FNRlSvOCmmmWguh2vmQgRr9AkBDS5tHkWCvMqpRT3spgk9eWOlChgCCpKXV9qNsFJVILEDNsM28pnXpSd91wdp4+m7HHe/Hyv6EyFtrio50dYZ5AkAODVVwUO8GBArJKTUml+JzwOQUa8OCSQFf9+xmOjPypH4qySQzfrcTRfrrhM3haqSJ3TQwuP/LTAGLCnGEjwP9AkBqFFyrrQviPOhwel3NWjRv8mftOFgnm0Isk/NQJ4JtoahYvPDeUyP80WSuVWnPyV4zHz9Kw7BggYCPc4xZDACV";

		/* RSA 2048 */

//        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAichGTEP0QFswnvn+ZAQrgGHM8VeDZLJuezGhgxh4d9SyRUfnIW/zefT71rwS4bZUs1MPxJwavOyxABJOHLuckdHXknCsGEWz78gsA6D0+O+9dl1gCZR29nnN/NlzmNbSjFnzvsTJYBlS88qSr35RXFE+6DM7uPsS8Fm2I+65FteJ8p2yMvpSg72QkIX8xvI1F1uwXrciIB+4u7uTozxIplMOo4a6uhAm3W+Kjpz3ni2btjGqHRbqb3ebSZyl+nFfnjQaBe3XyVxAWDSanjgFj/wbqbeug9FBs+nQFVPIZR9z0aE5Ndi5o3eSkV7HFmWpkxaiPZ0BLRK3XHMaBtuSpwIDAQAB";
//        String privateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCJyEZMQ/RAWzCe+f5kBCuAYczxV4Nksm57MaGDGHh31LJFR+chb/N59PvWvBLhtlSzUw/EnBq87LEAEk4cu5yR0deScKwYRbPvyCwDoPT47712XWAJlHb2ec382XOY1tKMWfO+xMlgGVLzypKvflFcUT7oMzu4+xLwWbYj7rkW14nynbIy+lKDvZCQhfzG8jUXW7BetyIgH7i7u5OjPEimUw6jhrq6ECbdb4qOnPeeLZu2MaodFupvd5tJnKX6cV+eNBoF7dfJXEBYNJqeOAWP/Bupt66D0UGz6dAVU8hlH3PRoTk12Lmjd5KRXscWZamTFqI9nQEtErdccxoG25KnAgMBAAECggEBAIPz1b88ZTMtIgdejA7lH3Q4Nbn8gc1yRPSet3uBd/3rKT/IeMZBHQBzaqxgOgUIRV3n8nXsun6sf2b+IOjLlErimH2agnZMauL85YokH/g4QU6WZl9GXBf41xmMd3SsZ8AadaEBfYoXNqZcHtcLNogfFwvx5QRnD+A3SoRnH8OLBeVvOEe4AqHLT2xEZ9TeCf3fJe0Rf0fUIbw7I5ioiRZV/ir0L1VM7+1k2JODUkdC2Luj5Tl3nl1Eg6EmkYCmGE1bip1NAatsfjPBLMF7XdPNjLboiffjgKVBOjb7Y9vL18BCoLtWeTT2GkMpi5Sr94T1te1Ox77dF4BP33Xn7eECgYEA1TNUrAQsh14NbbkwFtUHXS8/YXt81p9wbSpFBymIawF2Lkk0913TB4CHSun45LhYXjdZZxK/TgqC5EIq5v2RA0jY3cSxoqVe6RZKB04E8wszeJHiEJPdu2vFnpZh9iAyhswiM5FmuKZKoWsVc2SZrBXAI02smSn3lXYok1VBS3sCgYEApXEZS6gjUu4o7ZL53Ur1HDfi/nxpkxqrPh+D1HVYjzjT+4vTeZwtLXt2VCInPWNXH+f11mzhxIrLkI0jMcSCah81DuU8aFXnqvPuyFvt9uaQBYlVWBtkcGZyeaxHFrbfCyeu0jm7SfwmiIg12hKlIHtPTjEZQUX+kkWr8cdaZ8UCgYEAh0Pl+K09QzVc97yC0jmeTnTnlYWvksvdnKUw3nZvYtSukndH75nLhfr524HOs+5xwnUDd+3hCjaJDSEd7yf5lUfmr+1XdoXNTb0igrfxU/JLWbfU4geuqnaaDyACTxHmfLePC4C413ZJ61fxaCDvjsrN+JgTZanGt0EcRT3WC3kCgYEAgf5/GMJxlw0JXbs515a5R8Xl9358Whj/at3KcRsPTeIiNqnkrc54dR9ol60KViMDZ0+VDDobn5pLXzZ26/jzXD1PLHgU4gp18Q6glhAdx/3cNm11gLhtUCA/XLlwVjm0wggZRpgUQIr/IBKe9c3mr8IUS2Uq6e38nKRf+adhst0CgYAM4tvl+U1MPbbz3YzDv8QPepZ7Pglgdfxqfr5OkXA7jNhqTZjSq10B6oClGvirBo1m6f26F02iUKk1n67AuiLlTP/RRZHi1cfq6P9IaXl23PcxJfUMvIxQDS0U+UTFpNXryTw/qNAkSfufN48YzKdGvc8vHrYJyaeemaVlbdJOCw==";

//		String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApkwSy4Z2rAdLzt4B1ZI0wlmP2S/LWL1rm2GuTQIQd6kEgAwb9Zh2eGhec94JnxD1FVWr/8srSwCIDdap54+d0em4iB6p7CP7EaLjhmQC7iuG9/tuFooGvIgNN9nv39k/qV3EBEhcUnnAIDOI1ChLhxzS2nNbrawaDKnB69UC6/ndmZEHJLf4kVfR40lskKjftVp//HK8jcytP2PA8LV+mM3ty8QwKj38ZX5Jp9XKejAbUQeCpAJ1M2BcXI/1E9c3M/321gZoQDOELM9VeohZ4IZEbX7VCh8zB5vaiBqz7SbRevykkRWazFP1UUsTD6PTq1f/U9ECaek8jxQIbYfQ3QIDAQAB";
//		String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCmTBLLhnasB0vO3gHVkjTCWY/ZL8tYvWubYa5NAhB3qQSADBv1mHZ4aF5z3gmfEPUVVav/yytLAIgN1qnnj53R6biIHqnsI/sRouOGZALuK4b3+24Wiga8iA032e/f2T+pXcQESFxSecAgM4jUKEuHHNLac1utrBoMqcHr1QLr+d2ZkQckt/iRV9HjSWyQqN+1Wn/8cryNzK0/Y8DwtX6Yze3LxDAqPfxlfkmn1cp6MBtRB4KkAnUzYFxcj/UT1zcz/fbWBmhAM4Qsz1V6iFnghkRtftUKHzMHm9qIGrPtJtF6/KSRFZrMU/VRSxMPo9OrV/9T0QJp6TyPFAhth9DdAgMBAAECggEAcV5POdyzLlfTAHYs/fIscYopHMEOebK4lzciYnFT1J+u57RZozaLHU7ZhVgLaxteWWsMVNem7fyww+5h6fDogGA7YuC0qr1kvgMz5TxtGv0liA0qYcc7DHAbwsWaWBp84fOAXAMQnVFO/yTi1O+bfcLSpAYcq+s13wgjoaiX01JsnLkuPPhyriApw0RzBb963b7t1Hx6QU2bA976WCWb+NxrpESdZjZ3yM6Og/2q7s307m58oQMarY10rFiRdDO2MB8EA7Yn0OPWADhLLq5fIekgnlUNzsoMfcr1VneOYzsksS3n2qTkZgVfH9UYAo+IDbhP6/Se733CARlLLN1w6QKBgQDVLT56yQVkU0HtwYRYzCEyv+9fvt4olHoWdx8iHtI4pGgOpL5SQ0WsVw5GFvsgNuA4P8oWEfzgpHwfWwSYu57gHBiUdZMzT3/wKQhWSm1Zt+HqZZwNrzUZ1Gq2CO7FDL4MLP1SywIUbVHOyXRN+3p+drQ/a8x/dRNQ5UtxcIc8UwKBgQDHtAQZv4IhDpdjJNb/p5dWbijs27ZMxouNwUcSUQjHypFHSJNfwnuYkQruXLMcspTXjExibm3jlL8qJPWd75vJMwag2e8loqyAnO+CtMoSnmyS65UDCd8okYVDJ6vL4qh28Nw8MRs4SlkFQYgt+Nr9ghsMSMIKK3AAC5XYJc6YDwKBgBgUi0k9aeGCUiE0Senp7RoZekW7fsbgtsXQ4PBuwNprH+H/lAwg0z77en5KXEWwTTWtZvuFPqZ1jhvQoT1LfE/efJxQh7c1FMOhLvLoZ1/KGLfskGsvTnVDfGZhre8OWG1xq4/tpKeADcypDv08GgMtD4FWgPwo0mzB6fq/dldHAoGBAKwyPu7swQo8CLGGjJS5L7dpGYTTFugwsxiyNdRHZ2jO9GIg5GLH0ccGGgTajd8K5WjPIvYk/kAkfImcgOWEPDJK+0YrZRhwoonryGJN/Yf8buZomH6cPn4+rEfxpyMQ9+5HVb/ZH/1zrjrn/MKXOE0rqKL5Ace/GL4RP023m+JlAoGBALldDgPRRBaA5gyM4emXWHn29S+HDrWALr3NL4cqE7efdw/pJfRJt7DL624N+7TUZ125pGogpwBiHoypPUEyQOUaQR7E2aJvH7jT09xCoHwXWbdRKwLCDCxOc2SjDLSlBXlgfFXfK0XkVv6O9dRIAlz4mBRp0lBLpe3MiX4qfrYy";

		try {
			

        	Map<String, String> map = RSA2Utils.genKeyPair();
        	String publicKey = map.get(PUBLIC_KEY);
        	System.out.println("publicKey:"+publicKey);
        	String privateKey = map.get(PRIVATE_KEY);
        	System.out.println("privateKey:"+privateKey);
        	
			String data = "测试提交数据";

			String encrypt = RSA2Utils.encrypt(data, publicKey);
			System.out.println("公钥加密后的数据：" + encrypt);
			String decrypt = RSA2Utils.decrypt(encrypt, privateKey);
			System.out.println("私钥解密后的数据：" + decrypt);
			System.out.println("--------------------");
			
//			String signData = RSA2Utils.sign(data, privateKey);
//			System.out.println("私钥签名后的数据：" + signData);
//
//			boolean isSign = RSA2Utils.verify(data, publicKey, signData);
//			System.out.println("签名是否正确：" + isSign);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}
