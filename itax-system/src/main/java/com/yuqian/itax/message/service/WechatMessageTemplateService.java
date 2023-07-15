package com.yuqian.itax.message.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.message.dao.WechatMessageTemplateMapper;
import com.yuqian.itax.message.entity.WechatMessageTemplateEntity;
import com.yuqian.itax.message.entity.vo.WechatMessageTemplateSimpleVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 微信订阅消息模板配置service
 * 
 * @Date: 2020年06月04日 16:39:53 
 * @author 蒋匿
 */
public interface WechatMessageTemplateService extends IBaseService<WechatMessageTemplateEntity,WechatMessageTemplateMapper> {

    /**
     * 根据模板类型和机构编码查询微信订阅消息模板
     * @param templateType 模板类型 1-工商开户审核 2-邀请签名 3-签名确认结果
     * @param oemCode
     * @return
     */
    WechatMessageTemplateEntity getByTemplateType(Integer templateType, String oemCode);

    /**
     * 根据模板类型和机构编码查询微信订阅消息模板
     * @param templateTypes 模板类型 1-工商开户审核 2-邀请签名 3-签名确认结果
     * @param oemCode
     * @return
     */
    List<WechatMessageTemplateSimpleVO> getByTemplateTypes(int[] templateTypes, String oemCode);

    /**
     * 发送微信消息通知
     * @param openid
     * @param page
     * @param templateType
     * @param oemCode
     * @param args
     * @return true 发送成功
     */
    String sendNotice(String openid, String page, Integer templateType, String oemCode, Map<String, Object> args);

    /**
     * 构建开户审核结果微信通知业务参数
     * @param orderNo 订单编号
     * @param workOrderUpdateTime 时间
     * @param workOrderStatus 中文状态，如：（审核成功，已发货）
     * @param remark 备注信息
     * @return
     */
    Map<String, Object> getRegAuditNoticeMap(String orderNo, Date workOrderUpdateTime, String workOrderStatus, String remark);
}

