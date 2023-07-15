package com.yuqian.itax.order.dao;

import com.yuqian.itax.order.entity.OrderWechatAuthRelaEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单微信授权关系表dao
 * 
 * @Date: 2021年03月16日 14:52:21 
 * @author 蒋匿
 */
@Mapper
public interface OrderWechatAuthRelaMapper extends BaseMapper<OrderWechatAuthRelaEntity> {

    /**
     * 根据订单编号查询，模板类型，授权状态查询微信授权信息
     * @param orderNo 订单编号
     * @param wechatTmplType 微信模板类型 1-工单审核 2-邀请签名 3-签名确认结果
     * @param authStatus 授权状态 0-未授权 1-已授权
     * @return
     */
    OrderWechatAuthRelaEntity queryByOrderNo(@Param("orderNo") String orderNo, @Param("wechatTmplType") Integer wechatTmplType, @Param("authStatus") Integer authStatus);
}

