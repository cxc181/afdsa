package com.yuqian.itax.order.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.entity.ConsumptionInvoiceOrderChangeEntity;
import com.yuqian.itax.order.dao.ConsumptionInvoiceOrderChangeMapper;

/**
 * 消费开票订单变更记录表service
 * 
 * @Date: 2020年09月27日 11:22:42 
 * @author 蒋匿
 */
public interface ConsumptionInvoiceOrderChangeService extends IBaseService<ConsumptionInvoiceOrderChangeEntity,ConsumptionInvoiceOrderChangeMapper> {

    /**
     * 新增变更记录
     */
    void add(ConsumptionInvoiceOrderChangeEntity consumptionInvoiceOrderChangeEntity);
}

