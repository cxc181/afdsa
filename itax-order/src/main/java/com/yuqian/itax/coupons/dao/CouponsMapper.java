package com.yuqian.itax.coupons.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.coupons.entity.CouponsEntity;
import com.yuqian.itax.coupons.entity.query.CouponQuery;
import com.yuqian.itax.coupons.entity.query.CouponsQuery;
import com.yuqian.itax.coupons.entity.vo.CouponVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 优惠卷表dao
 * 
 * @Date: 2021年04月08日 10:43:32 
 * @author 蒋匿
 */
@Mapper
public interface CouponsMapper extends BaseMapper<CouponsEntity> {

    List<CouponVO> queryCouponList(CouponQuery query);

    CouponVO queryCouponsByCode(CouponQuery query);

    List<CouponsEntity> queryOverTimeCouponsEntity();

    List<CouponsEntity> queryStartTimeCouponsEntity();


}

