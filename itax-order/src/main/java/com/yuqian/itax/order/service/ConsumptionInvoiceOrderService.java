package com.yuqian.itax.order.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.dao.ConsumptionInvoiceOrderMapper;
import com.yuqian.itax.order.entity.ConsumptionInvoiceOrderEntity;
import com.yuqian.itax.order.entity.dto.ConsumptionInvOrderDTO;
import com.yuqian.itax.order.entity.query.ConsumptionInvoiceOrderQuery;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.user.entity.InvoiceHeadEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 消费开票订单service
 *
 * @author 蒋匿
 * @Date: 2020年09月27日 11:22:33
 */
public interface ConsumptionInvoiceOrderService extends IBaseService<ConsumptionInvoiceOrderEntity, ConsumptionInvoiceOrderMapper> {

    /**
     * 消费发票申请查询(分页)
     * @param query
     * @return
     */
    public PageInfo<ConsumptionInvoiceOrderVO> invoicePage(ConsumptionInvoiceOrderQuery query);

    /**
     * 消费发票申请查询
     * @param query
     * @return
     */
    public List<ConsumptionInvoiceOrderVO> invoiceList(ConsumptionInvoiceOrderQuery query);

    /**
     * @Description 创建消费开票订单
     * @Author  Kaven
     * @Date   2020/9/27 14:12
     * @Param   ConsumptionInvOrderDTO
     * @Return  String
     * @Exception  BusinessException
    */
    String createOrder(ConsumptionInvOrderDTO dto) throws BusinessException;

    /**
     * @Description 消费开票订单入库
     * @Author  Kaven
     * @Date   2020/9/27 14:47
     * @Param   ConsumptionInvOrderDTO MemberAccountEntity InvoiceHeadEntity
     * @Return String
     * @Exception BusinessException
     */
    String createConsumptionInvOrder(ConsumptionInvOrderDTO dto, MemberAccountEntity member, InvoiceHeadEntity invoiceHead, MemberAddressEntity memberAddress) throws BusinessException;

    /**
     * 消费开票创建开票金额
     * @param orderNo
     * @param invoiceAmount
     * @param addUser
     */
    void createConsumptionInvoiceRecord(String orderNo,Long invoiceAmount,String addUser);
    /**
     * 立即出票
     *
     * @param id
     */
    void applyInvoice(Long id, String account);


    /**
     * 出票失败
     *
     * @param id
     */
    void applyInvoiceFail(Long id, String account,String remark);

    /**
     * 查询消费开票订单列表
     *
     * @param memberId
     * @param oemCode
     * @return List<ConsumptionInvoiceOrderPageVO>
     */
    List<ConsumptionInvoiceOrderPageVO> findConsumptionInvoiceOrderList(Long memberId, String oemCode) throws BusinessException;

    /**
     * 查询消费开票订单详情
     *
     * @param memberId
     * @param oemCode
     * @param orderNo
     * @return ConsumptionInvoiceOrderDetailVO
     */
    ConsumptionInvoiceOrderDetailVO getDetailByOrderNo(Long memberId, String oemCode, String orderNo) throws BusinessException;

    /**
     * 查询消费开票订单对应的消费订单列表
     *
     * @param memberId
     * @param oemCode
     * @param consumptionOrderRela
     * @return List<ConsumptionRelaOrderVO>
     */
    List<ConsumptionRelaOrderVO> findConsumptionRelaOrderList(Long memberId, String oemCode, String consumptionOrderRela) throws BusinessException;

    /**
     * 查看物流
     * @param orderNo
     * @param currUserId
     * @return
     */
    Map<String, Object> checkTheLogistics(String orderNo, Long currUserId);

    /**
     * 根据id获取收件信息
     * @param id
     * @return
     */
    ConsumptionInvoiceReceivingVO getReceivingInfoById(Long id);

    /**
     * 根据消费发票订单状态
     * @param id
     * @param courierNumber
     * @param courierCompanyName
     * @param userName
     */
    void updateConsumptionInvoiceOrder(Long id,String courierNumber,String courierCompanyName,String userName);

    /**
     * 获取待签收的消费订单
     * @return
     */
    List<ConsumptionInvoiceOrderJobVO> listConsumptionInvoiceByStatus();
}

