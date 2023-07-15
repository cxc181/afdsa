package com.yuqian.itax.agent.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.agent.dao.InvoiceInfoByOemMapper;
import com.yuqian.itax.agent.entity.InvoiceInfoByOemEntity;
import com.yuqian.itax.agent.service.InvoiceInfoByOemService;
import org.springframework.stereotype.Service;


@Service("invoiceInfoByOemService")
public class InvoiceInfoByOemServiceImpl extends BaseServiceImpl<InvoiceInfoByOemEntity,InvoiceInfoByOemMapper> implements InvoiceInfoByOemService {

    @Override
    public InvoiceInfoByOemEntity queryInvoiceInfoByOemEntityByOemCode(String oemCode) {
        return mapper.queryInvoiceInfoByOemEntityByOemCode(oemCode);
    }
}

