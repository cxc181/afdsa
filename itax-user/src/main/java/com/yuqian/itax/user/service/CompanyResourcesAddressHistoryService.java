package com.yuqian.itax.user.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.entity.CompanyResourcesAddressHistoryEntity;
import com.yuqian.itax.user.dao.CompanyResourcesAddressHistoryMapper;

import java.util.List;

/**
 * 企业资源所在地历史记录service
 * 
 * @Date: 2020年02月13日 15:35:51 
 * @author 蒋匿
 */
public interface CompanyResourcesAddressHistoryService extends IBaseService<CompanyResourcesAddressHistoryEntity,CompanyResourcesAddressHistoryMapper> {

    /**
     * 根据公司id查询企业资源所在地历史记录
     * @param companyId
     * @param oemCode
     * @return
     */
    List<CompanyResourcesAddressHistoryEntity> queryCompanyResourcesAddressHistoryList(Long companyId, String oemCode,Integer resourcesType);
}

