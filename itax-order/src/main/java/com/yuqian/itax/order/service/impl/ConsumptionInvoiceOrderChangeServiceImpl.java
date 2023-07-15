package com.yuqian.itax.order.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.order.dao.ConsumptionInvoiceOrderChangeMapper;
import com.yuqian.itax.order.entity.ConsumptionInvoiceOrderChangeEntity;
import com.yuqian.itax.order.service.ConsumptionInvoiceOrderChangeService;
import org.springframework.stereotype.Service;


@Service("consumptionInvoiceOrderChangeService")
public class ConsumptionInvoiceOrderChangeServiceImpl extends BaseServiceImpl<ConsumptionInvoiceOrderChangeEntity,ConsumptionInvoiceOrderChangeMapper> implements ConsumptionInvoiceOrderChangeService {

    @Override
    public void add(ConsumptionInvoiceOrderChangeEntity consumptionInvoiceOrderChangeEntity) {
        mapper.insert(consumptionInvoiceOrderChangeEntity);
    }
}

