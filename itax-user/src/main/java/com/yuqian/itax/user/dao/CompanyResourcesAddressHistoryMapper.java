package com.yuqian.itax.user.dao;

import com.yuqian.itax.user.entity.CompanyResourcesAddressHistoryEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.vo.CompanyResourcesAddressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业资源所在地历史记录dao
 * 
 * @Date: 2020年02月13日 15:35:51 
 * @author 蒋匿
 */
@Mapper
public interface CompanyResourcesAddressHistoryMapper extends BaseMapper<CompanyResourcesAddressHistoryEntity> {

    List<CompanyResourcesAddressHistoryEntity> queryCompanyResourcesAddressHistoryList(@Param("companyId") Long companyId, @Param("oemCode")String oemCode,@Param("resourcesType")Integer resourcesType);
}

