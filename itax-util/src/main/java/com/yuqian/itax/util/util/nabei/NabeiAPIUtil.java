package com.yuqian.itax.util.util.nabei;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.*;
@Slf4j
public class NabeiAPIUtil {
	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D",
			"E", "F" };

	/**
	 * <p>
	 * 签名方式： 1.获取参与签名的字段，按ASII排序后
	 * </p>
	 * 
	 * @param obj
	 * @param md5Key
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static String sign(Object obj, String md5Key) {
		try {
			Map<String, String> signMap = new TreeMap<>();
			List<Field> fieldList = new ArrayList<Field>();
			Class clazz = obj.getClass();
			Field[] fields = clazz.getDeclaredFields();
			fieldList.addAll(Arrays.asList(fields));
			// 父类所有属性
			Class superClazz = clazz.getSuperclass();
			Field[] superfields = superClazz.getDeclaredFields();
			fieldList.addAll(Arrays.asList(superfields));
			for (Field field : fieldList) {
				field.setAccessible(true);
				// 字段有@SignInclude注解且有值时，参与签名
				if (field.isAnnotationPresent(SignInclude.class)) {
					String key = field.toString().substring(field.toString().lastIndexOf(".") + 1);
					String value = String.valueOf(field.get(obj));
					// value不为空
					if (StringUtils.isNotBlank(value) && !"NULL".equals(value.toUpperCase())) {
						signMap.put(key, value);
					}
				}
			}
			StringBuffer buffer = new StringBuffer();
			for (String key : signMap.keySet()) {
				buffer.append(signMap.get(key)).append("&");
			}
			buffer.append(md5Key);
			System.out.println("原签名串：" + buffer.toString());
			String hmac = getMD5Code(buffer.toString());
			System.out.println("签名串：" + hmac);
			return hmac;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/**
	 * 验证签名
	 *
	 * @param obj    对象
	 * @param md5Key 签名秘钥
	 * @param hmac   签名字符串
	 * @return
	 * @throws Exception
	 */
	public static boolean verify(Object obj, String md5Key, String hmac) {
		String _hmac = sign(obj, md5Key);
		if (_hmac.equals(hmac)) {
			return true;
		}
		return false;
	}

	public static String getMD5Code(String strObj) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		return byteToString(md.digest(strObj.getBytes()));
	}

	// 转换字节数组为16进制字串
	private static String byteToString(byte[] bByte) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < bByte.length; i++) {
			sBuffer.append(byteToArrayString(bByte[i]));
		}
		return sBuffer.toString();
	}

	// 返回形式为数字跟字符串
	private static String byteToArrayString(byte bByte) {
		int iRet = bByte;
		// System.out.println("iRet="+iRet);
		if (iRet < 0) {
			iRet += 256;
		}
		int iD1 = iRet / 16;
		int iD2 = iRet % 16;
		return strDigits[iD1] + strDigits[iD2];
	}

	public static void main(String[] args){

	}
}
