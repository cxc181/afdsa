package com.yuqian.itax.order.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.order.dao.InvoiceOrderChangeRecordMapper;
import com.yuqian.itax.order.entity.InvoiceOrderChangeRecordEntity;
import com.yuqian.itax.order.service.InvoiceOrderChangeRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 开票订单变更记录service impl
 *
 * @Date: 2019年12月07日 19:54:14
 * @author yejian
 */
@Service("invoiceOrderChangeRecordService")
public class InvoiceOrderChangeRecordServiceImpl extends BaseServiceImpl<InvoiceOrderChangeRecordEntity,InvoiceOrderChangeRecordMapper> implements InvoiceOrderChangeRecordService {

    @Resource
    private InvoiceOrderChangeRecordMapper invoiceOrderChangeRecordMapper;

    @Override
    public InvoiceOrderChangeRecordEntity queryByOrderNo(String orderNo) {
        return invoiceOrderChangeRecordMapper.queryByOrderNo(orderNo);
    }
}

