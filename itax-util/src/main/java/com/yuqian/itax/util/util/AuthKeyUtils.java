package com.yuqian.itax.util.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.duob.encrypt.EncryptUtil;
import com.yuqian.itax.util.util.channel.ChannelUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/17 16:01
 *  @Description: 要素认证工具类（二、三、四要素）
 */
@Slf4j
public class AuthKeyUtils {
    /**
     * @Description 二要素认证
     * @Author  Kaven
     * @Date   2019/12/17 16:51
     * @Param  agentNo signKey authUrl paramsValue
     * @Return String
    */
    public static String auth2Key(String agentNo,String signKey,String authUrl,String name,String idCard,String paramsValues) {
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("requestId", System.currentTimeMillis());
        orderMap.put("name", name);
        orderMap.put("idcard", idCard);
        if(paramsValues.trim().indexOf("\"channel\":\"new\"") > -1){ // 新加解密方式
            orderMap.put("authType", "12");
        }

        log.info("身份证二要素认证data数据：" + JSONUtil.toJsonPrettyStr(orderMap));

        String result = null;
        try {
            result = ChannelUtils.callApi(paramsValues,orderMap,agentNo,signKey,authUrl,"AUTH");
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        log.info("身份证二要素认证平台返回结果：\n" + result);
        return result;
    }

    /**
     * @Description 银行卡三要素认证
     * @Author  Kaven
     * @Date   2019/12/17 16:51
     * @Param  agentNo signKey authUrl
     * @Return String
     */
    public static String auth3Key(String agentNo,String signKey,String authUrl,String name,String idCard,String bankCard) {
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("requestId", System.currentTimeMillis());
        orderMap.put("name", name);
        orderMap.put("idcard", idCard);
        orderMap.put("bankcard", bankCard);

        log.info("银行卡三要素认证data数据：" + JSONUtil.toJsonPrettyStr(orderMap));

        Map<String, Object> pubParamMap = new HashMap<>();
        pubParamMap.put("data", JSONUtil.toJsonStr(orderMap));
        pubParamMap.put("agentNo", agentNo);
        pubParamMap.put("keyNum", "AUTH");
        pubParamMap.put("timestamp", System.currentTimeMillis() + "");
        pubParamMap.put("version", "1.0.0");

        String sign = EncryptUtil.sm3sign(pubParamMap, signKey);

        pubParamMap.put("sign", sign);
        String jsonParams = JSONUtil.toJsonStr(pubParamMap);
        log.info("银行卡三要素认证平台请求数据:\n" + JSONUtil.toJsonPrettyStr(jsonParams));

        HttpResponse response = HttpRequest.post(authUrl).body(jsonParams, "application/json").charset("utf-8").execute();
        String result = response.body();

        log.info("银行卡三要素认证平台返回结果：\n" + result);
        return result;
    }


    /**
     * @Description 银行卡四要素认证
     * @Author  Kaven
     * @Date   2019/12/17 16:51
     * @Param  agentNo signKey authUrl
     * @Return String
     */
    public static String auth4Key(String agentNo,String signKey,String authUrl,String name,String idCard,String bankCard,String mobile,String paramsValues) {
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("requestId", System.currentTimeMillis());
        orderMap.put("name", name);
        orderMap.put("idcard", idCard);
        orderMap.put("bankcard", bankCard);
        orderMap.put("mobile", mobile);
        if(paramsValues.trim().indexOf("\"channel\":\"new\"") > -1){ // 新加解密方式
            orderMap.put("authType", "11");
        }

        log.info("银行卡四要素认证data数据：" + JSONUtil.toJsonPrettyStr(orderMap));

        String result = null;
        try {
            result = ChannelUtils.callApi(paramsValues,orderMap,agentNo,signKey,authUrl,"AUTH");
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        log.info("银行卡四要素认证平台返回结果：\n" + result);
        return result;
    }
}