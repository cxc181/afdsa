package com.yuqian.itax.order.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.dao.InvoiceRecordDetailMapper;
import com.yuqian.itax.order.entity.InvoiceRecordDetailEntity;
import com.yuqian.itax.order.entity.vo.InvoiceDetailVO;
import com.yuqian.itax.order.entity.vo.InvoiceRecordDetailVO;

import java.util.List;
import java.util.Map;

/**
 * 开票明细表service
 * 
 * @Date: 2020年12月25日 11:42:18 
 * @author 蒋匿
 */
public interface InvoiceRecordDetailService extends IBaseService<InvoiceRecordDetailEntity,InvoiceRecordDetailMapper> {

    /**
     * 查询开票记录明细
     * @param invoiceRecordNo 开票记录编号
     * @param orderNo 开票订单号
     * @return
     */
    List<InvoiceRecordDetailVO> invoiceRecordDetailList(String invoiceRecordNo, String orderNo);

    /**
     * 根据订单编号查询
     * @param orderNo
     * @return InvoiceOrderDetailVO
     */
    List<InvoiceDetailVO> querryByOrderNo(String orderNo);

    /**
     * 电子发票pdf转成图片
     * @param entity
     */
    void invoicePdf2Img(InvoiceRecordDetailEntity entity);

    /**
     * 查询开票中的开票记录明细
     * @param invoiceRecordNo
     * @return
     */
    List<Map<String,Object>> queryDetailStatusNumByIng(String invoiceRecordNo);

    /**
     * 根据开票记录编号修改开票明细状态
     * @param invoiceRecordNo
     * @param status
     */
    void updateByInvoiceRecordNo(String invoiceRecordNo,Integer status);

    /**
     * 根据开票记录编号查询开票明细
     * @param invoiceRecordNo
     * @return
     */
    List<InvoiceRecordDetailEntity> findByInvoiceRecordNo(String invoiceRecordNo);
}

