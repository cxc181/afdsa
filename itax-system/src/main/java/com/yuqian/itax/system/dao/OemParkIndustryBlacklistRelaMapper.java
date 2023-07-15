package com.yuqian.itax.system.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.system.entity.OemParkIndustryBlacklistRelaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * oem机构园区行业黑名单dao
 * 
 * @Date: 2020年08月07日 10:38:39 
 * @author 蒋匿
 */
@Mapper
public interface OemParkIndustryBlacklistRelaMapper extends BaseMapper<OemParkIndustryBlacklistRelaEntity> {

    /**
     * 批量添加园区规则例外表
     * @param entity
     * @param industryIds
     */
    void addBatch(@Param("entity")OemParkIndustryBlacklistRelaEntity entity, @Param("industryIds") List<Long> industryIds);

    /**
     * 删除OEM机构不在当前当前园区的行业黑名单
     * @param oemCode
     * @param parkIdList
     */
    void deleteByParkIds(@Param("oemCode")String oemCode, @Param("parkIdList")List<Long> parkIdList);
}

