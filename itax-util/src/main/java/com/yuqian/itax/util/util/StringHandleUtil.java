package com.yuqian.itax.util.util;

import org.apache.commons.lang3.StringUtils;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/30 15:15
 *  @Description: 字符串处理工具类
 */
public class StringHandleUtil {

    /**
     * @Description 身份证号码脱敏（显示前4位和后4位）
     * @Author  Kaven
     * @Date   2020/3/30 15:19
     * @Param   idCardNo
     * @Return  String
     * @Exception
    */
    public static String desensitizeIdCardNo(String idCardNo){
        if(StringUtils.isBlank(idCardNo)){
            return null;
        }else{
            return idCardNo.replaceAll("(\\w{4})\\w+(\\w{4})", "$1**********$2");
        }
    }

    /**
     * 去*开头的字符串
     * @param str
     * @return
     */
    public static String removeStar(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        str = replace(str);
        while (StringUtils.isNotBlank(str) && str.startsWith("*")) {
            str = str.substring(1);
        }
        return str;
    }

    /**
     * 中文转英文符号
     * @param str
     * @return
     */
    public static String replace(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return str.replace("＊", "*").replace("，", ",");
    }
}