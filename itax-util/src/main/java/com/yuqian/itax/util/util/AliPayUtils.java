package com.yuqian.itax.util.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.google.common.collect.Maps;
import com.yuqian.itax.util.entity.AliPayDto;
import com.yuqian.itax.util.util.channel.DESUtils;
import com.yuqian.itax.util.util.channel.RSA2Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 渠道端支付宝支付相关工具类
 * @Author  Kaven
 * @Date   2020/10/21 11:43
*/
@Slf4j
public class AliPayUtils {
    /**
     * @Description 渠道端支付宝支付接口封装
     * @Author  Kaven
     * @Date   2020/1/7 16:46
     * @Param  WechatPayDto
     * @Return Map<String,String>
     * @Exception
    */
    public static Map<String,Object> aliPay(AliPayDto payDto) {
        log.info("请求渠道支付宝支付接口开始：");
        Map<String,Object> result = Maps.newHashMap();
        try{
            // 加密请求参数，发送HTTP请求交易接口
            Map<String,Object> orderMap = Maps.newHashMap();
            orderMap.put("orderNo", payDto.getTradeNo()); // 支付宝订单流水号
            orderMap.put("amount", payDto.getAmount()); // 金额：分为单位
            orderMap.put("userIp", payDto.getIpAddr()); // 设备ip
            orderMap.put("notifyUrl", payDto.getCallbackUrl());// 回调地址
            orderMap.put("appId", payDto.getAppId());// 小程序appId
            orderMap.put("appPrivateKey", payDto.getAppPrivateKey());// 小程序私钥
            orderMap.put("alipayPublicKey", payDto.getAlipayPublicKey());// 支付宝公钥
            orderMap.put("buyerLogonId", payDto.getBuyerLogonId());// 买家支付宝账号
            orderMap.put("buyerId", payDto.getBuyerId());// 买家支付宝用户号
            orderMap.put("productCode", payDto.getProductCode());// 渠道产品编码

            log.info("data数据明文：" + JSONUtil.toJsonPrettyStr(orderMap));

            String data = Base64.encode(DESUtils.des3EncodeECB(payDto.getKeyNum().getBytes(), JSONUtil.toJsonStr(orderMap).getBytes())); // 加密数据
            String enckeyNum =  Base64.encode(RSA2Util.encryptByPublicKey(payDto.getKeyNum().getBytes(), payDto.getServicePubKey()));

            log.info("data加密数据：" + data);

            log.info("数据秘钥加密:" + enckeyNum);

            Map<String, Object> pubParamMap = new HashMap<>();
            pubParamMap.put("data", data);
            pubParamMap.put("keyNum", enckeyNum);
            pubParamMap.put("timestamp", System.currentTimeMillis() + "");
            pubParamMap.put("version", "1.0.0");
            pubParamMap.put("merNo", payDto.getAgentNo());
            pubParamMap.put("signType","MD5");// 加解密方式
            String sign = RSA2Util.md5sign(pubParamMap, payDto.getSignKey());
            pubParamMap.put("sign", sign);
            String jsonParams = JSONUtil.toJsonStr(pubParamMap);

            log.info("平台请求数据:{}" + JSONUtil.toJsonPrettyStr(jsonParams));

            HttpResponse response = HttpRequest.post(payDto.getPostUrl()).body(jsonParams, "application/json").charset("utf-8").execute();

            log.info("渠道支付宝支付接口返回：{}",response);

            if(response.getStatus() == 200){// 请求成功
                String resultStr = response.body();

                log.info("平台返回结果：" + resultStr);

                // 解析返回结果
                if(StringUtils.isNotBlank(resultStr)){
                    JSONObject jsonObject = JSONObject.parseObject(resultStr);
                    String code = jsonObject.getString("code");
                    String message="";
                    if("00".equals(code)){
                        JSONObject dataJsonObject = JSONObject.parseObject(jsonObject.getString("data"));
                        if(dataJsonObject != null){
                            code = dataJsonObject.getString("bizCode");
                            message = dataJsonObject.getString("bizCodeMsg");
                        }
                    }else {
                        code = jsonObject.getString("code");
                        message = jsonObject.getString("message");
                    }
                    result.put("msg", message);
                    result.put("code",code);
                    result.put("data",jsonObject.get("data"));
                }else{
                    result.put("code","02");
                    result.put("msg","请求渠道支付宝支付接口返回失败");
                    log.error("请求渠道支付宝支付接口返回失败：{}",resultStr);
                }
            }else{
                log.error("请求渠道支付宝支付接口返回失败：{}",response);
                result.put("code","01");
                result.put("msg","请求渠道支付宝支付接口返回失败");
            }
        }catch (Exception e){
            log.error("请求渠道支付宝支付接口返回失败，异常详情{}",e.getMessage());
            result.put("code","9999");
            result.put("msg","请求渠道支付宝支付接口返回失败");
        }

        log.info("请求渠道支付宝支付接口结束：{}",JSONObject.toJSONString(result));
        return result;
    }

    /**
     * @Description 获取支付宝user_id
     * @Author  Kaven
     * @Date   2020/10/23 16:12
     * @Param   appId  privateKey authCode
     * @Return  String
     * @Exception  AlipayApiException
    */
    public static String getAccessTokenAndUserId(String appId,String privateKey,String authCode){
        log.info("支付宝userId请求数据:{}，{}，{}",appId,privateKey,authCode);

        String userId = null;
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",appId,privateKey,"json","GBK",null,"RSA2");
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code");
        request.setCode(authCode);
        AlipaySystemOauthTokenResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("获取支付宝userId失败：" + e.getErrMsg());
        }
        log.info("请求支付宝userId返回结果：{}",JSON.toJSONString(response));

        if(null != response && response.isSuccess()){
            userId = response.getUserId();
        }
        return userId;
    }

    /**
     * @Description 支付宝支付宝订单查询
     * @Author  Kaven
     * @Date   2020/10/27 14:37
     * @Param   AliPayDto
     * @Return  Map<String, Object>
     * @Exception
    */
    public static Map<String, Object> queryAliOrder(AliPayDto payDto) {
        log.info("请求渠道支付宝订单查询接口开始：{}",JSON.toJSONString(payDto));
        Map<String,Object> result = Maps.newHashMap();
        try{
            // 加密请求参数，发送HTTP请求交易接口
            Map<String,Object> orderMap = Maps.newHashMap();
            orderMap.put("orderNo", payDto.getTradeNo()); // 交易支付宝订单号
            orderMap.put("orderType", "0"); // 支付宝订单类型
            // orderMap.put("startTime", payDto.getAmount()); //开始时间
            // orderMap.put("endTime", payDto.getIpAddr()); //结束时间

            log.info("data数据明文：" + JSONUtil.toJsonPrettyStr(orderMap));

            String data = Base64.encode(DESUtils.des3EncodeECB(payDto.getKeyNum().getBytes(), JSONUtil.toJsonStr(orderMap).getBytes()));
            String enckeyNum  =  Base64.encode(RSA2Util.encryptByPublicKey(payDto.getKeyNum().getBytes(), payDto.getServicePubKey()));
            log.info("data加密数据：" + data);
            log.info("数据秘钥sm2加密:" + enckeyNum);

            Map<String, Object> pubParamMap = new HashMap<>();
            pubParamMap.put("data", data);
            pubParamMap.put("keyNum", enckeyNum);
            pubParamMap.put("timestamp", System.currentTimeMillis() + "");
            pubParamMap.put("version", "1.0.0");
            pubParamMap.put("signType", "MD5");
            pubParamMap.put("merNo", payDto.getAgentNo());

            // 加解密方式
            String sign = RSA2Util.md5sign(pubParamMap, payDto.getSignKey());
            pubParamMap.put("sign", sign);
            String jsonParams = JSONUtil.toJsonStr(pubParamMap);

            log.info("平台请求数据:{}" + JSONUtil.toJsonPrettyStr(jsonParams));

            HttpResponse response = HttpRequest.post(payDto.getPostUrl()).body(jsonParams, "application/json").charset("utf-8").execute();

            log.info("渠道支付宝订单查询接口返回：{}",response);

            if(response.getStatus() == 200){// 请求成功
                String resultStr = response.body();

                log.info("平台返回结果：" + resultStr);

                // 解析返回结果
                if(StringUtils.isNotBlank(resultStr)){
                    JSONObject jsonObject = JSONObject.parseObject(resultStr);
                    String code = jsonObject.getString("code");
                    String message = "";
                    if("00".equals(code)){
                        JSONObject dataJsonObject = JSONObject.parseObject(jsonObject.getString("data"));
                        if(dataJsonObject != null){
                            code = dataJsonObject.getString("bizCode");
                            message = dataJsonObject.getString("bizCodeMsg");
                        }
                    }else {
                        code = jsonObject.getString("code");
                        message = jsonObject.getString("message");
                    }
                    result.put("msg", message);
                    result.put("code",code);
                    result.put("data",jsonObject.get("data"));
                }else{
                    result.put("code","02");
                    result.put("msg","请求渠道支付宝订单查询接口返回失败");
                    log.error("请求渠道支付宝订单查询接口返回失败：{}",resultStr);
                }
            }else{
                log.error("请求渠道支付宝订单查询接口返回失败：{}",response);
                result.put("code","01");
                result.put("msg","请求渠道支付宝订单查询接口返回失败");
            }
        }catch (Exception e){
            log.error("请求渠道支付宝订单查询接口返回失败，异常详情{}",e.getMessage());
            result.put("code","9999");
            result.put("msg","请求渠道支付宝订单查询接口返回失败");
        }

        log.info("请求渠道支付宝订单查询接口结束：{}",JSONObject.toJSONString(result));
        return result;
    }

    /*public static void main(String[] args) {
        String userId = getAccessTokenAndUserId("2021001199602334","MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQDhQt7VrfulElLrUp0o+Wjt/N9B31575KILfybIh4ysptfyEuEkPAsq4gOUi08FvWoihNMlXVLMm1RIKeDP+oB8AWwqoaTp/ewRhnzcKEybHNajnEXgZU1BDQifSMMCXLUMDxZH6+9ITXW3BpCA4Ya6pcg8nzCGBW4IErAiBbQmU/NNtvgkBoFjLeSPdouULHM0dNBfH2S1EmYrBbhlVjYyuUaqCQqwOQHf58/X63vz6UGgAs46PqAW2Y2bunLBz/Qm4NE/lMjA/lEN2XXAQFYqvekv9mO2Z5qSRu+fGqiXk6I0+wr7vdoEUPOs/kcY718Gse70NJSlULuwGAJ+eK05AgMBAAECggEBANQ7HpuP6lGiLAGOuoeKRPRElUwI2Yo85PeT+sRgAYgBQd/PLdPnxlXGz7y6a99qkH6pSg4gGQyxI/1Bh5Ar4bXz2SdpWGklVa9FWIpMZbCWwRsw9xzuFJU/ZGo+MY0eL22HIWTtw2oJoOiqBKeI1FkyLLABA8ShhQOSJ+RGH28jUEiZZgiCqhi/JEtwTeChpjsXurDsl59kQKMglic2YywUVqw7q8U/YCY73EeR7XfcEBydL/ArTEsS0ArVmDrAFHtFNPiy898UXH25ymbtar3VGBfGbsbwUeTxunQHRkcGT0KhLpmPlQtANXuDirj3ht0gHm8JlR0LqFooZHtPrwkCgYEA++sGQWZjCwdrFqXkN1GJEP+D3cCLv7Y5eDkBQcEaLxg9DA5SLEg9vbcWjn1sKFQuulVIAaPBIYGjbD7SgvVR/4jObEI2U6hcKJSXA4EWr5cMnRZlQRvVCrl0y8X8Z7EmXoiXk7OYvELuRabdnVgeMKeDBVnRMVN1teYveTVd+lMCgYEA5OlFdkdkJm8XxL0ujc2bPL2E/IA22rg7oSOKanx68BU0jKnzSNbEQmc05gcDJi0Z+myMKZgPgcpTXQirMXIGTf3hGwqvsS4gG4vIZTEsvrfYtamDtUSDYk9mBc6dARmWE5m/B6BhOQIERZydkKat+U7xgOcuQqFD4t0I9JoxAMMCgYEAiCudQQK4LMEAyMg5GRHQtkh3ngkzQRid7fdaT5Gytpwye27OSjNudDsgTTtMmGwQqhXpC0HZ5mOt05gNsE4s2aS4d9o+hW/vCNxw2KsReS572fm+F7iUquxeTruhWOdv5w+MZ4Ff4PuI0kuFZUS5ZPFXKDBJnJERgpZ/ONR5DRMCgYEA04KDLz3Z2PVvRdbzzAy9sor+9PxXMxNl7YdUXv4z7d/6FTr3U0c2QpDwPNcf3sdvqIdhnxEkyu0yx9S5sc9E6g16UK04F4OiITGwcWfVi0d+UqSV0QjESTNJ9nWpdpm4pNW7wAU9sBof7l4+7KhGOgTqewuhH+m4O0e2/aP3QVcCgYEA3Ro4qS0Io0rw/dM12qo84XrKWVhW33W5Pm+V/BTcx6D/f7T9fsr/1HHr4QJTeX4TPHGMWgDBC2MEtt5GRkdAf3sUVPUQXLRaIIhd9ZYZgX6uF0dhTzydlAgq2Bf3jz6M+0Yh07WWGVEJ0hfeMpdgJihetOKS/AnTP3vMblWuCHI=","b3faf4b0f4e34aa6a0b283af73edOB64");
        System.out.println("userId:"+userId);
    }*/
}