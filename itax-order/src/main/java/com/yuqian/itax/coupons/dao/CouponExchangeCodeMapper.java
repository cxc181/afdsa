package com.yuqian.itax.coupons.dao;

import com.yuqian.itax.coupons.entity.CouponExchangeCodeEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.coupons.entity.query.CouponExchangeCodeQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 优惠券兑换码表dao
 * 
 * @Date: 2021年06月04日 09:33:54 
 * @author 蒋匿
 */
@Mapper
public interface CouponExchangeCodeMapper extends BaseMapper<CouponExchangeCodeEntity> {

    /**
     * 列表
     */
    List queryCouponExchangeCodeList(CouponExchangeCodeQuery query);

    /**
     * 根据兑换码编码查看兑换码信息
     * @param exchangeCode
     * @return
     */
    CouponExchangeCodeEntity queryByExchangeCode(@Param("exchangeCode") String exchangeCode);

    List<CouponExchangeCodeEntity> queryOverTimeCouponExchangeCodeEntity();

    List<CouponExchangeCodeEntity> queryStartTimeCouponExchangeCodeEntity();


    List<CouponExchangeCodeEntity> queryCouponExchangeCodeByCouponsId(Long couponsId);


}

