package com.yuqian.itax.util.util;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yuqian.itax.util.entity.BytedancePayDto;
import com.yuqian.itax.util.entity.BytedanceRefundDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Encoder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Description 字节跳动相关工具类
 * @Author  蒋匿
 * @Date   2021/9/18
 */
@Slf4j
public class BytedanceUtils {

    //字节跳动获取AccessToken
    private final static String ACCESSTOKEN_URL = "https://developer.toutiao.com/api/apps/v2/token";
    //字节跳动获取二维码
    private final static String CREATEQRCODE_URL = "https://developer.toutiao.com/api/apps/qrcode";
    //字节跳动openId URL
    private final static String BYTEDANCE_OPENID_URL="https://developer.toutiao.com/api/apps/v2/jscode2session";
    //字节跳动支付预下单URL
    private final static String CREATE_ORDER_URL = "https://developer.toutiao.com/api/apps/ecpay/v1/create_order";
    //字节跳动支付订单查询
    private final static String QUERY_ORDER = "https://developer.toutiao.com/api/apps/ecpay/v1/query_order";
    //字节跳动支付退款
    private final static String CREATE_REFUND = "https://developer.toutiao.com/api/apps/ecpay/v1/create_refund";
    //字节跳动支付退款查询
    private final static String QUERY_REFUND = "https://developer.toutiao.com/api/apps/ecpay/v1/query_refund";

    public static String getBytedanceQRCODE(String appId,String appSecret,Long width,String path,Integer type){
        Map<String,Object> accessTokenData = getAccessToken(appId,appSecret);
        if(accessTokenData!=null && accessTokenData.containsKey("access_token")){
            Map<String,Object> params = Maps.newHashMap();
            params.put("access_token",accessTokenData.get("access_token").toString());
            params.put("appname","douyin");
            try {
                params.put("path", URLEncoder.encode(path, "utf-8"));
            }catch (Exception e){
                params.put("path", path);
            }
            params.put("width",width);
            params.put("set_icon",true);
            String jsonParams = JSONUtil.toJsonStr(params);
            log.info("平台请求数据:{}" + jsonParams);
            HttpRequest request = HttpRequest.post(CREATEQRCODE_URL);
            request.setConnectionTimeout(60*1000); //设置一分钟的超时时间
            request.setReadTimeout(60*1000);//设置一分钟的超时时间
            HttpResponse response = request.body(jsonParams, "application/json").charset("utf-8").execute();
            log.info("字节跳动接口返回：{}",response);
            if(response.getStatus() == 200) {// 请求成功
               byte[] qrcodeImgsDatas = response.bodyBytes();
               if(qrcodeImgsDatas!=null){
                   BASE64Encoder encoder = new BASE64Encoder();
                   if (type == 1) {
                       return "data:image/png;base64,"+encoder.encode(qrcodeImgsDatas);
                   }
                   return encoder.encode(qrcodeImgsDatas);
               }else{
                   String resultStr = response.body();
                   if (StringUtils.isNotBlank(resultStr)) {
                       try {
                           //如果返回结果可以解析成JSON对象，则说明二维码生成失败，生产成功直接返还二维码图片二进制
                           JSONObject jsonObject = JSONObject.parseObject(resultStr);
                           if (jsonObject != null) {
                               log.error("获取二维码失败，错误信息：{}", jsonObject.get("errmsg"));
                           }
                       }catch (Exception ex){
                           log.error("获取二维码失败，错误信息：{}",resultStr);
                       }
                   }
               }
            }
        }
        return null;
    }

    /**
     * 获取字节跳动accesstoken
     * @param appId
     * @param appSecret
     * @return
     */
    public static Map<String,Object> getAccessToken(String appId,String appSecret){
        Map<String,Object> params = Maps.newHashMap();
        params.put("appid",appId);
        params.put("secret",appSecret);
        params.put("grant_type","client_credential");
        String jsonParams = JSONUtil.toJsonStr(params);
        log.info("平台请求数据:{}" + jsonParams);

        HttpRequest request = HttpRequest.post(ACCESSTOKEN_URL);
        request.setConnectionTimeout(60*1000); //设置一分钟的超时时间
        request.setReadTimeout(60*1000);//设置一分钟的超时时间
        HttpResponse response = request.body(jsonParams, "application/json").charset("utf-8").execute();
        log.info("字节跳动接口返回：{}",response);
        if(response.getStatus() == 200) {// 请求成功
            String resultStr = response.body();
            log.info("平台返回结果：" + resultStr);
            // 解析返回结果
            if (StringUtils.isNotBlank(resultStr)) {
                JSONObject jsonObject = JSONObject.parseObject(resultStr);
                String err_no = jsonObject.getString("err_no");
                if ("0".equals(err_no) && jsonObject.containsKey("data")) {
                    return (Map)jsonObject.get("data");
                }
            }
        }
        return null;
    }
    /**
     * 获取字节跳动openId
     * @param appId 小程序 ID
     * @param appSecret 小程序的 APP Secret，可以在开发者后台获取
     * @param code login 接口返回的登录凭证
     * @param anonymousCode login 接口返回的匿名登录凭证
     * @return
     */
    public static String getBytedanceOpenId(String appId,String appSecret,String code,String anonymousCode){
        log.info("字节跳动获取openId请求数据:{}，{}，{},{}",appId,appSecret,code,anonymousCode);
        Map<String,Object> params = Maps.newHashMap();
        params.put("appid",appId);
        params.put("secret",appSecret);
        params.put("code",code);
        params.put("anonymous_code",anonymousCode);
        String jsonParams = JSONUtil.toJsonStr(params);
        log.info("平台请求数据:{}" + jsonParams);

        HttpRequest request = HttpRequest.post(BYTEDANCE_OPENID_URL);
        request.setConnectionTimeout(60*1000); //设置一分钟的超时时间
        request.setReadTimeout(60*1000);//设置一分钟的超时时间
        HttpResponse response = request.body(jsonParams, "application/json").charset("utf-8").execute();
        log.info("字节跳动接口返回：{}",response);
        String openId = null;
        if(response.getStatus() == 200) {// 请求成功
            String resultStr = response.body();
            log.info("平台返回结果：" + resultStr);
            // 解析返回结果
            if (StringUtils.isNotBlank(resultStr)) {
                JSONObject jsonObject = JSONObject.parseObject(resultStr);
                String err_no = jsonObject.getString("err_no");
                if ("0".equals(err_no)) {
                    JSONObject dataJsonObject = JSONObject.parseObject(jsonObject.getString("data"));
                    if (dataJsonObject != null) {
                        String openid = dataJsonObject.getString("openid");
                        String anonymous_openid = dataJsonObject.getString("anonymous_openid");
                        openId = StringUtils.isNotBlank(openid) ? openid : anonymous_openid;
                    }
                }
            }
        }
        return openId;
    }

    /**
     * 字节跳动微信支付下单接口
     * @param payDto
     * @return
     */
    public static Map<String,Object> bytedancePay(BytedancePayDto payDto) {
        log.info("请求字节跳动微信支付下单接口：");
        Map<String,Object> result = Maps.newHashMap();
        try{
            // 加密请求参数，发送HTTP请求交易接口
            Map<String,Object> orderMap = Maps.newHashMap();
            orderMap.put("app_id", payDto.getAppId()); //小程序APPID
            orderMap.put("out_order_no", payDto.getTradeNo()); //开发者侧的订单号, 同一小程序下不可重复
            orderMap.put("total_amount", payDto.getAmount()); //支付价格; 接口中参数支付金额单位为[分]
            orderMap.put("subject", payDto.getSubject()); //商品描述; 长度限制 128 字节，不超过 42 个汉字
            orderMap.put("body",payDto.getBody() ); //商品详情
            orderMap.put("valid_time", 24*60*60); //订单过期时间(秒); 最小 15 分钟，最大两天
            Map<String,Object> extraData = Maps.newHashMap();
            extraData.put("orderNo",payDto.getOrderNo());
            extraData.put("openId",payDto.getOpenId());
            extraData.put("userId",payDto.getUserId());
            extraData.put("orderType",payDto.getOrderType());
            extraData.put("oemCode",payDto.getOemCode());
            orderMap.put("cp_extra", JSONUtil.toJsonStr(extraData)); //开发者自定义字段，回调原样回传
            orderMap.put("notify_url", payDto.getNotifyUrl()); //商户自定义回调地址
            orderMap.put("disable_msg",1 ); //是否屏蔽担保支付的推送消息，1-屏蔽 0-非屏蔽，接入 POI 必传
            orderMap.put("msg_page", ""); //担保支付消息跳转页
            orderMap.put("sign", getSign(orderMap,payDto.getPaySalt())); //开发者对核心字段签名, 签名方式见文档附录, 防止传输过程中出现意外
            String jsonParams = JSONUtil.toJsonStr(orderMap);
            log.info("字节跳动微信支付接口请求数据：" + JSONUtil.toJsonPrettyStr(jsonParams));

            HttpRequest request = HttpRequest.post(CREATE_ORDER_URL);
            request.setConnectionTimeout(60*1000); //设置一分钟的超时时间
            request.setReadTimeout(60*1000);//设置一分钟的超时时间
            HttpResponse response = request.body(jsonParams, "application/json").charset("utf-8").execute();
            log.info("字节跳动微信支付接口返回：{}",response);

            if(response.getStatus() == 200){// 请求成功
                String resultStr = response.body();
                log.info("平台返回结果：" + resultStr);

                // 解析返回结果
                if(StringUtils.isNotBlank(resultStr)){
                    JSONObject jsonObject = JSONObject.parseObject(resultStr);
                    String code = jsonObject.getString("err_no");
                    String message=jsonObject.getString("err_tips");
                    Object data = null;
                    if("0".equals(code)){
                        JSONObject dataJsonObject = JSONObject.parseObject(jsonObject.getString("data"));
                        if(dataJsonObject != null){
                            dataJsonObject.put("tradeNo",dataJsonObject.getString("order_id"));
                            data = dataJsonObject;
                        }
                        code = "00";
                    }
                    result.put("msg", message);
                    result.put("code",code);
                    result.put("data",data);
                }else{
                    result.put("code","02");
                    result.put("msg","字节跳动微信支付接口返回失败");
                    log.error("请求字节跳动微信支付接口返回失败：{}",resultStr);
                }
            }else{
                log.error("请求字节跳动微信支付接口返回失败：{}",response);
                result.put("code","01");
                result.put("msg","请求字节跳动微信支付返回失败");
            }
        }catch (Exception e){
            log.error("请求字节跳动微信支付接口返回失败，异常详情{}",e.getMessage());
            result.put("code","9999");
            result.put("msg","请求字节跳动微信支付返回失败");
        }

        log.info("请求字节跳动微信支付接口结束：{}",JSONObject.toJSONString(result));
        return result;
    }

    /**
     * 字节跳动支付订单查询
     * @param appId 字节小程序appId
     * @param tradeNo 支付流水号
     * @param paySalt 支付秘钥
     * @return
     */
    public static Map<String,Object> queryBytedancePayOrder(String appId,String tradeNo,String paySalt) {
        log.info("请求字节跳动支付订单查询接口：");
        Map<String,Object> result = Maps.newHashMap();
        try{
            // 加密请求参数，发送HTTP请求交易接口
            Map<String,Object> orderMap = Maps.newHashMap();
            orderMap.put("app_id", appId); //小程序APPID
            orderMap.put("out_order_no", tradeNo); //开发者侧的订单号, 同一小程序下不可重复
            orderMap.put("sign", getSign(orderMap,paySalt)); //开发者对核心字段签名, 签名方式见文档附录, 防止传输过程中出现意外
            String jsonParams = JSONUtil.toJsonStr(orderMap);
            log.info("字节跳动支付订单查询请求数据：" + JSONUtil.toJsonPrettyStr(jsonParams));

            HttpRequest request = HttpRequest.post(QUERY_ORDER);
            request.setConnectionTimeout(60*1000); //设置一分钟的超时时间
            request.setReadTimeout(60*1000);//设置一分钟的超时时间
            HttpResponse response = request.body(jsonParams, "application/json").charset("utf-8").execute();
            log.info("字节跳动支付订单查询返回：{}",response);

            if(response.getStatus() == 200){// 请求成功
                String resultStr = response.body();
                log.info("平台返回结果：" + resultStr);

                // 解析返回结果
                if(StringUtils.isNotBlank(resultStr)){
                    JSONObject jsonObject = JSONObject.parseObject(resultStr);
                    String code = jsonObject.getString("err_no");
                    String message=jsonObject.getString("err_tips");
                    Object data = null;
                    if("0".equals(code)){
                        JSONObject dataJsonObject = JSONObject.parseObject(jsonObject.getString("payment_info"));
                        if(dataJsonObject != null){
                            dataJsonObject.put("out_order_no",jsonObject.getString("out_order_no"));
                            dataJsonObject.put("order_id",jsonObject.getString("order_id"));
                            data = dataJsonObject;
                        }
                    }
                    result.put("msg", message);
                    result.put("code",code);
                    result.put("data",data);
                }else{
                    result.put("code","02");
                    result.put("msg","字节跳动支付订单查询返回失败");
                    log.error("请求字节跳动支付订单查询返回失败：{}",resultStr);
                }
            }else{
                log.error("请求字节跳动支付订单查询返回失败：{}",response);
                result.put("code","01");
                result.put("msg","请求字节跳动支付订单查询失败");
            }
        }catch (Exception e){
            log.error("请求字节跳动支付订单查询返回失败，异常详情{}",e.getMessage());
            result.put("code","9999");
            result.put("msg","请求字节跳动支付订单查询返回失败");
        }

        log.info("请求字节跳动支付订单查询结束：{}",JSONObject.toJSONString(result));
        return result;
    }

    /**
     * 字节跳动退款下单接口
     * @param refundDto
     * @return
     */
    public static Map<String,Object> bytedanceRefund(BytedanceRefundDto refundDto) {
        log.info("请求字节跳动退款下单接口：");
        Map<String,Object> result = Maps.newHashMap();
        try{
            // 加密请求参数，发送HTTP请求交易接口
            Map<String,Object> orderMap = Maps.newHashMap();
            orderMap.put("app_id", refundDto.getAppId()); //小程序APPID
            orderMap.put("out_order_no", refundDto.getTradeNo()); //开发者侧的订单号, 同一小程序下不可重复
            orderMap.put("out_refund_no",refundDto.getRefundOrderNo()); //商户分配退款号
            orderMap.put("reason", refundDto.getReason()); //退款原因
            orderMap.put("refund_amount",refundDto.getRefundAmount()); //退款金额，单位[分]
            Map<String,Object> extraData = Maps.newHashMap();
            extraData.put("orderNo",refundDto.getOrderNo());
            extraData.put("userId",refundDto.getUserId());
            extraData.put("orderType",refundDto.getOrderType());
            extraData.put("oemCode",refundDto.getOemCode());
            orderMap.put("cp_extra", JSONUtil.toJsonStr(extraData)); //开发者自定义字段，回调原样回传
            if(StringUtil.isNotBlank(refundDto.getCallbackUrl())){
                orderMap.put("notify_url",refundDto.getCallbackUrl()); //商户自定义回调地址
            }
            orderMap.put("disable_msg",1 ); //是否屏蔽担保支付的推送消息，1-屏蔽 0-非屏蔽，接入 POI 必传
            orderMap.put("msg_page", ""); //担保支付消息跳转页
            orderMap.put("sign", getSign(orderMap,refundDto.getPaySalt())); //开发者对核心字段签名, 签名方式见文档附录, 防止传输过程中出现意外
            String jsonParams = JSONUtil.toJsonStr(orderMap);
            log.info("字节跳动退款下单接口请求数据：" + JSONUtil.toJsonPrettyStr(jsonParams));

            HttpRequest request = HttpRequest.post(CREATE_REFUND);
            request.setConnectionTimeout(60*1000); //设置一分钟的超时时间
            request.setReadTimeout(60*1000);//设置一分钟的超时时间
            HttpResponse response = request.body(jsonParams, "application/json").charset("utf-8").execute();
            log.info("字节跳动退款下单接口返回：{}",response);

            if(response.getStatus() == 200){// 请求成功
                String resultStr = response.body();
                log.info("平台返回结果：" + resultStr);

                // 解析返回结果
                if(StringUtils.isNotBlank(resultStr)){
                    JSONObject jsonObject = JSONObject.parseObject(resultStr);
                    String code = jsonObject.getString("err_no");
                    String message=jsonObject.getString("err_tips");
                    Object data = null;
                    if("0".equals(code)){
                        JSONObject dataJsonObject = new JSONObject();
                        dataJsonObject.put("refund_no",jsonObject.getString("refund_no"));
                        code = "00";
                        data = dataJsonObject;
                    }
                    result.put("msg", message);
                    result.put("code",code);
                    result.put("data",data);
                }else{
                    result.put("code","02");
                    result.put("msg","字节跳动退款下单返回失败");
                    log.error("请求字节跳动退款下单接口返回失败：{}",resultStr);
                }
            }else{
                log.error("请求字节跳动退款下单接口返回失败：{}",response);
                result.put("code","01");
                result.put("msg","请求字节跳动退款下单返回失败");
            }
        }catch (Exception e){
            log.error("请求字节跳动退款下单接口返回失败，异常详情{}",e.getMessage());
            result.put("code","9999");
            result.put("msg","请求字节跳动退款下单返回失败");
        }

        log.info("请求字节跳动退款下单接口结束：{}",JSONObject.toJSONString(result));
        return result;
    }

    /**
     * 字节跳动退款订单查询
     * @param appId 字节小程序appId
     * @param outRefundNo 开发者侧的退款号
     * @param paySalt 支付秘钥
     * @return
     */
    public static Map<String,Object> queryBytedanceRefundOrder(String appId,String outRefundNo,String paySalt) {
        log.info("请求字节跳动退款订单查询接口：");
        Map<String,Object> result = Maps.newHashMap();
        try{
            // 加密请求参数，发送HTTP请求交易接口
            Map<String,Object> orderMap = Maps.newHashMap();
            orderMap.put("app_id", appId); //小程序APPID
            orderMap.put("out_refund_no", outRefundNo); //开发者侧的订单号, 同一小程序下不可重复
            orderMap.put("sign", getSign(orderMap,paySalt)); //开发者对核心字段签名, 签名方式见文档附录, 防止传输过程中出现意外
            String jsonParams = JSONUtil.toJsonStr(orderMap);
            log.info("字节跳动退款订单查询请求数据：" + JSONUtil.toJsonPrettyStr(jsonParams));

            HttpRequest request = HttpRequest.post(QUERY_REFUND);
            request.setConnectionTimeout(60*1000); //设置一分钟的超时时间
            request.setReadTimeout(60*1000);//设置一分钟的超时时间
            HttpResponse response = request.body(jsonParams, "application/json").charset("utf-8").execute();
            log.info("字节跳动退款订单查询返回：{}",response);

            if(response.getStatus() == 200){// 请求成功
                String resultStr = response.body();
                log.info("平台返回结果：" + resultStr);

                // 解析返回结果
                if(StringUtils.isNotBlank(resultStr)){
                    JSONObject jsonObject = JSONObject.parseObject(resultStr);
                    String code = jsonObject.getString("err_no");
                    String message=jsonObject.getString("err_tips");
                    Object data = null;
                    if("0".equals(code)){
                        JSONObject dataJsonObject = JSONObject.parseObject(jsonObject.getString("refundInfo"));
                        if(dataJsonObject != null){
                            data = dataJsonObject;
                        }
                    }
                    result.put("msg", message);
                    result.put("code",code);
                    result.put("data",data);
                }else{
                    result.put("code","02");
                    result.put("msg","字节跳动退款订单查询返回失败");
                    log.error("请求字节跳动退款订单查询返回失败：{}",resultStr);
                }
            }else{
                log.error("请求字节跳动退款订单查询返回失败：{}",response);
                result.put("code","01");
                result.put("msg","请求字节跳动退款订单查询失败");
            }
        }catch (Exception e){
            log.error("请求字节跳动退款订单查询返回失败，异常详情{}",e.getMessage());
            result.put("code","9999");
            result.put("msg","请求字节跳动退款订单查询返回失败");
        }

        log.info("请求字节跳动退款订单查询结束：{}",JSONObject.toJSONString(result));
        return result;
    }

    /**
     *  获取签名
     * @param paramsMap 请求参数
     * @param paySalt 支付密钥值
     * @return
     */
    public static String getSign(Map<String, Object> paramsMap,String paySalt) {
        log.info("字节跳动签名数据:{}，支付秘钥：{}", JSONUtil.toJsonStr(paramsMap),paySalt);
        List<String> paramsArr = new ArrayList<>();
        for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            String value = "";
            if(entry.getValue() != null) {
                value = entry.getValue().toString();
            }
            value = value.trim();
            if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
                value = value.substring(1, value.length() - 1);
            }
            value = value.trim();
            if (value.equals("") || value.equals("null")) {
                continue;
            }
            switch (key) {
                case "app_id":
                case "thirdparty_id":
                case "sign":
                case "msg_signature":
                    break;
                default:
                    paramsArr.add(value);
                    break;
            }
        }
        if(StringUtil.isNotBlank(paySalt)) {
            paramsArr.add(paySalt);
        }
        Collections.sort(paramsArr);
        StringBuilder signStr = new StringBuilder();
        String sep = "";
        for (String s : paramsArr) {
            signStr.append(sep).append(s);
            sep = "&";
        }
        log.info("字节跳动签名前数据:{}", signStr);
        String sign =md5FromStr(signStr.toString());
        log.info("字节跳动签名后数据:{}", sign);
        return sign;
    }

    /**
     *  字节跳动异步回调签名
     * @param paramsMap 请求参数
     * @param paySalt 支付密钥值
     * @return
     */
    public static String getNotifySign(Map<String, Object> paramsMap,String paySalt) {
        log.info("字节跳动异步回调签名数据:{}，支付秘钥：{}", JSONUtil.toJsonStr(paramsMap),paySalt);
        List<String> paramsArr = new ArrayList<>();
        for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            String value = "";
            if(entry.getValue() != null) {
                value = entry.getValue().toString();
            }
            value = value.trim();
            if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
                value = value.substring(1, value.length() - 1);
            }
            value = value.trim();
            if (value.equals("") || value.equals("null")) {
                continue;
            }
            switch (key) {
                case "app_id":
                case "thirdparty_id":
                case "sign":
                case "msg_signature":
                case "type":
                    break;
                default:
                    paramsArr.add(value);
                    break;
            }
        }
        if(StringUtil.isNotBlank(paySalt)) {
            paramsArr.add(paySalt);
        }
        Collections.sort(paramsArr);
        StringBuilder signStr = new StringBuilder();
        for (String s : paramsArr) {
            signStr.append(s);
        }
        log.info("字节跳动签名前数据:{}", signStr);
        String sign = DigestUtil.sha1Hex(signStr.toString());
        log.info("字节跳动签名后数据:{}", sign);
        return sign;
    }

    public static String md5FromStr(String inStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }

        byte[] byteArray = inStr.getBytes(StandardCharsets.UTF_8);
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

        public static void main(String[] args){
        String appId = "tta7b2d10f388af56801";
        String AppSecret = "062428ed4328adce9c7b829cbc223712688dd7b4";
        String anonymous_code = "dirE8MgnHbwdmwqMm-t5JfOvOhZmupce1N6KeSivKcdiBD9uf2LTIxRRO4mZO64jS_lgPmCSPa9nJX2AiJ4GRovthkiUkSMB4UjeKWueM8TWIeJ9UHFsJsa9Zx4";
        String code ="j997_JCrqPuCYPte-nOLzQsRHkT61rCDlTp7XtGRelVKkUaIN5alVlqfp4Ftk-4MyhiXcHT92142hEJHHyn1Z-WkMriMSLBQ-El5xRnsBRq5FAKYHcq0nfbL8fc";
//        String openId = getBytedanceQRCODE(appId,AppSecret,460L,"pages/index?aa=123",2);
//        System.out.println("=======:  "+openId);
//            Map<String,Object> result =  queryBytedanceRefundOrder(appId,"211012110009665380000000","FOaAa75RSNzjFdn1C0Zh0phzGwuOWKf6LJG4DCc0");
//            System.out.println("=======:  ");
//        BytedancePayDto payDto = new BytedancePayDto();
//        payDto.setAppId(appId);
//        payDto.setAppSecret(AppSecret);
//        payDto.setPaySalt("FOaAa75RSNzjFdn1C0Zh0phzGwuOWKf6LJG4DCc0");
//        payDto.setAmount(10000L);
//        payDto.setTradeNo("a0ac38d11ceb7ee37276dbe8d19e00b8");
//        payDto.setOemCode("YCS");
//        payDto.setNotifyUrl("https://itaxdev.yuncaiol.cn/itax-api/order/registerorder/bytedancePayNotify");
//            payDto.setSubject("充值");
//            payDto.setBody("充值");
//        bytedancePay(payDto);
//
            String data="{\"msg\":\"{\\\"appid\\\":\\\"tta7b2d10f388af56801\\\",\\\"cp_orderno\\\":\\\"211012113603489830000000\\\",\\\"cp_extra\\\":\\\"{\\\\\\\"orderType\\\\\\\":5,\\\\\\\"orderNo\\\\\\\":\\\\\\\"D202110121135333623088\\\\\\\",\\\\\\\"openId\\\\\\\":\\\\\\\"ea88d339-58e0-4f2a-8a15-87d64d19e6a2\\\\\\\",\\\\\\\"oemCode\\\\\\\":\\\\\\\"YCS\\\\\\\",\\\\\\\"userId\\\\\\\":476}\\\",\\\"way\\\":\\\"1\\\",\\\"channel_no\\\":\\\"4302101277202110124337181057\\\",\\\"channel_gateway_no\\\":\\\"\\\",\\\"payment_order_no\\\":\\\"PC2021101211355936886819999639\\\",\\\"out_channel_order_no\\\":\\\"\\\",\\\"total_amount\\\":1,\\\"status\\\":\\\"SUCCESS\\\",\\\"seller_uid\\\":\\\"70060687693322672720\\\",\\\"extra\\\":\\\"null\\\",\\\"item_id\\\":\\\"\\\",\\\"paid_at\\\":1634009779,\\\"message\\\":\\\"\\\",\\\"order_id\\\":\\\"7018018457446877479\\\"}\",\"msg_signature\":\"79987058a11dbeb3e2e7b62d60a2e40e0096526b\",\"type\":\"payment\",\"nonce\":\"4798\",\"timestamp\":\"1634009779\"}";
            JSONObject jsonObject = JSONObject.parseObject(data);
            getNotifySign(jsonObject.toJavaObject(Map.class),"");
        }
}
