package com.yuqian.itax.tax.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.tax.dao.CompanyTaxBillChangeMapper;
import com.yuqian.itax.tax.entity.CompanyTaxBillChangeEntity;
import com.yuqian.itax.tax.entity.query.CompanyTaxBillChangeQuery;
import com.yuqian.itax.tax.entity.vo.CompanyTaxBillChangeVO;

/**
 * 企业税单变更表service
 * 
 * @Date: 2022年04月08日 11:43:57 
 * @author 蒋匿
 */
public interface CompanyTaxBillChangeService extends IBaseService<CompanyTaxBillChangeEntity,CompanyTaxBillChangeMapper> {

    /**
     * 根据企业税单id获取历史数据
     * @param
     * @return
     */
    PageInfo<CompanyTaxBillChangeVO> getCompanyTaxBillChange(CompanyTaxBillChangeQuery query);
	
}

