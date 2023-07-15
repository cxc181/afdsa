package com.yuqian.itax.util.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;


/**
 * 正则表达式工具类
 *
 * @author LiuXianTing
 * @date: 2017年7月10日 上午9:50:48
 */
public class MatcherUtil {

    /**
     * 手机号码正则表达式
     */
    private static final String PHONE_PATTERN = "^0?1[3|4|5|6|7|8|9][0-9]\\d{8}$";

    /**
     * 隐藏手机号码中间四位
     *
     * @param phone
     * @return
     */
    public static String getphone(String phone) {
        if (StringUtils.isNotBlank(phone)) {
            return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return "";
    }

    /**
     * 验证是否是手机号码
     *
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone) {
        return validate(PHONE_PATTERN, phone);
    }

    /**
     * 验证
     *
     * @param pattern
     * @param text
     * @return
     */
    private static boolean validate(String pattern, String text) {
        return Pattern.matches(pattern, text);
    }

    public static void main(String[] args) {
//        System.out.println(isPhone("00123547854"));
    }
}
