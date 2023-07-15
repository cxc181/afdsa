package com.yuqian.itax.util.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duob.encrypt.EncryptUtil;
import com.google.common.collect.Maps;
import com.yuqian.itax.util.entity.AccessPartyPayDto;
import com.yuqian.itax.util.entity.WechatPayDto;
import com.yuqian.itax.util.entity.WechatRefundDto;
import com.yuqian.itax.util.util.channel.DESUtils;
import com.yuqian.itax.util.util.channel.RSA2Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *  @Author: Kaven
 *  @Date: 2020/1/7 16:17
 *  @Description: 请求渠道端微信支付工具类
 */
@Slf4j
public class WechatPayUtils {
    /**
     * @Description 获取微信openId
     * @Author  Kaven
     * @Date   2020/1/7 16:18
     * @Param
     * @Return
     * @Exception
    */
    public static String getWxOpenId(String appId,String appSecret,String jsCode){
        log.info("微信openId请求数据:{}，{}，{}",appId,appSecret,jsCode);
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + appSecret + "&js_code=" + jsCode + "&grant_type=authorization_code";
        log.info("微信请求openId地址：{}",url);
        String result = HttpClientUtil.doGet(url,null);
        log.info("请求微信openId返回结果：{}",result);

        // 解析结果{"session_key":"jLbVoeA8JoV+35oaPZvmrw==","openid":"oPwgL4wVmO-6YaXJG2drwQiaDLEw"}
        String openId = null;
        JSONObject jsonObj = JSON.parseObject(result);
        if(null != jsonObj.getString("openid")){
            openId = jsonObj.getString("openid");
        }
        return openId;
    }

    /**
     * @Description 请求渠道端微信支付接口封装
     * @Author  Kaven
     * @Date   2020/1/7 16:46
     * @Param  WechatPayDto
     * @Return Map<String,String>
     * @Exception
    */
    public static Map<String,Object> wechatPay(WechatPayDto payDto) {
        log.info("请求渠道微信支付接口开始：");
        Map<String,Object> result = Maps.newHashMap();
        try{
            // 加密请求参数，发送HTTP请求交易接口
            Map<String,Object> orderMap = Maps.newHashMap();
            orderMap.put("orderNo", payDto.getTradeNo()); //微信支付订单流水号
            orderMap.put("merNo", payDto.getMerNo()); //商户号
            orderMap.put("amount", payDto.getAmount()); //金额：分为单位
            orderMap.put("deviceIp", payDto.getIpAddr()); //设备ip
            orderMap.put("notifyUrl", payDto.getCallbackUrl());// 回调地址
            orderMap.put("appId", payDto.getAppId());//微信appId
            orderMap.put("openId", payDto.getOpenId());//微信openId

            log.info("data数据明文：" + JSONUtil.toJsonPrettyStr(orderMap));

            String data = null;// 加密数据
            String enckeyNum = null;
            if("1".equals(payDto.getChannel())){ // 新加解密方式
                orderMap.put("productCode", payDto.getProductCode());//微信openId
                data = Base64.encode(DESUtils.des3EncodeECB(payDto.getKeyNum().getBytes(), JSONUtil.toJsonStr(orderMap).getBytes()));
                enckeyNum =  Base64.encode(RSA2Util.encryptByPublicKey(payDto.getKeyNum().getBytes(), payDto.getServicePubKey()));
            } else {
                // 旧的加解密方式
                data = EncryptUtil.encSm4Data(orderMap, payDto.getKeyNum());
                enckeyNum = EncryptUtil.encSm2SecretKey(payDto.getKeyNum(), payDto.getServicePubKey());
            }

            log.info("data加密数据：" + data);

            log.info("数据秘钥sm2加密:" + enckeyNum);

            Map<String, Object> pubParamMap = new HashMap<>();
            pubParamMap.put("data", data);
            pubParamMap.put("keyNum", enckeyNum);
            pubParamMap.put("timestamp", System.currentTimeMillis() + "");
            pubParamMap.put("version", "1.0.0");
            pubParamMap.put("signType", "SM3");

            // 加解密方式
            String sign = null;
            if("1".equals(payDto.getChannel())) { // 新加解密方式
                pubParamMap.put("merNo", payDto.getAgentNo());
                pubParamMap.put("signType","MD5");
                sign = RSA2Util.md5sign(pubParamMap, payDto.getSignKey());
            } else {
                // 旧的加解密方式
                pubParamMap.put("agentNo", payDto.getAgentNo());
                sign = EncryptUtil.sm3sign(pubParamMap, payDto.getSignKey());
            }
            pubParamMap.put("sign", sign);
            String jsonParams = JSONUtil.toJsonStr(pubParamMap);

            log.info("平台请求数据:{}" + JSONUtil.toJsonPrettyStr(jsonParams));

            HttpRequest request = HttpRequest.post(payDto.getPostUrl());
            request.setConnectionTimeout(60*1000); //设置一分钟的超时时间
            request.setReadTimeout(60*1000);//设置一分钟的超时时间
            HttpResponse response = request.body(jsonParams, "application/json").charset("utf-8").execute();

            log.info("渠道微信支付接口返回：{}",response);

            if(response.getStatus() == 200){// 请求成功
                String resultStr = response.body();

                log.info("平台返回结果：" + resultStr);

                // 解析返回结果
                if(StringUtils.isNotBlank(resultStr)){
                    JSONObject jsonObject = JSONObject.parseObject(resultStr);
                    String code = jsonObject.getString("code");
                    String message="";
                    if("1".equals(payDto.getChannel())) {
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
                    }else {
                        code = jsonObject.getString("code");
                        String msg = jsonObject.getString("msg");
                        int pos = msg.indexOf("[");// 错误信息截取
                        message = pos > 0 ? msg.substring(0, pos) : msg;
                    }
                    result.put("msg", message);
                    result.put("code",code);
                    result.put("data",jsonObject.get("data"));
                }else{
                    result.put("code","02");
                    result.put("msg","请求渠道微信支付接口返回失败");
                    log.error("请求渠道微信支付接口返回失败：{}",resultStr);
                }
            }else{
                log.error("请求渠道微信支付接口返回失败：{}",response);
                result.put("code","01");
                result.put("msg","请求渠道微信支付接口返回失败");
            }
        }catch (Exception e){
            log.error("请求渠道微信支付接口返回失败，异常详情{}",e.getMessage());
            result.put("code","9999");
            result.put("msg","请求渠道微信支付接口返回失败");
        }

        log.info("请求渠道微信支付接口结束：{}",JSONObject.toJSONString(result));
        return result;
    }

    /**
     * @Description 渠道微信支付订单查询接口，补单用
     * @Author  Kaven
     * @Date   2020/1/16 11:22
     * @Param  payDto
     * @Return Map<String,Object>
     * @Exception
    */
    public static Map<String,Object> queryWxOrder(WechatPayDto payDto) {
        log.info("请求渠道微信支付订单查询接口开始：{}",JSON.toJSONString(payDto));
        Map<String,Object> result = Maps.newHashMap();
        try{
            // 加密请求参数，发送HTTP请求交易接口
            Map<String,Object> orderMap = Maps.newHashMap();
            orderMap.put("tradeNo", payDto.getTradeNo()); // 交易微信支付订单号
            orderMap.put("orderType", "0"); // 微信支付订单类型
            // orderMap.put("startTime", payDto.getAmount()); //开始时间
            // orderMap.put("endTime", payDto.getIpAddr()); //结束时间

            log.info("data数据明文：" + JSONUtil.toJsonPrettyStr(orderMap));

            String data = null;// 加密数据
            String enckeyNum = null;
            if("1".equals(payDto.getChannel())){ // 新加解密方式
                data = Base64.encode(DESUtils.des3EncodeECB(payDto.getKeyNum().getBytes(), JSONUtil.toJsonStr(orderMap).getBytes()));
                enckeyNum =  Base64.encode(RSA2Util.encryptByPublicKey(payDto.getKeyNum().getBytes(), payDto.getServicePubKey()));
            } else {
                // 旧的加解密方式
                data = EncryptUtil.encSm4Data(orderMap, payDto.getKeyNum());
                enckeyNum = EncryptUtil.encSm2SecretKey(payDto.getKeyNum(), payDto.getServicePubKey());
            }
            log.info("data加密数据：" + data);
            log.info("数据秘钥sm2加密:" + enckeyNum);

            Map<String, Object> pubParamMap = new HashMap<>();
            pubParamMap.put("data", data);
            pubParamMap.put("keyNum", enckeyNum);
            pubParamMap.put("timestamp", System.currentTimeMillis() + "");
            pubParamMap.put("version", "1.0.0");
            pubParamMap.put("signType", "SM3");

            // 加解密方式
            String sign = null;
            if("1".equals(payDto.getChannel())) { // 新加解密方式
                pubParamMap.put("merNo", payDto.getAgentNo());
                pubParamMap.put("signType","MD5");
                sign = RSA2Util.md5sign(pubParamMap, payDto.getSignKey());
            } else {
                // 旧的加解密方式
                pubParamMap.put("agentNo", payDto.getAgentNo());
                sign = EncryptUtil.sm3sign(pubParamMap, payDto.getSignKey());
            }
            pubParamMap.put("sign", sign);
            String jsonParams = JSONUtil.toJsonStr(pubParamMap);

            log.info("平台请求数据:{}" + JSONUtil.toJsonPrettyStr(jsonParams));

            HttpRequest request = HttpRequest.post(payDto.getPostUrl());
            request.setConnectionTimeout(60*1000); //设置一分钟的超时时间
            request.setReadTimeout(60*1000);//设置一分钟的超时时间
            HttpResponse response = request.body(jsonParams, "application/json").charset("utf-8").execute();

            log.info("渠道微信支付订单查询接口返回：{}",response);

            if(response.getStatus() == 200){// 请求成功
                String resultStr = response.body();

                log.info("平台返回结果：" + resultStr);

                // 解析返回结果
                if(StringUtils.isNotBlank(resultStr)){
                    JSONObject jsonObject = JSONObject.parseObject(resultStr);
                    String code = jsonObject.getString("code");
                    String message="";
                    if("1".equals(payDto.getChannel())) {
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
                    }else {
                        code = jsonObject.getString("code");
                        String msg = jsonObject.getString("msg");
                        int pos = msg.indexOf("[");// 错误信息截取
                        message = pos > 0 ? msg.substring(0, pos) : msg;
                    }
                    result.put("msg", message);
                    result.put("code",code);
                    result.put("data",jsonObject.get("data"));
                }else{
                    result.put("code","02");
                    result.put("msg","请求渠道微信支付订单查询接口返回失败");
                    log.error("请求渠道微信支付订单查询接口返回失败：{}",resultStr);
                }
            }else{
                log.error("请求渠道微信支付订单查询接口返回失败：{}",response);
                result.put("code","01");
                result.put("msg","请求渠道微信支付订单查询接口返回失败");
            }
        }catch (Exception e){
            log.error("请求渠道微信支付订单查询接口返回失败，异常详情{}",e.getMessage());
            result.put("code","9999");
            result.put("msg","请求渠道微信支付订单查询接口返回失败");
        }

        log.info("请求渠道微信支付订单查询接口结束：{}",JSONObject.toJSONString(result));
        return result;
    }

    /**
     * @Description 请求渠道端微信支付退款接口封装
     * @Author  HZ
     * @Date   2021/8/11 16:46
     * @Param  WechatPayDto
     * @Return Map<String,String>
     * @Exception
     */
    public static Map<String,Object> wechatRefund(WechatRefundDto refundDto) {
        log.info("请求渠道微信退款接口开始：");
        Map<String,Object> result = Maps.newHashMap();
        try{
            // 加密请求参数，发送HTTP请求交易接口
            Map<String,Object> orderMap = Maps.newHashMap();
            orderMap.put("orderNo", refundDto.getTradeNo()); //支付订单号
            orderMap.put("refundOrderNo", refundDto.getRefundOrderNo()); //商户退款订单号
            //orderMap.put("refundAmt",null); //退款金额

            log.info("data数据明文：" + JSONUtil.toJsonPrettyStr(orderMap));

            String data = null;// 加密数据
            String enckeyNum = null;
            if("1".equals(refundDto.getChannel())){ // 新加解密方式
                orderMap.put("productCode", refundDto.getProductCode());//微信openId
                data = Base64.encode(DESUtils.des3EncodeECB(refundDto.getKeyNum().getBytes(), JSONUtil.toJsonStr(orderMap).getBytes()));
                enckeyNum =  Base64.encode(RSA2Util.encryptByPublicKey(refundDto.getKeyNum().getBytes(), refundDto.getServicePubKey()));
            } else {
                // 旧的加解密方式
                data = EncryptUtil.encSm4Data(orderMap, refundDto.getKeyNum());
                enckeyNum = EncryptUtil.encSm2SecretKey(refundDto.getKeyNum(), refundDto.getServicePubKey());
            }

            log.info("data加密数据：" + data);

            log.info("数据秘钥sm2加密:" + enckeyNum);

            Map<String, Object> pubParamMap = new HashMap<>();
            pubParamMap.put("data", data);
            pubParamMap.put("keyNum", enckeyNum);
            pubParamMap.put("timestamp", System.currentTimeMillis() + "");
            pubParamMap.put("version", "1.0.0");
            pubParamMap.put("signType", "SM3");

            // 加解密方式
            String sign = null;
            if("1".equals(refundDto.getChannel())) { // 新加解密方式
                pubParamMap.put("merNo", refundDto.getAgentNo());
                pubParamMap.put("signType","MD5");
                sign = RSA2Util.md5sign(pubParamMap, refundDto.getSignKey());
            } else {
                // 旧的加解密方式
                pubParamMap.put("agentNo", refundDto.getAgentNo());
                sign = EncryptUtil.sm3sign(pubParamMap, refundDto.getSignKey());
            }
            pubParamMap.put("sign", sign);
            String jsonParams = JSONUtil.toJsonStr(pubParamMap);

            log.info("平台请求数据:{}" + JSONUtil.toJsonPrettyStr(jsonParams));

            HttpResponse response = HttpRequest.post(refundDto.getPostUrl()).body(jsonParams, "application/json").charset("utf-8").execute();

            log.info("渠道微信退款接口返回：{}",response);

            if(response.getStatus() == 200){// 请求成功
                String resultStr = response.body();

                log.info("平台返回结果：" + resultStr);

                // 解析返回结果
                if(StringUtils.isNotBlank(resultStr)){
                    JSONObject jsonObject = JSONObject.parseObject(resultStr);
                    String code = jsonObject.getString("code");
                    String message="";
                    if("1".equals(refundDto.getChannel())) {
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
                    }else {
                        code = jsonObject.getString("code");
                        String msg = jsonObject.getString("msg");
                        int pos = msg.indexOf("[");// 错误信息截取
                        message = pos > 0 ? msg.substring(0, pos) : msg;
                    }
                    result.put("msg", message);
                    result.put("code",code);
                    result.put("data",jsonObject.get("data"));
                }else{
                    result.put("code","02");
                    result.put("msg","请求渠道微信退款接口返回失败");
                    log.error("请求渠道微信退款接口返回失败：{}",resultStr);
                }
            }else{
                log.error("请求渠道微信退款接口返回失败：{}",response);
                result.put("code","01");
                result.put("msg","请求渠道微信退款接口返回失败");
            }
        }catch (Exception e){
            log.error("请求渠道微信退款接口返回失败，异常详情{}",e.getMessage());
            result.put("code","9999");
            result.put("msg","请求渠道微信退款接口返回失败");
        }
        log.info("请求渠道微信退款接口结束：{}",JSONObject.toJSONString(result));
        return result;
    }

    /**
     * @Description 渠道微信退款订单查询接口，
     * @Author  Kaven
     * @Date   2020/1/16 11:22
     * @Param  payDto
     * @Return Map<String,Object>
     * @Exception
     */
    public static Map<String,Object> queryWxRefundOrder(WechatRefundDto wechatRefundDto) {
        log.info("请求渠道微信退款订单查询接口开始：{}",JSON.toJSONString(wechatRefundDto));
        Map<String,Object> result = Maps.newHashMap();
        try{
            // 加密请求参数，发送HTTP请求交易接口
            Map<String,Object> orderMap = Maps.newHashMap();
            orderMap.put("refundOrderNo", wechatRefundDto.getRefundOrderNo()); // 交易微信支付订单号
            //orderMap.put("refundOrderNo", "210818151342628050000002"); // 交易微信支付订单号

            log.info("data数据明文：" + JSONUtil.toJsonPrettyStr(orderMap));

            String data = null;// 加密数据
            String enckeyNum = null;
            if("1".equals(wechatRefundDto.getChannel())){ // 新加解密方式
                data = Base64.encode(DESUtils.des3EncodeECB(wechatRefundDto.getKeyNum().getBytes(), JSONUtil.toJsonStr(orderMap).getBytes()));
                enckeyNum =  Base64.encode(RSA2Util.encryptByPublicKey(wechatRefundDto.getKeyNum().getBytes(), wechatRefundDto.getServicePubKey()));
            } else {
                // 旧的加解密方式
                data = EncryptUtil.encSm4Data(orderMap, wechatRefundDto.getKeyNum());
                enckeyNum = EncryptUtil.encSm2SecretKey(wechatRefundDto.getKeyNum(), wechatRefundDto.getServicePubKey());
            }
            log.info("data加密数据：" + data);
            log.info("数据秘钥sm2加密:" + enckeyNum);

            Map<String, Object> pubParamMap = new HashMap<>();
            pubParamMap.put("data", data);
            pubParamMap.put("keyNum", enckeyNum);
            pubParamMap.put("timestamp", System.currentTimeMillis() + "");
            pubParamMap.put("version", "1.0.0");
            pubParamMap.put("signType", "SM3");

            // 加解密方式
            String sign = null;
            if("1".equals(wechatRefundDto.getChannel())) { // 新加解密方式
                pubParamMap.put("merNo", wechatRefundDto.getAgentNo());
                pubParamMap.put("signType","MD5");
                sign = RSA2Util.md5sign(pubParamMap, wechatRefundDto.getSignKey());
            } else {
                // 旧的加解密方式
                pubParamMap.put("agentNo", wechatRefundDto.getAgentNo());
                sign = EncryptUtil.sm3sign(pubParamMap, wechatRefundDto.getSignKey());
            }
            pubParamMap.put("sign", sign);
            String jsonParams = JSONUtil.toJsonStr(pubParamMap);

            log.info("平台请求数据:{}" + JSONUtil.toJsonPrettyStr(jsonParams));

            HttpResponse response = HttpRequest.post(wechatRefundDto.getPostUrl()).body(jsonParams, "application/json").charset("utf-8").execute();

            log.info("渠道微信退款订单查询接口返回：{}",response);

            if(response.getStatus() == 200){// 请求成功
                String resultStr = response.body();

                log.info("平台返回结果：" + resultStr);

                // 解析返回结果
                if(StringUtils.isNotBlank(resultStr)){
                    JSONObject jsonObject = JSONObject.parseObject(resultStr);
                    String code = jsonObject.getString("code");
                    String message="";
                    if("1".equals(wechatRefundDto.getChannel())) {
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
                    }else {
                        code = jsonObject.getString("code");
                        String msg = jsonObject.getString("msg");
                        int pos = msg.indexOf("[");// 错误信息截取
                        message = pos > 0 ? msg.substring(0, pos) : msg;
                    }
                    result.put("msg", message);
                    result.put("code",code);
                    result.put("data",jsonObject.get("data"));
                }else{
                    result.put("code","02");
                    result.put("msg","请求渠道微信退款订单查询接口返回失败");
                    log.error("请求渠道微信退款订单查询接口返回失败：{}",resultStr);
                }
            }else{
                log.error("请求渠道微信退款订单查询接口返回失败：{}",response);
                result.put("code","01");
                result.put("msg","请求渠道微信退款订单查询接口返回失败");
            }
        }catch (Exception e){
            log.error("请求渠道微信退款订单查询接口返回失败，异常详情{}",e.getMessage());
            result.put("code","9999");
            result.put("msg","请求渠道微信退款订单查询接口返回失败");
        }

        log.info("请求渠道微信退款订单查询接口结束：{}",JSONObject.toJSONString(result));
        return result;
    }

    /*public static void main(String[] args) {
        getWxOpenId("wxb884fccbb878f5b8","b755b965deb2e144f7616162986e0270","043vdVVX0MbIkX1IRIUX0iHCVX0vdVVb");
    }*/

    public static void main(String[] args) {
        String keyNum = "bb9aa8f2499c329b88f37567dd9aab31";
        String servicePubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmj62LA3IWA/SO12Q6Kmsb3B8N0Sr9WZnfcAKutzv2NSdHh/XthTB8LNBTxbfSUlLDgNf9Nui+sTRyeA2SrkBSRtye99O1KZ8FNPqFr4JQWgCz7yrg2/bg3sSr75/YqVXvKrld+ZZhOmR3egFoBmBUIldCu3Bbd2dvUpteSqiIfsTBi2h/Sb0cM/HIBm2jn8q6VMCBwssukLw9lnaPLbiGcaFf9phrjyCsdLFKKPVeiJYEDUeLcmyZjslCHE+5fJmshPMnF8CmoQDR2v94p8C3wptagwH6B+Nl5M1GBq71M56/izthboyWmW+vfntG84ebiLgsTeQR5nZJu9S0popfwIDAQAB";
        String agentNo = "M00000";
        String merNo = "M00000";
        String signKey = "d9729feb74992cc3482b350163a1a010";
        String postUrl = "https://tst.api.mch.duobeikeji.cn/gateway/payment/v1/createPayOrder";
        String callbackUrl = "https://itaxtest.yuncaiol.cn/itax-api/order/registerorder/wechatNotify";
        String tradeNo = "D202009111034498046117";
        String amount = "80000";
        String userIp = "172.16.100.171";
        String productName = null;
        String appSecret = "d9729feb74992cc3482b350163a1a010";
        String channel = "1";
        String productCode = "PRD00001";
        String appId = "wx3151bbc9cff9f453";
        String openId = "oXkh55D-4ZGGozr9MuXndudZeW8k";

        Map<String,Object> result = Maps.newHashMap();
        try{
            // 加密请求参数，发送HTTP请求交易接口
            Map<String,Object> orderMap = Maps.newHashMap();
            orderMap.put("orderNo", tradeNo); //微信支付订单流水号
            orderMap.put("merNo", merNo); //商户号
            orderMap.put("amount", amount); //金额：分为单位
            orderMap.put("userIp", userIp); //设备ip
            orderMap.put("notifyUrl", callbackUrl);// 回调地址
            orderMap.put("appId", appId);//微信appId
            orderMap.put("openId", openId);// openId

            log.info("data数据明文：" + JSONUtil.toJsonPrettyStr(orderMap));

            String data = null;// 加密数据
            String enckeyNum = null;
            if("1".equals(channel)){ // 新加解密方式
                orderMap.put("productCode", productCode);//微信openId
                data = Base64.encode(DESUtils.des3EncodeECB(keyNum.getBytes(), JSONUtil.toJsonStr(orderMap).getBytes()));
                enckeyNum =  Base64.encode(RSA2Util.encryptByPublicKey(keyNum.getBytes(), servicePubKey));
            } else {
                // 旧的加解密方式
                data = EncryptUtil.encSm4Data(orderMap, keyNum);
                enckeyNum = EncryptUtil.encSm2SecretKey(keyNum, servicePubKey);
            }

            log.info("data加密数据：" + data);

            log.info("数据秘钥sm2加密:" + enckeyNum);

            Map<String, Object> pubParamMap = new HashMap<>();
            pubParamMap.put("data", data);
            pubParamMap.put("keyNum", enckeyNum);
            pubParamMap.put("timestamp", System.currentTimeMillis() + "");
            pubParamMap.put("version", "1.0.0");
            pubParamMap.put("signType", "SM3");

            // 加解密方式
            String sign = null;
            if("1".equals(channel)) { // 新加解密方式
                pubParamMap.put("merNo", agentNo);
                pubParamMap.put("signType","MD5");
                sign = RSA2Util.md5sign(pubParamMap, signKey);
            } else {
                // 旧的加解密方式
                pubParamMap.put("agentNo", agentNo);
                sign = EncryptUtil.sm3sign(pubParamMap, signKey);
            }
            pubParamMap.put("sign", sign);
            String jsonParams = JSONUtil.toJsonStr(pubParamMap);

            log.info("平台请求数据:{}" + JSONUtil.toJsonPrettyStr(jsonParams));

            HttpResponse response = HttpRequest.post(postUrl).body(jsonParams, "application/json").charset("utf-8").execute();

            log.info("渠道微信H5支付接口返回：{}",response);

            if(response.getStatus() == 200){// 请求成功
                String resultStr = response.body();

                log.info("平台返回结果：" + resultStr);

                // 解析返回结果
                if(StringUtils.isNotBlank(resultStr)){
                    JSONObject jsonObject = JSONObject.parseObject(resultStr);
                    String code = jsonObject.getString("code");
                    String message="";
                    if("1".equals(channel)) {
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
                    }else {
                        code = jsonObject.getString("code");
                        String msg = jsonObject.getString("msg");
                        int pos = msg.indexOf("[");// 错误信息截取
                        message = pos > 0 ? msg.substring(0, pos) : msg;
                    }
                    result.put("msg", message);
                    result.put("code",code);
                    result.put("data",jsonObject.get("data"));
                }else{
                    result.put("code","02");
                    result.put("msg","请求渠道微信H5支付接口返回失败");
                    log.error("请求渠道微信H5支付接口返回失败：{}",resultStr);
                }
            }else{
                log.error("请求渠道微信H5支付接口返回失败：{}",response);
                result.put("code","01");
                result.put("msg","请求渠道微信H5支付接口返回失败");
            }
        }catch (Exception e){
            log.error("请求渠道微信H5支付接口返回失败，异常详情{}",e.getMessage());
            result.put("code","9999");
            result.put("msg","请求渠道微信H5支付接口返回失败");
        }
        log.info("请求渠道微信H5支付接口结束：{}",JSONObject.toJSONString(result));
    }
    /**
     * @Description 请求渠道端微信H5支付接口封装
     * @Author  lmh
     * @Date   2021/8/13
     * @Param  WeChatPayDto
     * @Return Map<String,String>
     * @Exception
     */
    public static Map<String,Object> accessPartyPayH5(AccessPartyPayDto payDto, Integer sourceType) {
        log.info("请求渠道微信H5支付接口开始：");
        Map<String,Object> result = Maps.newHashMap();
        try{
            // 加密请求参数，发送HTTP请求交易接口
            Map<String,Object> orderMap = Maps.newHashMap();
            orderMap.put("orderNo", payDto.getTradeNo()); //微信支付订单流水号
            orderMap.put("merNo", payDto.getMerNo()); //商户号
            orderMap.put("amount", payDto.getAmount()); //金额：分为单位
            orderMap.put("deviceIp", payDto.getUserIp()); //设备ip
            orderMap.put("notifyUrl", payDto.getCallbackUrl());// 回调地址
            if (1 == sourceType || 4 == sourceType) {
                orderMap.put("appId", payDto.getAppId());//微信appId或公众号appId
                orderMap.put("openId", payDto.getOpenId());//微信openId
            } else if (2 == sourceType) {
                orderMap.put("appId", payDto.getAppId());//支付宝appId
                orderMap.put("buyerId", payDto.getBuyerId());//微信openId
            }

            log.info("data数据明文：" + JSONUtil.toJsonPrettyStr(orderMap));

            String data = null;// 加密数据
            String enckeyNum = null;
            if("1".equals(payDto.getChannel())){ // 新加解密方式
                orderMap.put("productCode", payDto.getProductCode());//微信openId
                data = Base64.encode(DESUtils.des3EncodeECB(payDto.getKeyNum().getBytes(), JSONUtil.toJsonStr(orderMap).getBytes()));
                enckeyNum =  Base64.encode(RSA2Util.encryptByPublicKey(payDto.getKeyNum().getBytes(), payDto.getServicePubKey()));
            } else {
                // 旧的加解密方式
                data = EncryptUtil.encSm4Data(orderMap, payDto.getKeyNum());
                enckeyNum = EncryptUtil.encSm2SecretKey(payDto.getKeyNum(), payDto.getServicePubKey());
            }

            log.info("data加密数据：" + data);

            log.info("数据秘钥sm2加密:" + enckeyNum);

            Map<String, Object> pubParamMap = new HashMap<>();
            pubParamMap.put("data", data);
            pubParamMap.put("keyNum", enckeyNum);
            pubParamMap.put("timestamp", System.currentTimeMillis() + "");
            pubParamMap.put("version", "1.0.0");
            pubParamMap.put("signType", "SM3");

            // 加解密方式
            String sign = null;
            if("1".equals(payDto.getChannel())) { // 新加解密方式
                pubParamMap.put("merNo", payDto.getAgentNo());
                pubParamMap.put("signType","MD5");
                sign = RSA2Util.md5sign(pubParamMap, payDto.getSignKey());
            } else {
                // 旧的加解密方式
                pubParamMap.put("agentNo", payDto.getAgentNo());
                sign = EncryptUtil.sm3sign(pubParamMap, payDto.getSignKey());
            }
            pubParamMap.put("sign", sign);
            String jsonParams = JSONUtil.toJsonStr(pubParamMap);

            log.info("平台请求数据:{}" + JSONUtil.toJsonPrettyStr(jsonParams));

            HttpResponse response = HttpRequest.post(payDto.getPostUrl()).body(jsonParams, "application/json").charset("utf-8").execute();

            log.info("渠道微信H5支付接口返回：{}",response);

            if(response.getStatus() == 200){// 请求成功
                String resultStr = response.body();

                log.info("平台返回结果：" + resultStr);

                // 解析返回结果
                if(StringUtils.isNotBlank(resultStr)){
                    JSONObject jsonObject = JSONObject.parseObject(resultStr);
                    String code = jsonObject.getString("code");
                    String message="";
                    if("1".equals(payDto.getChannel())) {
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
                    }else {
                        code = jsonObject.getString("code");
                        String msg = jsonObject.getString("msg");
                        int pos = msg.indexOf("[");// 错误信息截取
                        message = pos > 0 ? msg.substring(0, pos) : msg;
                    }
                    result.put("msg", message);
                    result.put("code",code);
                    result.put("data",jsonObject.get("data"));
                }else{
                    result.put("code","02");
                    result.put("msg","请求渠道微信H5支付接口返回失败");
                    log.error("请求渠道微信H5支付接口返回失败：{}",resultStr);
                }
            }else{
                log.error("请求渠道微信H5支付接口返回失败：{}",response);
                result.put("code","01");
                result.put("msg","请求渠道微信H5支付接口返回失败");
            }
        }catch (Exception e){
            log.error("请求渠道微信H5支付接口返回失败，异常详情{}",e.getMessage());
            result.put("code","9999");
            result.put("msg","请求渠道微信H5支付接口返回失败");
        }

        log.info("请求渠道微信H5支付接口结束：{}",JSONObject.toJSONString(result));
        return result;
    }
}