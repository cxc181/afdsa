package com.yuqian.itax.user.dao;

import com.yuqian.itax.user.entity.CompanyResourcesAddressEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.vo.CompanyResourcesAddressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业资源所在地管理dao
 * 
 * @Date: 2019年12月14日 13:56:31 
 * @author 蒋匿
 */
@Mapper
public interface CompanyResourcesAddressMapper extends BaseMapper<CompanyResourcesAddressEntity> {

    /**
     * 根据公司id查询企业资源所在地
     * @param companyId
     * @param oemCode
     * @return
     */
    List<CompanyResourcesAddressEntity> listCompanyResourcesAddress(@Param("companyId")Long companyId, @Param("oemCode")String oemCode);


    CompanyResourcesAddressVO queryCompanyResourcesAddressDetail(@Param("id")Long id);

}

