package com.yuqian.itax.util.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/20 17:31
 *  @Description: 订单编码码生成器，生成32位数字编码，
 *  @生成规则 1位单号类型+17位时间戳+14位(用户id加密&随机数)
 */
public class OrderNoFactory {
    /** 订单类别头 */
    private static final String ORDER_CODE = "D";
    /** 工单类别头 */
    private static final String WORK_ORDER_CODE = "G";
    /** 分润类别头 */
    private static final String PROFITS_ORDER_CODE = "P";
    /** 集团类别头 */
    private static final String GROUP_ORDER_CODE = "T";
    /** 百旺电票头 */
    private static final String BW_INVOICE_ORDER_CODE = "BI";
    /** 随机编码 */
    private static final int[] R = new int[]{7, 9, 6, 2, 8 , 1, 3, 0, 5, 4};
    /** 用户id和随机数总长度 */
    private static final int MAX_LENGTH = 4;

    /**
     * 更具id进行加密+加随机数组成固定长度编码
     */
    private static String toCode(Long id) {
        String idStr = id.toString();
        StringBuilder idsbs = new StringBuilder();
        for (int i = idStr.length() - 1 ; i >= 0; i--) {
            idsbs.append(R[idStr.charAt(i)-'0']);
        }
        return idsbs.append(getRandom(MAX_LENGTH - (long)idStr.length())).toString();
    }

    /**
     * 生成时间戳
     */
    private static String getDateTime(){
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(new Date());
    }

    /**
     * 生成固定长度随机码
     * @param n    长度
     */
    private static long getRandom(long n) {
        long min = 1,max = 9;
        for (int i = 1; i < n; i++) {
            min *= 10;
            max *= 10;
        }
        long rangeLong = (((long) (new Random().nextDouble() * (max - min)))) + min ;
        return rangeLong;
    }

    /**
     * 生成不带类别标头的编码
     * @param userId
     */
    private static synchronized String getCode(Long userId){
        userId = userId == null ? 10000 : userId;
        return getDateTime() + toCode(userId);
    }

    /**
     * 生成订单号
     * @param userId
     */
    public static String getOrderCode(Long userId){
        return ORDER_CODE + getCode(userId);
    }

    /**
     * 生成分润流水号
     * @param userId
     */
    public static String getProfitsNo(Long userId){
        return PROFITS_ORDER_CODE + getCode(userId);
    }

    /**
     * 生成工单号
     * @param userId
     */
    public static String getWorkOrderCode(Long userId){
        return WORK_ORDER_CODE + getCode(userId);
    }

    /**
     * 生成集团订单号
     * @param userId
     */
    public static String getGroupOrderCode(Long userId){
        return GROUP_ORDER_CODE + getCode(userId);
    }

    /**
     * 生成开票记录订单号
     * @param userId
     */
    public static String getBWOrderCode(Long userId){
        return BW_INVOICE_ORDER_CODE + getCode(userId);
    }


    public static void main(String[] args) {
        System.out.println(getOrderCode(11L));
    }
}