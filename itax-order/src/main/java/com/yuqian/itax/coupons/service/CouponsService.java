package com.yuqian.itax.coupons.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.coupons.dao.CouponsMapper;
import com.yuqian.itax.coupons.entity.CouponsEntity;
import com.yuqian.itax.coupons.entity.po.CouponPO;
import com.yuqian.itax.coupons.entity.query.CouponQuery;
import com.yuqian.itax.coupons.entity.vo.CouponVO;
import com.yuqian.itax.coupons.entity.vo.CouponsBatchIssueVO;

import java.text.ParseException;
import java.util.List;

/**
 * 优惠卷表service
 *
 * @author 蒋匿
 * @Date: 2021年04月08日 10:43:32
 */
public interface CouponsService extends IBaseService<CouponsEntity, CouponsMapper> {

    /**
     * 优惠券列表（分页）
     */
    PageInfo<CouponVO> queryCouponPageInfo(CouponQuery query);
    /**
     * 更具优惠卷编码查询
     */
    CouponVO queryCouponsByCode(CouponQuery query);
    /**
     * 优惠券列表
     */
    List<CouponVO> queryCouponList(CouponQuery query);
    /**
     * 新增
     */
    CouponsEntity add(CouponPO po,String account) throws ParseException;
    /**
     * 编辑
     */
    CouponsEntity update(CouponPO po,String account) throws ParseException;

    /**
     * 批量发放
     */
    List<CouponsBatchIssueVO> batchIssue(List<CouponsBatchIssueVO> list ,List<CouponsBatchIssueVO> failedMsg ,String account );
    /**
     * 查询出来已过期得数据
     */
    List<CouponsEntity> queryOverTimeCouponsEntity();
    /**
     * 查询出来已生效期得数据
     */
    List<CouponsEntity> queryStartTimeCouponsEntity();

    /**
     * 兑换优惠券
     * @param currUserId
     * @param exchangeCode
     * @return
     */
    void exchange(Long currUserId, String oemCode, String exchangeCode);
}

