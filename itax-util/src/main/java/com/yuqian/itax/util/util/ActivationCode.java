package com.yuqian.itax.util.util;

/**
 * 激活码
 * 
 * @author huangmiao
 * @version $Id: ActivationCode.java, v 0.1 2012-3-27 上午9:40:59 huangmiao Exp $
 */
public class ActivationCode {

    private static final String ONE = "0";
    private static final String TWO = "10";

    /**
     * 获取激活码
     * 
     * @return
     */
    public static String getActivationCode() {
        double d = Math.random() * 900000 + 1000;
        return leftAdd(String.valueOf((int) d));
    }
    
    public static String getActivationCodeShort() {
        double d = Math.random()* 9000 + 1000;
        return String.valueOf((int) d);
    }

    private static String leftAdd(String value) {
        switch (value.length()) {
            case 4:
                value = TWO + value;
                break;
            case 5:
                value = ONE + value;
                break;
            default:
                value = "" + value;
        }
        return value;
    }
}

