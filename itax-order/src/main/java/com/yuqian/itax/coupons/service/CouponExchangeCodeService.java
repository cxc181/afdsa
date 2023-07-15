package com.yuqian.itax.coupons.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.coupons.dao.CouponExchangeCodeMapper;
import com.yuqian.itax.coupons.entity.CouponExchangeCodeEntity;
import com.yuqian.itax.coupons.entity.po.CouponExchangeCodePO;
import com.yuqian.itax.coupons.entity.query.CouponExchangeCodeQuery;
import com.yuqian.itax.coupons.entity.vo.CouponExchangeCodeVO;

import java.text.ParseException;
import java.util.List;

/**
 * 优惠券兑换码表service
 * 
 * @Date: 2021年06月04日 09:33:54 
 * @author 蒋匿
 */
public interface CouponExchangeCodeService extends IBaseService<CouponExchangeCodeEntity, CouponExchangeCodeMapper> {

    /**
     * 分页
     */
    PageInfo<CouponExchangeCodeVO> queryCouponExchangeCodePageInfo (CouponExchangeCodeQuery query);
    /**
     * 查询
     */
    List<CouponExchangeCodeVO> queryCouponExchangeCodeList (CouponExchangeCodeQuery query);
    /**
     * 新增
     */
    CouponExchangeCodeEntity add(CouponExchangeCodePO po, String account) throws ParseException;

    /**
     * 根据兑换码编码查看兑换码信息
     * @param exchangeCode
     * @return
     */
    CouponExchangeCodeEntity findByExchangeCode(String exchangeCode);
    /**
     * 编辑
     */
    CouponExchangeCodeEntity update(CouponExchangeCodePO po, String account) throws ParseException;
    /**
     * 查询出过期得兑换码
     */
    List<CouponExchangeCodeEntity> queryOverTimeCouponExchangeCodeEntity();
    /**
     * 查询出生效得兑换码
     */
    List<CouponExchangeCodeEntity> queryStartTimeCouponExchangeCodeEntity();
    /**
     * 根据优惠卷查询未作废的兑换码
     */
    List<CouponExchangeCodeEntity> queryCouponExchangeCodeByCouponsId(Long couponsId);
}

