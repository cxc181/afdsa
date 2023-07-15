package com.yuqian.itax.util.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/10 20:45
 *  @Description: 用户资金账号生成器
 */
public final class UniqueNumGenerator {

	private static final String DATE_FORMAT = "yyMMddHHmmss";

	private UniqueNumGenerator() {

	}

	private static Integer serialNo = 0;   //自增的流水号计数器
	private static final Integer MAX_SERIAL_NO = 99999;//最大计数值
	private static final int SERIAL_NO_LEN = 5;
	private static final Integer MAX_SERIAL_NO_1 = 999999999;//最大计数值
	private static final int SERIAL_NO_LEN_1 = 7;
	private static final String FILL_CHAR = "0000000000000000";//不足位数左补零
	private static Random random = new Random();
	private static String michineNo = "01";

	/**
	 * 生成唯一资金账号
	 * 订单号组成格式： yyMMddHHmmss+两位机器码+五位计数器
	 * @return
     */
	public static final String generateUniqueNo(){
		synchronized(UniqueNumGenerator.class){
			int sNo = serialNo;
			serialNo++;
			if(serialNo > MAX_SERIAL_NO){
				serialNo = 0;
			}
			SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
			return format.format(new Date()) + michineNo + FILL_CHAR.substring(0,SERIAL_NO_LEN - (sNo + "").length()) + sNo;
		}
	}

	/**
	 * 生成唯一长度为24的支付流水号
	 * 订单号组成格式： yyMMddHHmmss + 5位随机数 + 7位计数器
	 * @return
	 */
	public static final String generatePayNo(){
		synchronized(UniqueNumGenerator.class){
			int sNo = serialNo;
			serialNo++;
			if(serialNo > MAX_SERIAL_NO_1){
				serialNo = 0;
			}
			SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
			return format.format(new Date()) + getRandomNum() + FILL_CHAR.substring(0,SERIAL_NO_LEN_1 - (sNo + "").length()) + sNo;
		}
	}

	/**
     * 产生随机的5位数（不足位左补0）
     * @return 
     */  
    public static String getRandomNum(){
        Random rad = new Random();
        String result  = rad.nextInt(99999) + "";
		if(result.length() == 1){
			result = "0000" + result;
		}else if(result.length() == 2){
			result = "000" + result;
		}else if(result.length() == 3){
			result = "00" + result;
		}
		else if(result.length() == 4){
			result = "0" + result;
		}
		return result;
    }
}

