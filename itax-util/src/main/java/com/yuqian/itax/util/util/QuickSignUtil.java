package com.yuqian.itax.util.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class QuickSignUtil {

    private QuickSignUtil(){}

    /**
     * md5签名方式1 参数直接拼接不用&符号键值对=好连接
     * @param params
     * @param signKey
     * @return
     */
    public static String md5sign1(Map<String, String> params, String signKey) {
        // 根据元素的自然顺序按升序进行排序
        List<String> keys = Arrays.asList(params.keySet().toArray(new String[params.size()]));
        Collections.sort(keys);

        // 根据不同的签名方法计算签名值
        StringBuilder sb = new StringBuilder();
        sb.append(signKey);
        for (String key : keys) {
            String value = params.get(key);
            if (StringUtils.isEmpty(value) || "sign".equals(key)) {
                continue;
            }
            sb.append(key).append(StringUtils.trim(value));
        }
        sb.append(signKey);
        log.info("参数md5前字符串：{}", sb.toString());
        return Md5Util.encrypt(sb.toString());
    }

}
