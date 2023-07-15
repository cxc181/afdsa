package com.yuqian.itax.agent.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.agent.entity.InvoiceInfoByOemEntity;
import com.yuqian.itax.agent.dao.InvoiceInfoByOemMapper;

/**
 * oem机构开票信息service
 * 
 * @Date: 2020年06月23日 09:29:36 
 * @author 蒋匿
 */
public interface InvoiceInfoByOemService extends IBaseService<InvoiceInfoByOemEntity,InvoiceInfoByOemMapper> {

    public InvoiceInfoByOemEntity queryInvoiceInfoByOemEntityByOemCode(String oemCode);
}

