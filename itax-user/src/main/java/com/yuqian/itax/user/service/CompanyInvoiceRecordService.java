package com.yuqian.itax.user.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.dao.CompanyInvoiceRecordMapper;
import com.yuqian.itax.user.entity.CompanyInvoiceRecordEntity;

/**
 * 企业开票记录service
 * 
 * @Date: 2019年12月10日 11:35:24 
 * @author 蒋匿
 */
public interface CompanyInvoiceRecordService extends IBaseService<CompanyInvoiceRecordEntity,CompanyInvoiceRecordMapper> {

    /**
     * 根据企业id查询企业所有已开票额度
     * @return
     */
    Long sumUseInvoiceAmount(Long companyId);
}

