package com.yuqian.itax.system.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.BusinessScopeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 经营范围dao
 * 
 * @Date: 2019年12月08日 20:38:37 
 * @author 蒋匿
 */
@Mapper
public interface BusinessScopeMapper extends BaseMapper<BusinessScopeEntity> {
    /**
     * @Description 根据行业ID查询经营范围列表
     * @Author  Kaven
     * @Date   2019/12/11 18:00
     * @Param  industryId
     * @Return List
    */
    List<BusinessScopeEntity> listBusinessScope(Long industryId);

    /**
     * 根据行业id删除经营范围
     * @param industryId
     */
    void delByIndustryId(@Param("industryId") Long industryId);

    /**
     * 根据经营范围基础库里的经营范围去查找经营范围表中是否存在该经营范围
     * @param content
     * @return
     */
    List<BusinessScopeEntity> getScopeByBusinessContent(@Param("content") String content, @Param("industryId") Long industryId);

    /**
     * 根据行业id集合和经营范围查找数据
     * @param content
     * @param industryId
     * @return
     */
    List<BusinessScopeEntity> getScopeByBusinessByindustryIds(@Param("content") String content, @Param("industryIds") List<Long> industryId);

    /**
     * 根据园区id获取园区行业经营范围
     * @param parkId
     * @return
     */
    List<BusinessScopeEntity> getScopeByBusinessByParkId(@Param("parkId") Long parkId,@Param("content") String content);

    /**
     * 删除行业经营范围
     * @param parkId
     * @param content
     */
    void deleteScopeBusinessByContentAndParkId(@Param("parkId") Long parkId,@Param("content") String content);
}

