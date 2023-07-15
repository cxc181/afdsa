package com.yuqian.itax.tax.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.tax.dao.CompanyTaxCostItemMapper;
import com.yuqian.itax.tax.entity.CompanyTaxCostItemEntity;
import com.yuqian.itax.tax.entity.vo.CompanyTaxCostVo;

import java.util.List;

/**
 * 企业税单成本项表service
 * 
 * @Date: 2021年12月13日 20:50:26 
 * @author 蒋匿
 */
public interface CompanyTaxCostItemService extends IBaseService<CompanyTaxCostItemEntity,CompanyTaxCostItemMapper> {

    /**
     * 根据企业税单id获取成本明细数据
     * @param companyTaxId
     * @return
     */
    CompanyTaxCostVo getCoseItemsByCompanyTaxId(Long companyTaxId);

    void insertAll(List<CompanyTaxCostItemEntity> costs);

    /**
     * 根据企业税单id删除数据
     * @param companyTaxId
     */
    void deleteByCompanyTaxId(Long companyTaxId);
}

