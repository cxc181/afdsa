package com.yuqian.itax.order.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.entity.InvoiceOrderChangeRecordEntity;
import com.yuqian.itax.order.dao.InvoiceOrderChangeRecordMapper;
import com.yuqian.itax.order.entity.OrderEntity;

/**
 * 开票订单变更记录service
 * 
 * @Date: 2019年12月07日 19:54:14 
 * @author yejian
 */
public interface InvoiceOrderChangeRecordService extends IBaseService<InvoiceOrderChangeRecordEntity,InvoiceOrderChangeRecordMapper> {
    /**
     * @Description 根据订单号查询订单
     * @Author  yejian
     * @Date   2019/12/11 16:55
     * @Param  orderNo
     * @Return InvoiceOrderChangeRecordEntity
     */
    InvoiceOrderChangeRecordEntity queryByOrderNo(String orderNo);
}

