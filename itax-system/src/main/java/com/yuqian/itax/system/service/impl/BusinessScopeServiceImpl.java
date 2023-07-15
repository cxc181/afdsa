package com.yuqian.itax.system.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.system.dao.BusinessScopeMapper;
import com.yuqian.itax.system.entity.BusinessScopeEntity;
import com.yuqian.itax.system.service.BusinessScopeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service("businessScopeService")
public class BusinessScopeServiceImpl extends BaseServiceImpl<BusinessScopeEntity,BusinessScopeMapper> implements BusinessScopeService {
    @Resource
    private BusinessScopeMapper businessScopeMapper;

    @Override
    public List<BusinessScopeEntity> listBusinessScope(Long industryId) {
        return this.businessScopeMapper.listBusinessScope(industryId);
    }

    @Override
    public void delByIndustryId(Long industryId) {
        mapper.delByIndustryId(industryId);
    }

    @Override
    public List<BusinessScopeEntity> getScopeByBusinessContent(String content, Long industryId) {
        return mapper.getScopeByBusinessContent(content, industryId);
    }

    @Override
    public List<BusinessScopeEntity> getScopeByBusinessByindustryIds(String content, List<Long> industryId) {
        return mapper.getScopeByBusinessByindustryIds(content,industryId);
    }

    @Override
    public List<BusinessScopeEntity> getScopeByBusinessByParkId(Long parkId,String content) {
        return mapper.getScopeByBusinessByParkId(parkId,content);
    }

    @Override
    public void deleteScopeBusinessByContentAndParkId(Long parkId, String content) {
        mapper.deleteScopeBusinessByContentAndParkId(parkId,content);
    }
}

