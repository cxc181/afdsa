package com.yuqian.itax.wechat;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.message.entity.WechatMessageTemplateEntity;

/**
 * @author：pengwei
 * @Date：2020/6/8 16:19
 * @version：1.0
 */
public interface WeChatService {

    /**
     * 获取微信AccessToken
     * @param appId
     * @param appSecret
     * @return
     */
    String getAccessToken(String appId, String appSecret);

    /**
     * 获取微信AccessToken
     * @param url
     * @param appId
     * @param appSecret
     * @return
     */
    String getAccessToken(String url, String appId, String appSecret);

    /**
     * 下发订阅消息
     * @param templateEntity
     * @param openid
     * @param page
     * @param appId
     * @param appSecret
     * @return
     */
    String messageSubscribeSend(WechatMessageTemplateEntity templateEntity, String openid, String page, String appId, String appSecret);

    /**
     * 下发订阅消息
     * @param templateEntity
     * @param openid
     * @param page
     * @param url
     * @param appId
     * @param appSecret
     * @return
     */
    String messageSubscribeSend(WechatMessageTemplateEntity templateEntity, String openid, String page, String url, String appId, String appSecret);

    /**
     * 获取小程序二维码
     *
     * @param oemCode
     * @param scene
     * @param width
     * @param page
     * @param type       1-获取带data url的base64，2-获取不带data url的base64
     * @param sourceType 操作小程序来源 1-微信小程序 2-支付宝小程序 4-字节跳动小程序
     * @return
     * @Date 2019/12/18 14:39
     */
    String getQRCode(String oemCode, String scene, Long width, String page, Integer type, String sourceType) throws BusinessException;

    String getJsApiTicket(String oemCode) throws BusinessException;
}
