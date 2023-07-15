package com.yuqian.itax.coupons.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.coupons.entity.CouponsIssueRecordEntity;
import com.yuqian.itax.coupons.dao.CouponsIssueRecordMapper;
import com.yuqian.itax.coupons.entity.query.CouponsIssueRecordQueryAdmin;
import com.yuqian.itax.coupons.entity.query.CouponsQuery;
import com.yuqian.itax.coupons.entity.vo.CouponsIssueVO;
import com.yuqian.itax.coupons.entity.vo.CouponsIssueVOAdmin;

import java.util.List;

/**
 * 优惠卷发放记录表service
 *
 * @Date: 2021年04月08日 10:43:43
 * @author 蒋匿
 */
public interface CouponsIssueRecordService extends IBaseService<CouponsIssueRecordEntity,CouponsIssueRecordMapper> {

    /**
     * 根据用户id查询优惠券列表
     * @param memberId
     * @param query
     * @return
     */
    PageInfo<CouponsIssueVO> listByMemberId(Long memberId, String oemCode, CouponsQuery query);

    /**
     * 用户可用优惠券张数
     * @param memberId
     * @param oemCode
     * @param usableRange 可用范围 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户
     * @return
     */
    Integer countUsable(Long memberId, String oemCode, String usableRange);

    /**
     * 优惠券发放记录（分页）
     */
    PageInfo<CouponsIssueVOAdmin> queryCouponIssueRecordPageInfo(CouponsIssueRecordQueryAdmin query);

    /**
     * 优惠券发放记录
     */
    List<CouponsIssueVOAdmin> queryCouponIssueRecordList(CouponsIssueRecordQueryAdmin query);
    /**
     * 查询已过期的优惠卷
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
    int getExchangeNumber(Long currUserId, String oemCode, Long couponsId, Long exchangeCodeId);
}

