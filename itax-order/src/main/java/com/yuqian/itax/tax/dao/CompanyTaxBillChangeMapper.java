package com.yuqian.itax.tax.dao;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.tax.entity.CompanyTaxBillChangeEntity;
import com.yuqian.itax.tax.entity.vo.CompanyTaxBillChangeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业税单变更表dao
 * 
 * @Date: 2022年04月08日 11:43:57 
 * @author 蒋匿
 */
@Mapper
public interface CompanyTaxBillChangeMapper extends BaseMapper<CompanyTaxBillChangeEntity> {

    /**
     * 根据企业税单id获取历史数据
     * @param companyTaxBillId
     * @return
     */
    List<CompanyTaxBillChangeVO> getCompanyTaxBillChange(@Param("companyTaxBillId") Long companyTaxBillId);
	
}

