package com.yuqian.itax.system.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.system.dao.BusinessScopeMapper;
import com.yuqian.itax.system.entity.BusinessScopeEntity;

import java.util.List;

/**
 * 经营范围service
 * 
 * @Date: 2019年12月08日 20:38:37 
 * @author 蒋匿
 */
public interface BusinessScopeService extends IBaseService<BusinessScopeEntity,BusinessScopeMapper> {
    /**
     * @Description 根据行业ID获取经营范围列表
     * @Author  Kaven
     * @Date   2019/12/11 17:58
     * @Param  industryId
     * @Return
    */
    List<BusinessScopeEntity> listBusinessScope(Long industryId);

    /**
     * 根据行业id删除经营范围
     * @param industryId
     */
    void delByIndustryId(Long industryId);

    /**
     * 根据经营范围基础库里的经营范围去查找经营范围表中是否存在该经营范围
     * @param content
     * @return
     */
    List<BusinessScopeEntity> getScopeByBusinessContent(String content, Long industryId);

    /**
     * 根据行业id集合和经营范围查找数据
     * @param content
     * @param industryId
     * @return
     */
    List<BusinessScopeEntity> getScopeByBusinessByindustryIds(String content, List<Long> industryId);

    /**
     * 根据园区id获取园区行业经营范围
     * @param parkId
     * @return
     */
    List<BusinessScopeEntity> getScopeByBusinessByParkId(Long parkId,String content);

    /**
     * 删除行业经营范围
     * @param parkId
     * @param content
     */
    void deleteScopeBusinessByContentAndParkId(Long parkId,String content);
}

