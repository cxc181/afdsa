package com.yuqian.itax.util.util.nabei;

import com.yuqian.itax.util.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title SM2Utils.java
 * @Description 国密算法SM2工具类
 * @author kevin.wlb
 * @createTime 2019年4月2日
 */
@Slf4j
public class SM2Utils {

	public final static String PUBLIC_KEY = "publicKey";

	public final static String PRIVATE_KEY = "privateKey";

	/**
	 *  生成SM2随机秘钥对
	 * @return Map<String,String>{ publicKey="", privateKey="" }
	 */
	public static Map<String, String> generateKeyPair() {
		SM2 sm2 = SM2.Instance();
		AsymmetricCipherKeyPair key = sm2.ecc_key_pair_generator.generateKeyPair();
		ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) key.getPrivate();
		ECPublicKeyParameters ecpub = (ECPublicKeyParameters) key.getPublic();
		BigInteger privateKey = ecpriv.getD();
		ECPoint publicKey = ecpub.getQ();

//		System.out.println("public key: " + Utils.byteToHex(publicKey.getEncoded()));
//		System.out.println("private key: " + Utils.byteToHex(privateKey.toByteArray()));

		Map<String, String> keysMap = new HashMap<String, String>();
		keysMap.put(PUBLIC_KEY, Utils.byteToHex(publicKey.getEncoded()));
		keysMap.put(PRIVATE_KEY, Utils.byteToHex(privateKey.toByteArray()));

		return keysMap;
	}

	// 数据加密
	public static String encrypt(byte[] publicKey, byte[] data) throws IOException {
		if (publicKey == null || publicKey.length == 0) {
			return null;
		}

		if (data == null || data.length == 0) {
			return null;
		}

		byte[] source = new byte[data.length];
		System.arraycopy(data, 0, source, 0, data.length);

		Cipher cipher = new Cipher();
		SM2 sm2 = SM2.Instance();
		ECPoint userKey = sm2.ecc_curve.decodePoint(publicKey);

		ECPoint c1 = cipher.Init_enc(sm2, userKey);
		cipher.Encrypt(source);
		byte[] c3 = new byte[32];
		cipher.Dofinal(c3);

		// System.out.println("C1 " + Utils.byteToHex(c1.getEncoded()));
		// System.out.println("C2 " + Utils.byteToHex(source));
		// System.out.println("C3 " + Utils.byteToHex(c3));
		// C1 C2 C3拼装成加密字串
		return Utils.byteToHex(c1.getEncoded()) + Utils.byteToHex(source) + Utils.byteToHex(c3);
	}

	// 数据解密
	public static byte[] decrypt(byte[] privateKey, byte[] encryptedData) throws IOException {
		if (privateKey == null || privateKey.length == 0) {
			return null;
		}

		if (encryptedData == null || encryptedData.length == 0) {
			return null;
		}
		// 加密字节数组转换为十六进制的字符串 长度变为encryptedData.length * 2
		String data = Utils.byteToHex(encryptedData);
		/***
		 * 分解加密字串 （C1 = C1标志位2位 + C1实体部分128位 = 130） （C3 = C3实体部分64位 = 64） （C2 = encryptedData.length * 2 - C1长度 - C2长度）
		 */
		byte[] c1Bytes = Utils.hexToByte(data.substring(0, 130));
		int c2Len = encryptedData.length - 97;
		byte[] c2 = Utils.hexToByte(data.substring(130, 130 + 2 * c2Len));
		byte[] c3 = Utils.hexToByte(data.substring(130 + 2 * c2Len, 194 + 2 * c2Len));

		SM2 sm2 = SM2.Instance();
		BigInteger userD = new BigInteger(1, privateKey);

		// 通过C1实体字节来生成ECPoint
		ECPoint c1 = sm2.ecc_curve.decodePoint(c1Bytes);
		Cipher cipher = new Cipher();
		cipher.Init_dec(userD, c1);
		cipher.Decrypt(c2);
		cipher.Dofinal(c3);

		// 返回解密结果
		return c2;
	}

	public static void main(String[] args) {
		try {
			// 生成密钥对
			System.out.println(SM2Utils.generateKeyPair());


//		String plainText = "merchantNo=BM123456&merchantName=测试merchantNo=BM123456&merchantName=测试merchantNo=BM123456&merchantName=测试merchantNo=BM123456&merchantName=测试merchantNo=BM123456&merchantName=测试merchantNo=BM123456&merchantName=测试merchantNo=BM123456&merchantName=测试merchantNo=BM123456&merchantName=测试merchantNo=BM123456&merchantName=测试merchantNo=BM123456&merchantName=测试merchantNo=BM123456&merchantName=测试merchantNo=BM123456&merchantName=测试merchantNo=BM123456&merchantName=测试";
//		byte[] sourceData = plainText.getBytes();
//
//		// 下面的秘钥可以使用generateKeyPair()生成的秘钥内容
//		// 国密规范正式私钥
//		String prik = "03840E93DFE6DDA894081C57A158E598B3B00D4BC99FE6AB994B40733784A565";
//		// 国密规范正式公钥
//		String pubk = "049E68857BF477DF0C8B0CE6681F1EF70D8209B8421E746D30FF3E306A23D8566E429854171F5942E066F99A44F7B0BDD9322A4B879BB88FFF96B22B9DDEB4B8D2";
//
//		System.out.println("加密: ");
//		String cipherText = SM2Utils.encrypt(Utils.hexToByte(pubk), sourceData);
//		System.out.println(cipherText);
//		System.out.println("解密: ");
//		plainText = new String(SM2Utils.decrypt(Utils.hexToByte(prik), Utils.hexToByte(cipherText)));
//		System.out.println(plainText);
		}catch (Exception e){
			log.error(e.getMessage());
		}
	}
}
