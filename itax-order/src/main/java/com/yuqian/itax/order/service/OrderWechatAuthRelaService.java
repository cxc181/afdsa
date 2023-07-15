package com.yuqian.itax.order.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.entity.OrderWechatAuthRelaEntity;
import com.yuqian.itax.order.dao.OrderWechatAuthRelaMapper;
import com.yuqian.itax.user.entity.MemberAccountEntity;

import java.util.Map;

/**
 * 订单微信授权关系表service
 * 
 * @Date: 2021年03月16日 14:52:21 
 * @author 蒋匿
 */
public interface OrderWechatAuthRelaService extends IBaseService<OrderWechatAuthRelaEntity,OrderWechatAuthRelaMapper> {

    /**
     * 添加或者更新微信授权
     * @param orderNo 订单编号
     * @param templateType 微信模板类型 1-工单审核 2-邀请签名 3-签名确认结果
     * @param flag 授权状态 0-未授权 1-已授权
     * @param member
     * @param oemCode
     */
    void addOrUpdate(String orderNo, int templateType, Integer flag, MemberAccountEntity member, String oemCode);

    /**
     * 根据订单编号查询，模板类型，授权状态查询微信授权信息
     * @param orderNo 订单编号
     * @param templateType 微信模板类型 1-工单审核 2-邀请签名 3-签名确认结果
     * @param authStatus 授权状态 0-未授权 1-已授权
     * @return
     */
    OrderWechatAuthRelaEntity queryByOrderNo(String orderNo, Integer templateType, Integer authStatus);

    /**
     * 添加或者更新微信授权
     * @param orderNo 订单编号
     * @param templateType 微信模板类型 1-工单审核 2-邀请签名 3-签名确认结果
     * @param member
     * @param pageDictCode 微信通知小程序跳转页面字典配置key
     * @param args
     */
    String sendNotice(String orderNo, int templateType, MemberAccountEntity member, String pageDictCode, Map<String, Object> args);
}

