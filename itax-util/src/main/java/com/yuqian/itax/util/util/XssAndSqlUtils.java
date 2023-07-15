package com.yuqian.itax.util.util;


import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName XssAndSqlUtils
 * @Description XSS、SQL 脚本检测工具类
 * @Author jiangni
 * @Date 2019/7/15
 * @Version 1.0
 */
@Slf4j
public class XssAndSqlUtils {

    private XssAndSqlUtils(){}

    /**
     * 字符后面必须要有空格，不然会导致参数被过滤
     */
    private static final String[] BAD_STRS = " and | exec | execute | insert | ordercreate | drop | table | from |grant|group_concat|column_name|information_schema.columns|table_schema|union |where |select |delete |update |chr |mid |master |truncate |char | declare | or |-- |% ".split("\\|");


    /**
     * 将容易引起xss & sql漏洞的半角字符直接替换成全角字符
     *
     * @param s
     * @return
     */
    public static String xssEncode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        } else {
            if (sqlValidate(s)) {
                throw new XssAndSqlException("输入参数包含非法字符或关键字，请检查输入参数");
            } else {
                s = htmlEncode(s);
            }
        }
        return s;
    }

    /**
     * 检查是否包含sql关键字，可以手动添加
     */
	private static boolean sqlValidate(String str) {
        str = str.toLowerCase();
        for (int i = 0; i < BAD_STRS.length; i++) {
            if (str.contains(BAD_STRS[i])) {
                log.error("输入参数【" + str + "】包含非法字符或关键字：" + BAD_STRS[i]);
                return true;
            }
        }
        return false;
    }

	private static String htmlEncode(String source) {
        if (source == null) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            switch (c) {
                case '>':
                    buffer.append("＞");
                    break;
                case '<':
                    buffer.append("＜");
                    break;
                case '&':
                    buffer.append("＆");
                    break;
                case '#':
                    buffer.append("＃");
                    break;
                case 10:
                case 13:
                    break;
                default:
                    buffer.append(c);
            }
        }
        return buffer.toString();
    }

    /**
     * SQL注入异常
     */
    @SuppressWarnings("serial")
	public static class XssAndSqlException extends RuntimeException{

        public XssAndSqlException(String message) {
            super(message);
        }
    }

}
