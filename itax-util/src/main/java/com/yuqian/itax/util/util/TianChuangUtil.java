package com.yuqian.itax.util.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * className TianChuangUtil
 * Description
 * author Jerry
 * DATE 2019/3/25 15:20
 * version [版本号,  2017/6/5]
 */
@Slf4j
public class TianChuangUtil {

    private TianChuangUtil(){}
    /**
     *
     * 名称: sortParam
     * 作者：陈祥
     * 日期：2017年10月11日 下午2:25:12
     * 描述: 生成参数字符串，参数key按字典序排列
     * 参数： param-生成token需要的参数
     * 返回值： String
     * 异常：
     *
     */
    public static String sortParam(Map<String, String> param) {
        if (null == param || 0 == param.size()) {
            return "";
        }
        // 排序键，按照字母先后进行排序
        Iterator<String> iterator = param.keySet().iterator();
        String[] arr = new String[param.size()];
        for (int i = 0; iterator.hasNext(); i++) {
            arr[i] = iterator.next();
        }
        Arrays.sort(arr);
        // 生成进行MD5摘要的字符串
        StringBuilder sb = new StringBuilder();
        for (String key : arr) {
            String value = param.get(key);
            if (StringUtils.isNotBlank(value)) {
                sb.append(key).append("=").append(value).append(",");
            }
        }
        // 检查结果
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        } else {
            return "";
        }
    }

    /**
     *
     * 名称: md5Hex
     * 作者：陈祥
     * 日期：2017年10月11日 下午2:47:44
     * 描述: 对字符串进行md5摘要，然后转化成16进制字符串
     *       使用标准的md5摘要算法
     * 参数： text-需要进行摘要的字符串
     * 返回值： 进行MD5摘要以及16进制转化后的字符串
     *
     */
    public static String md5Hex(String text) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(text.trim().getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                int high = (bytes[i] >> 4) & 0x0f;
                int low = bytes[i] & 0x0f;
                sb.append(high > 9 ? (char) ((high - 10) + 'a') : (char) (high + '0'));
                sb.append(low > 9 ? (char) ((low - 10) + 'a') : (char) (low + '0'));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("系统不支持MD5算法");
        } catch (UnsupportedEncodingException e) {
            log.error("系统不支持指定的编码格式");
        }
        return null;
    }
}