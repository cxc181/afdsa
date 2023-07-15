package com.yuqian.itax.util.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.Map;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/6 15:50
 *  @Description: 消息构建器工具类
 */
public final class ImContentCreator {

    /** 模板替代间隔符 */
    private static final String TEMPLATE_SPLIT = "#";

    /**
     * 消息内容创建
     * @param customPattern 用户自定义模板，只要遵行内置模板的规则
     * @param data
     * @return
     */
    public static final String customMerge(String customPattern, Map<String, Object> data) {

        if (StringUtils.isBlank(customPattern)) {
            return null;
        }

        if (CollectionUtils.isEmpty(data)) {
            return customPattern;
        }

        for (Iterator<String> i = data.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            Object value = getMapValue(data, key);

            if (StringUtils.isBlank(key)) {
                continue;
            }

            String varKey = TEMPLATE_SPLIT + key + TEMPLATE_SPLIT;
            String varValue = (value != null) ? value.toString() : "";

            // 替换本变量
            customPattern = StringUtils.replace(customPattern, varKey, varValue);
        }
        return customPattern;
    }

    /**
     * 获取参数map中指定key的值
     * @param data
     * @param key
     * @return
     */
    private static Object getMapValue(Map<String, Object> data, String key) {
        return data.get(key);
    }
}

