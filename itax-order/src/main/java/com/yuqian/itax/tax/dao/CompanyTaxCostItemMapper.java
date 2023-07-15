package com.yuqian.itax.tax.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.tax.entity.CompanyTaxCostItemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 企业税单成本项表dao
 * 
 * @Date: 2021年12月13日 20:50:26 
 * @author 蒋匿
 */
@Mapper
public interface CompanyTaxCostItemMapper extends BaseMapper<CompanyTaxCostItemEntity> {

    /**
     * 根据企业税单id删除数据
     * @param companyTaxId
     */
    void deleteByCompanyTaxId(@Param("companyTaxId") Long companyTaxId);
	
}

