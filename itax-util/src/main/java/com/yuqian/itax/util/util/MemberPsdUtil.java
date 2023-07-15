package com.yuqian.itax.util.util;



/**
 * 会员的密码服务
 * 
 * @author HZ
 * @date 20190813
 */
public final class MemberPsdUtil {

	private MemberPsdUtil(){}

	public static final String SALT = "FIGHT IT BOY"; //老版本默认盐值

	public static String encrypt(String password, String memberNo,String SALT) {
		return Md5Util.MD5(Md5Util.MD5(password).substring(12) + SALT	+ memberNo);
	}
}


