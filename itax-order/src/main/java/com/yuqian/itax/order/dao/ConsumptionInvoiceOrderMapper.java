package com.yuqian.itax.order.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.ConsumptionInvoiceOrderEntity;
import com.yuqian.itax.order.entity.query.ConsumptionInvoiceOrderQuery;
import com.yuqian.itax.order.entity.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消费开票订单dao
 *
 * @Date: 2020年09月27日 11:22:33 
 * @author 蒋匿
 */
@Mapper
public interface ConsumptionInvoiceOrderMapper extends BaseMapper<ConsumptionInvoiceOrderEntity> {
    /**
     *
     */
    List<ConsumptionInvoiceOrderVO> queryInvoiceList(ConsumptionInvoiceOrderQuery query);

    /**
     * 查询消费开票订单列表
     *
     * @param memberId
     * @param oemCode
     * @return List<InvoiceOrderVO>
     */
    List<ConsumptionInvoiceOrderPageVO> findConsumptionInvoiceOrderList(@Param("memberId") Long memberId, @Param("oemCode") String oemCode);

    /**
     * 查询消费开票订单详情
     *
     * @param memberId
     * @param oemCode
     * @param orderNo
     * @return ConsumptionInvoiceOrderDetailVO
     */
    ConsumptionInvoiceOrderDetailVO getDetailByOrderNo(@Param("memberId") Long memberId, @Param("oemCode") String oemCode,
                                                       @Param("orderNo") String orderNo);

    /**
     * 查询消费开票订单对应的消费订单列表
     *
     * @param memberId
     * @param oemCode
     * @param consumptionOrderRela
     * @return List<ConsumptionRelaOrderVO>
     */
    List<ConsumptionRelaOrderVO> findConsumptionRelaOrderList(@Param("memberId") Long memberId, @Param("oemCode") String oemCode,
                                                              @Param("consumptionOrderRela") String consumptionOrderRela);

    /**
     * 根据id获取收件信息
     * @param id
     * @return
     */
    ConsumptionInvoiceReceivingVO getReceivingInfoById(@Param("id") Long id);


    /**
     * 获取待签收的消费订单
     * @return
     */
    List<ConsumptionInvoiceOrderJobVO> listConsumptionInvoiceByStatus();

}

