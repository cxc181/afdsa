package com.yuqian.itax.wechat.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayOpenAppQrcodeCreateRequest;
import com.alipay.api.response.AlipayOpenAppQrcodeCreateResponse;
import com.google.common.collect.Maps;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.enums.WeChatConstants;
import com.yuqian.itax.message.entity.WechatMessageTemplateEntity;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.util.util.BytedanceUtils;
import com.yuqian.itax.util.util.FileUtil;
import com.yuqian.itax.util.util.HttpClientUtil;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.wechat.WeChatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author：pengwei
 * @Date：2020/6/8 16:20
 * @version：1.0
 */
@Service("weChatService")
@Slf4j
public class WeChatServiceImpl implements WeChatService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private OemParamsService oemParamsService;

    @Override
    public String getAccessToken(String appId, String appSecret) {
        DictionaryEntity entity = dictionaryService.getByCode("wechat_request_url_prefix");
        if (entity == null) {
            throw new BusinessException("字典表未配置微信接口url");
        }
        return getAccessToken(entity.getDictValue(), appId, appSecret);
    }

    @Override
    public String getAccessToken(String url, String appId, String appSecret) {
        String weChatAccessToken = RedisKey.WECHAT_ACCESS_TOKEN_SUFFER + appId;
        //如果redis存在token，返回token
        String token = redisService.get(weChatAccessToken);
        if(StringUtils.isNotBlank(token)) {
            return token;
        }
        Map<String, String> reqMap = Maps.newHashMap();
        reqMap.put("grant_type", "client_credential");
        reqMap.put("appid", appId);
        reqMap.put("secret", appSecret);
        String result = HttpClientUtil.doGet(url + "cgi-bin/token", reqMap);
        log.info("小程序获取token返回结果: {}", result);
        if(StringUtils.isBlank(result)) {
            log.error("小程序获取token失败，网络连接超时");
            throw new RuntimeException("小程序获取token失败，网络连接超时");
        }
        JSONObject accessToken = JSON.parseObject(result);
        token = accessToken.getString("access_token");
        if(StringUtils.isBlank(token)) {
            throw new RuntimeException("小程序获取token失败，返回信息："+accessToken.getString("errmsg"));
        }
        Integer timeout = accessToken.getInteger("expires_in");
        //如果token不存在，向微信获取，放入redis设置系统返回超时时间
        redisService.set(weChatAccessToken, token, timeout);
        return token;
    }

    @Override
    public String messageSubscribeSend(WechatMessageTemplateEntity templateEntity, String openid, String page, String appId, String appSecret) {
        DictionaryEntity entity = dictionaryService.getByCode("wechat_request_url_prefix");
        if (entity == null) {
            throw new BusinessException("字典表未配置微信接口url");
        }
        return messageSubscribeSend(templateEntity, openid, page, entity.getDictValue(), appId, appSecret);
    }

    @Override
    public String messageSubscribeSend(WechatMessageTemplateEntity templateEntity, String openid, String page, String url, String appId, String appSecret) {
        //获取微信accessToken
        String accessToken = getAccessToken(url, appId, appSecret);
        url = url + "cgi-bin/message/subscribe/send?access_token=" + accessToken;
        JSONObject params = new JSONObject();
        //小程序背景透明
        params.put("touser", openid);
        params.put("template_id", templateEntity.getWechatTemplateId());
        params.put("page", page);
        params.put("miniprogram_state", Optional.ofNullable(dictionaryService.getByCode("notice_miniprogram_state")).map(DictionaryEntity::getDictValue).orElse(null));
        params.put("data", JSONObject.parseObject(templateEntity.getTemplateParams()));

        log.info("微信消息通知，请求url：{}", url);
        log.info("微信消息通知，请求参数：{}", params);
        String result = HttpClientUtil.doPostJson(url, params);
        log.info("微信消息通知，请求参数：{}，返回参数：{}", params, result);
        return result;
    }

    @Override
    public String getQRCode(String oemCode, String scene, Long width, String page, Integer type, String sourceType) throws BusinessException {
        String base64QRCode = "";
        // 操作小程序来源 1-微信小程序 2-支付宝小程序
        if (Objects.equals("1", sourceType)) {
            base64QRCode = createWechatQRCode(oemCode, scene, width, page, type);
            if ("9999".equals(base64QRCode)) {
                throw new BusinessException("获取微信小程序二维码图片失败");
            }
        } else if (Objects.equals("2", sourceType)) {
            base64QRCode = createAliQRCode(oemCode, scene, width, page, type);
        }else if (Objects.equals("4", sourceType)) {
            base64QRCode = createBytedanceQRCode(oemCode, width, page, type);
            if(StringUtil.isBlank(base64QRCode)){
                log.error("获取字节跳动小程序二维码图片失败");
                throw new BusinessException("获取字节跳动二维码图片失败！");
            }
        }
        return base64QRCode;
    }

    @Override
    public String getJsApiTicket(String oemCode) throws BusinessException {
        // 读取微信小程序二维码相关配置 paramsType=8
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 8);
        if (null == paramsEntity) {
            throw new BusinessException("未配置微信小程序二维码相关信息！");
        }
        // 此处需要公众号appId及其秘钥
        String appId = "wx3151bbc9cff9f453";
        String appSecret = "9a542768d03332d48f43f4fc1a07ed3b";
        String accessToken = getAccessToken(appId, appSecret);
        String url = WeChatConstants.API_REQ_URL + "cgi-bin/ticket/getticket";
        Map<String, String> map = Maps.newHashMap();
        map.put("access_token", accessToken);
        map.put("type", "jsapi");
        String jsApiTicket;
        try {
            jsApiTicket = HttpClientUtil.doGet(url, map);
            JSONObject jsonObject = JSONObject.parseObject(jsApiTicket);
            jsApiTicket = jsonObject.getString("ticket");
        } catch (Exception e) {
            throw new BusinessException("获取jsApiTicket失败");
        }
        return jsApiTicket;
    }

    /**
     * 生成微信小程序二维码
     */
    public String createWechatQRCode(String oemCode, String scene, Long width, String page, Integer type) throws BusinessException {
        // 读取微信小程序二维码相关配置 paramsType=8
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 8);
        if (null == paramsEntity) {
            throw new BusinessException("未配置微信小程序二维码相关信息！");
        }
        String appId = paramsEntity.getAccount();
        String appSecret = paramsEntity.getSecKey();

        // 请求获取二维码
        String url = WeChatConstants.API_REQ_URL + "wxa/getwxacodeunlimit?access_token=" + getAccessToken(appId, appSecret);
        JSONObject param = new JSONObject();
        //小程序背景透明
        param.put("scene", scene);
        param.put("page", page);//二维码中跳向的地址
        param.put("width", width.toString());//图片大小
        param.put("is_hyaline", true);
        log.info("url:" + url + "， param:" + param);
        String base64QRCode = HttpClientUtil.doPostPictrue(url, param, type);
        return base64QRCode;
    }

    /**
     * 生成支付宝小程序二维码
     */
    public String createAliQRCode(String oemCode, String scene, Long width, String page, Integer type) throws BusinessException {
        // 读取支付宝小程序二维码相关配置 paramsType=22
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 22);
        if (null == paramsEntity) {
            throw new BusinessException("未配置支付宝小程序二维码相关信息！");
        }
        AlipayClient alipayClient = new DefaultAlipayClient(
                paramsEntity.getUrl(),
                paramsEntity.getAccount(),
                paramsEntity.getPrivateKey(),
                "json",
                "utf-8",
                paramsEntity.getPublicKey(),
                "RSA2");

        //设置请求参数
        JSONObject param = new JSONObject();
        param.put("url_param", page);// 小程序中能访问到的页面路径
        param.put("query_param", scene);// 小程序的启动参数
        param.put("describe", oemCode);// 对应的二维码描述

        //参数转化位json格式
        AlipayOpenAppQrcodeCreateRequest request = new AlipayOpenAppQrcodeCreateRequest();
        request.setBizContent(JSON.toJSONString(param));
        AlipayOpenAppQrcodeCreateResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error(e.getMessage());
        }

        //处理响应参数
        String base64Image = "data:image/png;base64,";
        if (response!=null&&response.isSuccess()) {
            log.info("获取支付宝小程序二维码图片成功，url:" + response.getQrCodeUrl() + "， param:" + param);
            String base64String = FileUtil.imgUrl2pngBase64(response.getQrCodeUrl());
            // 1-获取带data url的base64，2-获取不带data url的base64
            if (type == 1) {
                return base64Image + base64String;
            }
            return base64String;
        } else {
            log.info("获取支付宝小程序二维码图片失败");
            throw new BusinessException("获取支付宝小程序二维码图片失败！");
        }
    }

    /**
     * 生成字节跳动小程序二维码
     */
    public String createBytedanceQRCode(String oemCode, Long width, String page, Integer type) throws BusinessException {
        // 读取微信小程序二维码相关配置 paramsType=8
        OemParamsEntity paramsEntity = this.oemParamsService.getParams(oemCode, 30);
        if (null == paramsEntity) {
            throw new BusinessException("未配置字节跳动小程序二维码相关信息！");
        }
        String appId = paramsEntity.getAccount();
        String appSecret = paramsEntity.getSecKey();
        return BytedanceUtils.getBytedanceQRCODE(appId,appSecret,width,page,type);
    }
}
