package com.yuqian.itax.user.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.user.dao.CompanyInvoiceRecordMapper;
import com.yuqian.itax.user.entity.CompanyInvoiceRecordEntity;
import com.yuqian.itax.user.service.CompanyInvoiceRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("companyInvoiceRecordService")
public class CompanyInvoiceRecordServiceImpl extends BaseServiceImpl<CompanyInvoiceRecordEntity,CompanyInvoiceRecordMapper> implements CompanyInvoiceRecordService {
    @Resource
    private CompanyInvoiceRecordMapper companyInvoiceRecordMapper;

    /**
     * 根据企业id查询企业所有已开票额度
     * @return
     */
    @Override
    public Long sumUseInvoiceAmount(Long companyId) {
        return companyInvoiceRecordMapper.sumUseInvoiceAmount(companyId);
    }
}

