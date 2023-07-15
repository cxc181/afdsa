package com.yuqian.itax.pay.dao;

import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.pay.entity.query.PayWaterQuery;
import com.yuqian.itax.pay.entity.query.WthdrawQuery;
import com.yuqian.itax.pay.entity.vo.PaywaterVO;
import com.yuqian.itax.pay.entity.vo.WithdrawVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 支付流水dao
 * 
 * @Date: 2019年12月07日 20:13:40 
 * @author 蒋匿
 */
@Mapper
public interface PayWaterMapper extends BaseMapper<PayWaterEntity> {


    List<WithdrawVO> getWithdrawPayWaterList(WthdrawQuery wthdrawQuery);

    /**
     * @Description 更新支付流水信息
     * @Author  Kaven
     * @Date   2019/12/12 16:09
     * @Param  orderPay
     */
    void updatePayWater(PayWaterEntity orderPay);

    /**
     * 提现记录列表
     * @param query
     * @return
     */
    List<PaywaterVO> listPayWater(PayWaterQuery query);
    /**
     * 支付流水记录列表
     */
    List<PaywaterVO> getPayWaterList(PayWaterQuery query);

    /**
     * @Description 查询支付中状态的微信支付订单流水
     * @Author  Kaven
     * @Date   2020/1/16 11:06
     * @Param  payWay
     * @Return List
     * @Exception
    */
    List<PayWaterEntity> selectPayingList(@Param("payWay") Integer payWay);
    /**
     * @Description 查询支付中状态的微信退款订单流水
     * @Author  Kaven
     * @Date   2020/1/16 11:06
     * @Param  payWay
     * @Return List
     * @Exception
     */
    List<PayWaterEntity> selectRefundPayingList(@Param("payWay") Integer payWay, @Param("orderBy") Integer orderBy);
    /**
     * @Description 根据订单号更新流水状态
     * @Author  Kaven
     * @Date   2020/3/5 9:31
     * @Param  water
     * @Return
     * @Exception
    */
    void updatePayStatus(PayWaterEntity water);


    List<Map<String,Object>> batchDownIdCard(WthdrawQuery query);

    List<PayWaterEntity> queryPayWaterListByOrderNoAndStaus(@Param("orderNo")String orderNo);

    /**
     * 根据订单编号查询订单类型为补税并支付状态为支付成功的流水
     * @param orderNo
     * @return
     */
    PayWaterEntity getPayWaterByOrderNo(String orderNo);
}

