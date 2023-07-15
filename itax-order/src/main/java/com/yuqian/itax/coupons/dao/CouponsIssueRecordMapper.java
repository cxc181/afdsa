package com.yuqian.itax.coupons.dao;

import com.yuqian.itax.coupons.entity.CouponsIssueRecordEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.coupons.entity.query.CouponsIssueRecordQueryAdmin;
import com.yuqian.itax.coupons.entity.vo.CouponsIssueVO;
import com.yuqian.itax.coupons.entity.vo.CouponsIssueVOAdmin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 优惠卷发放记录表dao
 * 
 * @Date: 2021年04月08日 10:43:43 
 * @author 蒋匿
 */
@Mapper
public interface CouponsIssueRecordMapper extends BaseMapper<CouponsIssueRecordEntity> {

    /**
     * 根据用户id查询优惠券列表
     * @param memberId
     * @param oemCode
     * @param usableRange 可用范围，多个之间用户逗号分割 1-个体户注册 2-开票 3-注销 4-个体户续费
     * @param type 查询类型 1-可用优惠券 2-未使用优惠券 3-已失效优惠券
     * @return
     */
    List<CouponsIssueVO> listByMemberId(@Param("memberId") Long memberId, @Param("oemCode") String oemCode, @Param("usableRange") String usableRange, @Param("type") Integer type);

    /**
     * 用户可用优惠券张数
     * @param memberId
     * @param oemCode
     * @param usableRange 可用范围，多个之间用户逗号分割 1-个体户注册 2-开票 3-注销 4-个体户续费
     * @return
     */
    Integer countUsable(@Param("memberId") Long memberId, @Param("oemCode") String oemCode, @Param("usableRange") String usableRange);

    /**
     * 优惠券发放记录
     * @param query
     * @return
     */
    List<CouponsIssueVOAdmin> queryCouponIssueRecordList(CouponsIssueRecordQueryAdmin query);
    /**
     * 查询过去的优惠卷
     */
    List<CouponsIssueRecordEntity> queryOverTimeCouponsIssueRecordEntity();
    /**
     * 查询用户使用特定兑换码兑换次数
     * @param currUserId
     * @param oemCode
     * @param couponsId
     * @param exchangeCodeId
     * @return
     */
    int queryExchangeNumber(@Param("currUserId") Long currUserId, @Param("oemCode") String oemCode, @Param("couponsId") Long couponsId, @Param("exchangeCodeId") Long exchangeCodeId);
}

