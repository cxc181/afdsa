package com.yuqian.itax.tax.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.tax.dao.CompanyTaxBillChangeMapper;
import com.yuqian.itax.tax.entity.CompanyTaxBillChangeEntity;
import com.yuqian.itax.tax.entity.query.CompanyTaxBillChangeQuery;
import com.yuqian.itax.tax.entity.vo.CompanyTaxBillChangeVO;
import com.yuqian.itax.tax.service.CompanyTaxBillChangeService;
import org.springframework.stereotype.Service;


@Service("companyTaxBillChangeService")
public class CompanyTaxBillChangeServiceImpl extends BaseServiceImpl<CompanyTaxBillChangeEntity,CompanyTaxBillChangeMapper> implements CompanyTaxBillChangeService {

    @Override
    public PageInfo<CompanyTaxBillChangeVO> getCompanyTaxBillChange(CompanyTaxBillChangeQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo<>(this.mapper.getCompanyTaxBillChange(query.getCompanyTaxBillId()));
    }
}

