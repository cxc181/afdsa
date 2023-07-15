package com.yuqian.itax.util.util.channel;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duob.encrypt.EncryptUtil;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName 渠道接口调用封装
 * @Description TODO
 * @Author jiangni
 * @Date 2020/8/13
 * @Version 1.0
 */
@Slf4j
public class ChannelUtils {

    /**
     * 渠道接口调用封装（适用于支付相关接口，data需要加密的）
     * @param dataParams 业务参数
     * @param account  渠道编码
     * @param publicKey 平台工业
     * @param secKey 加密key
     * @param url 请求url
     * @throws Exception
     */
     public static JSONObject callApi2Md5(Map<String,Object> dataParams, String account, String publicKey, String secKey, String url) throws Exception{
         String uuid = UUID.randomUUID().toString();
         Map<String, Object> pubParamMap = new HashMap<>();
         pubParamMap.put("data",Base64.encode(DESUtils.des3EncodeECB(uuid.getBytes(), JSONUtil.toJsonStr(dataParams).getBytes())));
         pubParamMap.put("merNo", account);
         pubParamMap.put("keyNum", Base64.encode(RSA2Util.encryptByPublicKey(uuid.getBytes(), publicKey)));
         pubParamMap.put("timestamp", System.currentTimeMillis() + "");
         pubParamMap.put("version", "1.0.0");
         pubParamMap.put("signType","MD5");
         pubParamMap.put("sign", RSA2Util.md5sign(pubParamMap,secKey));
         String jsonParams = JSONUtil.toJsonStr(pubParamMap);
         log.info("平台请求数据:" + JSONUtil.toJsonPrettyStr(jsonParams));
         HttpResponse response = HttpRequest.post(url).body(jsonParams, "application/json").charset("utf-8").execute();
         String result = response.body();
         log.info("====上游通道返回结果：" + result);
         JSONObject jsonObject = JSON.parseObject(result);
         if(jsonObject == null || !"00".equals(jsonObject.getString("code"))){
             String errormsg = "上游无结果返回";
             if(jsonObject!=null){
                 errormsg = jsonObject.getString("msg");
             }
             throw new Exception("----上游渠道请求异常："+errormsg);
         }
         return  JSON.parseObject(jsonObject.getString("data"));
     }

     /**
      * @Description 渠道接口调用封装（适用于非支付相关接口，data不需要加密的）
      * @Author  Kaven
      * @Date   2020/8/13 11:14
      * @Param  paramsValues dataParams  account  secKey url keyNum
      * @Return
      * @Exception
     */
    public static String callApi(String paramsValues, Map<String,Object> dataParams,String account,String secKey,String url,String keyNum) throws Exception{
        Map<String, Object> pubParamMap = new HashMap<>();
        pubParamMap.put("data", JSONUtil.toJsonStr(dataParams));
        pubParamMap.put("keyNum", keyNum);
        pubParamMap.put("timestamp", System.currentTimeMillis() + "");
        pubParamMap.put("version", "1.0.0");

        // 加解密方式
        String sign = null;
        if(StringUtil.isNotBlank(paramsValues) && paramsValues.trim().indexOf("\"channel\":\"new\"") > -1){
            pubParamMap.put("merNo", account);
            pubParamMap.put("signType","MD5");
            sign = RSA2Util.md5sign(pubParamMap, secKey);
        } else {
            pubParamMap.put("agentNo", account);
            sign = EncryptUtil.sm3sign(pubParamMap, secKey);
        }
        pubParamMap.put("sign", sign);
        String jsonParams = JSONUtil.toJsonStr(pubParamMap);
        log.info("平台请求数据:" + JSONUtil.toJsonPrettyStr(jsonParams));
        HttpResponse response = HttpRequest.post(url).body(jsonParams, "application/json").charset("utf-8").execute();
        log.info("----平台返回数据结果：" + response.body());
        String result = "";
        if(StringUtil.isNotBlank(paramsValues) && paramsValues.trim().indexOf("\"channel\":\"new\"") > -1){
            JSONObject jsonObject = JSON.parseObject(response.body());
            if(jsonObject == null || !"00".equals(jsonObject.getString("code"))){
                String errormsg = "上游无结果返回";
                if(jsonObject!=null){
                    errormsg = jsonObject.getString("message");
                }
                throw new Exception("----上游渠道请求异常："+errormsg);
            }
            JSONObject  dataJsonObject= JSON.parseObject(jsonObject.getString("data"));
            jsonObject.put("code",dataJsonObject.getString("bizCode"));
            jsonObject.put("msg",dataJsonObject.get("bizCodeMsg"));
            result = jsonObject.toJSONString();
        }else{
            result = response.body();
        }
        log.debug("平台返回结果：" + result);
        return result;
    }
}
