package com.yuqian.itax.order.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.dao.InvoiceServiceFeeDetailMapper;
import com.yuqian.itax.order.entity.InvoiceServiceFeeDetailEntity;
import com.yuqian.itax.order.entity.vo.InvoiceServiceFeeDetailVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 服务费收费阶段明细service
 * 
 * @Date: 2021年03月16日 14:51:12 
 * @author 蒋匿
 */
public interface InvoiceServiceFeeDetailService extends IBaseService<InvoiceServiceFeeDetailEntity,InvoiceServiceFeeDetailMapper> {

    /**
     * 计算服务费明细
     * @return
     */
    List<InvoiceServiceFeeDetailVO> countServiceDetail(Long memberId, Long companyId, Long productId, Long invoiceAmount,Long periodInvoiceAmount, String oemCode);

    /**
     * 特价活动服务费明细
     * @return
     */
    List<InvoiceServiceFeeDetailVO> countServiceDetail(Long memberId, Long industryId, Long parkId,Integer productType,Long invoiceAmount, Long periodInvoiceAmount, String oemCode);

    /**
     * 根据订单编号获取服务费明细
     * @param orderNo
     * @return
     */
    List<InvoiceServiceFeeDetailVO> findDetailByOrderNo(String orderNo);

    /**
     * 查询订单最小阶梯服务费率
     */
    BigDecimal findLeastServiceFeeRate(String orderNo);
}

