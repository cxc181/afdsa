package com.yuqian.itax.user.dao;

import com.yuqian.itax.user.entity.CompanyTaxHostingEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 企业税务托管表dao
 * 
 * @Date: 2020年12月25日 11:38:54 
 * @author 蒋匿
 */
@Mapper
public interface CompanyTaxHostingMapper extends BaseMapper<CompanyTaxHostingEntity> {

    /**
     * 根据企业id查询税务托管
     * @param companyId
     * @return
     */
    CompanyTaxHostingEntity queryByCompanyId(@Param("companyId")Long companyId);
}

