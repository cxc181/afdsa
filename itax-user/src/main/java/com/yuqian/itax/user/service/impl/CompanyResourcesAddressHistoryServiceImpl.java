package com.yuqian.itax.user.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.user.dao.CompanyResourcesAddressHistoryMapper;
import com.yuqian.itax.user.entity.CompanyResourcesAddressHistoryEntity;
import com.yuqian.itax.user.service.CompanyResourcesAddressHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("companyResourcesAddressHistoryService")
public class CompanyResourcesAddressHistoryServiceImpl extends BaseServiceImpl<CompanyResourcesAddressHistoryEntity,CompanyResourcesAddressHistoryMapper> implements CompanyResourcesAddressHistoryService {

    @Override
    public List<CompanyResourcesAddressHistoryEntity> queryCompanyResourcesAddressHistoryList(Long companyId, String oemCode,Integer resourcesType) {
        return mapper.queryCompanyResourcesAddressHistoryList(companyId, oemCode, resourcesType);
    }
}

